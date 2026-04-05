package com.islandstudio.xenon.shared.di

import com.islandstudio.xenon.shared.init.context.IPluginContext
import org.koin.core.Koin
import org.koin.core.annotation.KoinApplication
import org.koin.ksp.generated.koinApplication

@KoinApplication(modules = [

])
object PluginDependencyInjectionManager {
    fun startAsPluginScoped(pluginContext: IPluginContext) {
        this.koinApplication().koin.declare(pluginContext)
    }

    fun getKoin(): Koin {
        return this.koinApplication().koin
    }

    fun dispose() {
        this.koinApplication().close()
    }
}