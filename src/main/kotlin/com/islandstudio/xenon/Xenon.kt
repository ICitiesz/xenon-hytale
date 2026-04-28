package com.islandstudio.xenon

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.islandstudio.xenon.shared.di.PluginDependencyInjectionManager
import com.islandstudio.xenon.shared.init.context.PluginContext

class Xenon(init: JavaPluginInit) : JavaPlugin(init) {

    protected override fun setup() {
        PluginDependencyInjectionManager.startAsPluginScoped(PluginContext(this))
    }
}