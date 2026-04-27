package com.islandstudio.xenon

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.islandstudio.xenon.shared.config.CoreConfig
import com.islandstudio.xenon.shared.config.configobject.TestConfigObject
import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import com.islandstudio.xenon.shared.di.PluginDependencyInjectionManager
import com.islandstudio.xenon.shared.init.context.PluginContext
import com.islandstudio.xenon.shared.io.ExternalResource

class Xenon(init: JavaPluginInit) : JavaPlugin(init) {

    protected override fun setup() {
        PluginDependencyInjectionManager.startAsPluginScoped(PluginContext(this))

        val testCoreConfig = CoreConfig
            .Builder(TestConfigObject.CONFIG_CODEC)
            .build(TestConfigProperty.getAllConfigSection(), ExternalResource.TestConfigFile)

        val testConfigObject = testCoreConfig.configObject

        println("Debug: ${testConfigObject.IsEnabled}")
        println("Debug: ${testConfigObject.Options.TestOption1}")
    }
}