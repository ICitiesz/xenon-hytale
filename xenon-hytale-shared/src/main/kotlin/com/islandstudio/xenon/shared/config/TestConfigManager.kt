package com.islandstudio.xenon.shared.config

import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.islandstudio.xenon.shared.config.configobject.TestConfigObject
import com.islandstudio.xenon.shared.io.ExternalResource
import com.islandstudio.xenon.shared.utils.TextHelper

class TestConfigManager: BaseConfig(ExternalResource.TestConfigFile) {
    val codec = BuilderCodec.builder(
        TestConfigObject::class.java,
        ::TestConfigObject
    ).append(
        KeyedCodec(TextHelper.capitalizeFirstChar(TestConfigObject::testValue1.name), BuilderCodec.BOOLEAN),
        { testConfigObject, value -> testConfigObject.copy(testValue1 = value) },
        { testConfigObject -> testConfigObject.testValue1 }
    ).add().build()
}