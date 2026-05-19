package com.islandstudio.xenon.shared.init.context

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.islandstudio.xenon.shared.experimental.protocol.PacketFilterRegistry

interface IPluginContext {
    val mainPluginInstance: JavaPlugin

    val packetFilterRegistry: PacketFilterRegistry
}