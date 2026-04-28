package com.islandstudio.xenon.shared.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import com.akuleshov7.ktoml.tree.nodes.TomlFile
import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.TomlTable
import com.akuleshov7.ktoml.tree.nodes.pairs.values.*
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.islandstudio.xenon.shared.exception.TomlExceptionMessage
import com.islandstudio.xenon.shared.exception.XenonException
import com.islandstudio.xenon.shared.io.DataDirectory
import com.islandstudio.xenon.shared.io.ExternalResource
import com.islandstudio.xenon.shared.utils.data.DataType
import com.islandstudio.xenon.shared.utils.data.DataUtil
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType

class CoreConfig<T> private constructor(
    val configCodec: BuilderCodec<T>,
    private val configSections: List<BaseConfigSection>,
    private val tomlInstance: Toml,
    configResource: ExternalResource
) {
    class Builder<T>(private val configCodec: BuilderCodec<T>) {
        private var inputOption: TomlInputConfig = TomlInputConfig(true, allowEscapedQuotesInLiteralStrings = true)
        private var outputOption: TomlOutputConfig = TomlOutputConfig(TomlIndentation.TWO_SPACES)

        fun withInputOption(inputOption: TomlInputConfig): Builder<T> {
            this.inputOption = inputOption
            return this
        }

        fun withOutputOption(outputOption: TomlOutputConfig): Builder<T> {
            this.outputOption = outputOption
            return this
        }

        fun build(configSections: List<BaseConfigSection>, configResource: ExternalResource): CoreConfig<T> {
            return CoreConfig(configCodec, configSections, Toml(inputOption, outputOption), configResource)
        }
    }

    private val configFile = DataDirectory.createOrGetFile(configResource.resourceFolder, configResource.resourceName)
    private var configContext: T

    init {
        /* 1. Config Object -> Toml String (no comments)
        *  2. Toml String -> Toml File
        *  3. Update Config & Add Comments
        * */
        val loadedConfigAsTomlString = loadConfig()

        configContext = decodeToConfigObject(loadedConfigAsTomlString)

        internalSaveToFile(loadedConfigAsTomlString)
    }

    private fun loadConfig(): String {
        val isLoadAsNew: Boolean
        val loadedTomlString = if (configFile.length() == 0L) {
            isLoadAsNew = true
            encodeToString(configCodec.defaultValue)
        } else {
            isLoadAsNew = false
            configFile.reader().use { it.readText() }
        }
        val loadedTomlFile = with(decodeToTomFile(loadedTomlString)) {
            val updatedTomlFile = if (isLoadAsNew) this else updateConfig(this)

            return@with addComments(updatedTomlFile)
        }

        return encodeToString(loadedTomlFile)
    }

    fun getContext(): T {
        return configContext
    }

    fun reloadConfig() {
        val loadedConfigAsTomlString = loadConfig()

        configContext = decodeToConfigObject(loadedConfigAsTomlString)
    }

    private fun encodeToString(tomlFile: TomlFile): String {
        return tomlInstance.tomlWriter.writeToString(tomlFile)
    }

    private fun encodeToString(configObject: T): String {
        return tomlInstance.encodeToString(serializer(configCodec.defaultValue::class.createType()), configObject)
    }

    private fun decodeToTomFile(tomlString: String): TomlFile {
        var tomlStringList = tomlString.split("\n").toMutableList()

        while (true) {
            val parseResult = runCatching {
                tomlInstance.tomlParser.parseLines(tomlStringList.asSequence())
            }.onSuccess {
                return it
            }

            tomlStringList = tryResolveParseError(tomlStringList, parseResult)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun decodeToConfigObject(tomlString: String): T {
        return tomlInstance.decodeFromString(serializer(configCodec.defaultValue::class.createType()), tomlString) as T
    }

    private fun internalSaveToFile(tomlString: String) {
        configFile.writeText(tomlString)
    }

    fun saveConfig() {
        val tomlFile = decodeToTomFile(encodeToString(configContext)).run {
            addComments(this)
        }

        val tomlString = encodeToString(tomlFile)

        internalSaveToFile(tomlString)
    }

    private fun addComments(tomlFile: TomlFile): TomlFile {
        configSections.forEach { configSection ->
            val tomlNode = if (configSection.sectionKey == BaseConfigSection.ROOT_NODE_KEY) {
                tomlFile
            } else {
                tomlFile.getRealTomlTables().find { it.fullTableKey.toString() == configSection.sectionKey }
            }

            tomlNode?.let { node ->
                val configEntries = configSection.getAllConfigEntry()

                node.comments.add(configSection.description)

                node.children.forEach { tomlNodeChild ->
                    val configEntry = configEntries.find { it.entryKey == tomlNodeChild.name } ?: return@forEach

                    tomlNodeChild.comments.add(configEntry.description)
                }
            }
        }

        return tomlFile
    }

    private fun updateConfig(tomlFile: TomlFile): TomlFile {
        val updatedConfig = decodeToTomFile(encodeToString(configCodec.defaultValue))
        val flattenTomlNodes = flattenTomlNode(tomlFile)

        flattenTomlNodes.forEach { tomlNode ->
            val tomlNodeParent = tomlNode.parent ?: return@forEach
            val tomlNodeParentName: String

            val tomlKeyValue = when (tomlNodeParent) {
                is TomlTable -> {
                    val tomlTable = updatedConfig.getAllChildTomlTables().find {
                        it.fullTableKey.toString() == tomlNodeParent.fullTableKey.toString()
                    } ?: return@forEach

                    tomlNodeParentName = tomlTable.fullTableKey.toString()

                    tomlTable.children.find {
                        it.name == tomlNode.name
                    }?.let { DataUtil.asType<TomlKeyValuePrimitive>(it) } ?: return@forEach
                }

                is TomlFile -> {
                    tomlNodeParentName = BaseConfigSection.ROOT_NODE_KEY

                    updatedConfig.children.find {
                        it.name == tomlNode.name
                    }?.let { DataUtil.asType<TomlKeyValuePrimitive>(it) } ?: return@forEach
                }

                else -> return@forEach
            }

            val configSection = configSections.find {
                tomlNodeParentName == it.sectionKey
            } ?: return@forEach

            tomlKeyValue.value.content = DataUtil.asType<TomlKeyValuePrimitive>(tomlNode).value.content

            /* Config value validation */
            if (!tryResolveConfigValue(tomlKeyValue, configSection)) return@forEach
        }

        return updatedConfig
    }

    /**
     * Flatten toml node. (Excluded TomlFile (RootNode) and TomlTable)
     *
     * @param rootTomlNode
     * @return
     */
    private fun flattenTomlNode(rootTomlNode: TomlFile): MutableList<TomlNode> {
        val tomlNodes: MutableList<TomlNode> = mutableListOf(rootTomlNode)

        while (tomlNodes.any {x -> x is TomlTable || x is TomlFile }) {
            tomlNodes.toTypedArray().forEach {
                if (it is TomlTable || it is TomlFile) {
                    tomlNodes.remove(it)
                    tomlNodes.addAll(it.children)

                    return@forEach
                }

                if (tomlNodes.contains(it)) return@forEach

                tomlNodes.add(it)
            }
        }

        return tomlNodes
    }

    private fun tryResolveConfigValue(tomlKeyValue: TomlKeyValuePrimitive, configSection: BaseConfigSection): Boolean {
        val configEntry = configSection.getAllConfigEntry().find {
            it.entryKey == tomlKeyValue.name
        } ?: return false

        val configValue = tomlKeyValue.value.content
        val configValueDataType = getDataType(tomlKeyValue.value)
        val configValueDataRange = configEntry.dataRange

        /* Validate and resolve data type if mismatch data type */
        if (configValueDataType != configEntry.dataType) {
            val newConfigValue = DataUtil.toDataType(configValue, configEntry.dataType)
                ?: configEntry.defaultValue
                ?: return false

            tomlKeyValue.value.content = newConfigValue
        }

        /* Validate and resolve data range if mismatch data range */
        if (!ConfigDataRange.validateDataRange(configValue, configEntry.dataType, configValueDataRange)) {
            val newConfigValue = DataUtil.toDataType(configValue, configEntry.dataType)
                ?: configEntry.defaultValue
                ?: return false

            tomlKeyValue.value.content = newConfigValue
        }

        return true
    }

    private fun tryResolveParseError(tomlStringList: MutableList<String>, parseResult: Result<TomlFile>): MutableList<String> {
        val parseException = parseResult.exceptionOrNull() ?: throw XenonException("Failed to parse config file")
        val parseExceptionMsg = parseException.message

        /* Validate exception */
        if (parseException !is TomlDecodingException) {
            parseExceptionMsg?.let {
                throw XenonException(it)
            }

            throw XenonException("Failed to parse config file")
        }

        /* Try to get the string line number that cause parse error */
        val errorLineNo = parseExceptionMsg?.let { stringChar ->
            Regex("""[Ll]ine:? <?(\d+)>?""").find(stringChar)?.groupValues?.get(1)?.toInt()
        } ?: throw XenonException("Error while trying to get error line number from config file!")

        /* Resolve parse error by category */
        when {
            // Error Case 1: Incorrect format key-value pair (missing equals sign) | E.g: keyName
            TomlExceptionMessage.validateExceptionMessage(
                parseExceptionMsg,
                TomlExceptionMessage.TomlParseExceptionIncorrectFormat
            ) -> {
                tomlStringList[errorLineNo - 1] = "${tomlStringList[errorLineNo - 1].trimEnd()} = \"\""
            }

            // Error Case 2: Invalid key spaces | E.g: keyName keyValue
            TomlExceptionMessage.validateExceptionMessage(
                parseExceptionMsg,
                TomlExceptionMessage.TomlParseExceptionInvalidSpaces
            ) -> {
                tomlStringList[errorLineNo - 1] = with (tomlStringList[errorLineNo - 1].split(" ").filter { it.isNotEmpty() }) {
                    "${this.first()} = \"${this[1]}\""
                }
            }

            // Error Case 3: String value not wrapped or quoted | E.g: keyName = stringValue
            TomlExceptionMessage.validateExceptionMessage(
                parseExceptionMsg,
                TomlExceptionMessage.TomlParseExceptionStringValueNotWrapped
            ) -> {
                val searchDelimiter = "="
                val replacedValue = tomlStringList[errorLineNo - 1].substringAfter(searchDelimiter).trimIndent().run {
                    return@run DataUtil.toDataType(this, DataType.Boolean)?.let {
                        it as Boolean
                    } ?: "\"${this}\""
                }

                tomlStringList[errorLineNo - 1] = tomlStringList[errorLineNo - 1].replaceAfter(searchDelimiter, " $replacedValue")
            }

            else -> {
                throw XenonException(parseExceptionMsg)
            }
        }

        return tomlStringList
    }

    private fun getDataType(tomlDataType: TomlValue): DataType {
        return when (tomlDataType) {
            is TomlBasicString, is TomlLiteralString -> DataType.String

            is TomlBoolean -> DataType.Boolean

            is TomlLong -> DataType.Long

            is TomlDouble -> DataType.Double

            else -> throw XenonException("Unsupported data type: $tomlDataType")
        }
    }
}