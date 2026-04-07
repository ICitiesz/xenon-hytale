package com.islandstudio.xenon.shared.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.islandstudio.xenon.shared.io.DataDirectory
import com.islandstudio.xenon.shared.io.ExternalResource
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType

abstract class BaseConfig(
    configResource: ExternalResource,
    inputOption: TomlInputConfig = TomlInputConfig(true, allowEscapedQuotesInLiteralStrings = true),
    outputOption: TomlOutputConfig = TomlOutputConfig(TomlIndentation.TWO_SPACES)
) {
    private val tomlInstance = Toml(inputOption, outputOption)
    private val configFile = DataDirectory.createOrGetFile(configResource.resourceFolder, configResource.resourceName)

    init {

    }

    private fun initialize() {
        if (configFile.length() == 0L) {

        }
    }

//    private fun saveToFile(configObject: IConfigObject) {
//        configFile.writeText(encodeToString(configObject))
//    }
//
//    private fun encodeToString(configObject: IConfigObject): String {
//        return tomlInstance.encodeToString(serializer(configObject::class.createType()), configObject)
//    }

//    private fun toConfigObject(configContent: String): IConfigObject {
//        return tomlInstance
//            .decodeFromString(
//                serializer(configObject::class.createType()),
//                configContent
//            ) as IConfigObject
//    }
}