package com.islandstudio.xenon.shared.experimental.protocol.provider

import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter

interface IPlayerPacketFilterProvider {


    fun getInboundFilter(): PlayerPacketFilter? = null
    fun getOutboundFilter(): PlayerPacketFilter? = null
}