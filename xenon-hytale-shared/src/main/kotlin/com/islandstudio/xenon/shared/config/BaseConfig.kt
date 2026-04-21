package com.islandstudio.xenon.shared.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.tree.nodes.TomlFile
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import com.islandstudio.xenon.shared.io.DataDirectory
import com.islandstudio.xenon.shared.io.ExternalResource
import kotlinx.serialization.serializer
import java.io.File
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

    init {
        /* 1. Config Object -> Toml String (no comments) */
        val tomlString = encodeToString(configObject)

        /* 2. Toml String -> Toml File */
        val tomlFileObj = decodeToTomFile(tomlString)

        // Testing only
        val testConfigPropertySections = TestConfigProperty.getAllConfigSection()

        testConfigPropertySections.forEach { configSection ->
            val tomlTable = tomlFileObj.getRealTomlTables().find { it.fullTableKey.toString() == configSection.sectionKey }

            if (tomlTable == null) return@forEach

            tomlTable.comments.add(configSection.description)
        }
    }

    private fun initialize(): T {
        if (configFile.length() == 0L) {
            val defaultConfigObject = configCodec.defaultValue

            saveToFile(defaultConfigObject)
            return defaultConfigObject
        }

        return toConfigObject(configFile.readText()) // TODO: Revise needed
    }

    private fun encodeToString(tomlFile: TomlFile): String {
        return tomlInstance.tomlWriter.writeToString(tomlFile)
    }

    private fun encodeToString(configObject: T): String {
        return tomlInstance.encodeToString(serializer(configCodec.defaultValue::class.createType()), configObject)
    }

    private fun decodeToTomFile(tomlString: String): TomlFile {
        return tomlInstance.tomlParser.parseString(tomlString)
    }

    private fun decodeToTomFile(configFile: File): TomlFile {
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

    private fun saveToFile(configObject: T) {
        configFile.writeText(encodeToString(configObject))
    }

    private fun updateConfig(): TomlFile {
        val parsedConfigAsTomlFile = decodeToTomFile(configFile)

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