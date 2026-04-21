package com.islandstudio.xenon.shared.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.tree.nodes.TomlFile
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.server.core.util.Config
import com.islandstudio.xenon.shared.config.configobject.TestConfigObject
import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import com.islandstudio.xenon.shared.experimental.TomlCommentHelper
import com.islandstudio.xenon.shared.io.DataDirectory
import com.islandstudio.xenon.shared.io.ExternalResource
import com.islandstudio.xenon.shared.utils.TextHelper
import kotlinx.serialization.serializer
import java.io.File
import kotlin.collections.asSequence
import kotlin.io.path.Path
import kotlin.reflect.full.createType

class BaseConfig<T>(
    val configCodec: BuilderCodec<T>,
    configResource: ExternalResource,
    inputOption: TomlInputConfig = TomlInputConfig(true, allowEscapedQuotesInLiteralStrings = true),
    outputOption: TomlOutputConfig = TomlOutputConfig(TomlIndentation.TWO_SPACES)
) {
    private val tomlInstance = Toml(inputOption, outputOption)
    private val configFile = DataDirectory.createOrGetFile(configResource.resourceFolder, configResource.resourceName)
    val configObject = initialize()

    private fun initialize(): T {
        if (configFile.length() == 0L) {
            val defaultConfigObject = configCodec.defaultValue

            saveToFile(defaultConfigObject)
            return defaultConfigObject
        }

        return toConfigObject(configFile.readText()) // TODO: Revise needed
    }

    private fun parseToTomFile(configFile: File): TomlFile {
        var configContent = configFile
            .reader()
            .use {
                it.readLines().toMutableList()
            }

        return tomlInstance.tomlParser.parseLines(configContent.asSequence())

        /* TODO: Revise needed */
        /* Try to parse and resolve any parse error if possible */
//        while (true) {
//            val parseResult = runCatching {
//                tomlInstance.tomlParser.parseLines(configContent.asSequence())
//            }.onSuccess {
//                return it
//            }
//
//            configContent = tryResolveParseError(configContent, parseResult)
//        }
    }

    private fun encodeToString(tomlFile: TomlFile): String {
        return tomlInstance.tomlWriter.writeToString(tomlFile)
    }

    private fun encodeToString(configObject: T): String {
        return tomlInstance.encodeToString(serializer(configCodec.defaultValue::class.createType()), configObject)
    }

    private fun saveToFile(configObject: T) {
        configFile.writeText(encodeToString(configObject))
    }

    private fun updateConfig(): TomlFile {
        val parsedConfigAsTomlFile = parseToTomFile(configFile)

        return parsedConfigAsTomlFile
    }

    private fun toConfigObject(configContent: String): T {
        var configContent2 = configFile
            .reader()
            .use {
                it.readLines().toMutableList()
            }

        val s2 = tomlInstance.tomlParser.parseLines(configContent2.asSequence())
//        s2.children.forEach {
//            println("Debug: \n${it.comments}")
//        }

        s2.getRealTomlTables().forEach {
            it.children
            println("Debug: ${it.fullTableKey}")
        }

        //println("Debug: \n${tomlInstance.tomlWriter.writeToString(s2)}")

        return tomlInstance
            .decodeFromString(
                serializer(configCodec.defaultValue::class.createType()),
                configContent
            ) as T
    }

    fun addComment() {

    }
}