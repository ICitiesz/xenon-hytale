package com.islandstudio.xenon.shared.experimental.protocol

import com.hypixel.hytale.server.core.io.adapter.PacketAdapters
import com.hypixel.hytale.server.core.io.adapter.PacketFilter
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter

class PacketFilterRegistry {
    private val registeredPlayerPacketFilters = HashMap<Class<*>, PlayerPacketFilterHolder>()

    data class PlayerPacketFilterHolder(val inboundPlayerPacketFilter: PlayerPacketFilter?, val outboundPlayerPacketFilter: PlayerPacketFilter?) {
        private var registeredInboundPacketFilter: PacketFilter? = null
        private var registeredOutboundPacketFilter: PacketFilter? = null
        fun registerInbound() {
            inboundPlayerPacketFilter?.let {
                registeredInboundPacketFilter = PacketAdapters.registerInbound(it)
            }
        }

        fun registerOutbound() {
            outboundPlayerPacketFilter?.let {
                registeredOutboundPacketFilter = PacketAdapters.registerOutbound(it)
            }
        }

        fun deregister() {
            registeredInboundPacketFilter?.let {
                PacketAdapters.deregisterInbound(it)
            }

            registeredOutboundPacketFilter?.let {
                PacketAdapters.deregisterOutbound(it)
            }
        }

        fun getRegisteredInbound(): PacketFilter? = registeredInboundPacketFilter

        fun getRegisteredOutbound(): PacketFilter? = registeredOutboundPacketFilter
    }

    fun registerPlayerPacketFilter(clazz: Class<*>, playerPacketFilterHolder: PlayerPacketFilterHolder) {
        val inboundFilter = playerPacketFilterHolder.inboundPlayerPacketFilter
        val outboundFilter = playerPacketFilterHolder.outboundPlayerPacketFilter

        if (inboundFilter == null && outboundFilter == null) return

        registeredPlayerPacketFilters[clazz] = playerPacketFilterHolder

        playerPacketFilterHolder.registerInbound()
        playerPacketFilterHolder.registerOutbound()
    }

    fun deregisterPlayerPacketFilter(clazz: Class<*>) {
        val playerPacketFilterHolder = registeredPlayerPacketFilters[clazz] ?: return

        playerPacketFilterHolder.deregister()

        registeredPlayerPacketFilters.remove(clazz)
    }

    fun deregisterAllPlayerPacketFilter() {
        registeredPlayerPacketFilters.values.forEach {
            it.deregister()
        }

        registeredPlayerPacketFilters.clear()
    }
}