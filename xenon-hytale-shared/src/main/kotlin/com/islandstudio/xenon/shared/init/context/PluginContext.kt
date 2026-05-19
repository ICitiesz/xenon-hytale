package com.islandstudio.xenon.shared.init.context

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.islandstudio.xenon.shared.experimental.protocol.PacketFilterRegistry
import org.koin.core.annotation.Single

@Single
class PluginContext(override val mainPluginInstance: JavaPlugin) : IPluginContext {
    override val packetFilterRegistry: PacketFilterRegistry = PacketFilterRegistry()
}