package com.islandstudio.xenon.shared.di

import com.islandstudio.xenon.shared.init.context.IPluginContext
import org.koin.core.Koin
import org.koin.core.annotation.KoinApplication
import org.koin.ksp.generated.koinApplication

@KoinApplication(modules = [

])
object PluginDependencyInjectionManager {
    private var koinApp: org.koin.core.KoinApplication? = null

    fun startAsPluginScoped(pluginContext: IPluginContext) {
        koinApp = this.koinApplication()

        koinApp?.koin?.declare(pluginContext)
    }

    fun getKoin(): Koin {
        return koinApp?.koin ?: throw IllegalStateException("Koin not initialized. Call startAsPluginScoped() first.")
    }

    fun dispose() {
        koinApp?.close()
        koinApp = null
    }
}