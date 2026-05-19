package com.islandstudio.xenon.experimental

import com.hypixel.hytale.protocol.Direction
import com.hypixel.hytale.protocol.Position
import com.hypixel.hytale.protocol.packets.player.ClientMovement
import com.hypixel.hytale.server.core.Message
import com.islandstudio.xenon.shared.di.IComponentProvider
import com.islandstudio.xenon.shared.di.getComponent
import com.islandstudio.xenon.shared.experimental.protocol.PacketFilterRegistry
import com.islandstudio.xenon.shared.init.context.IPluginContext
import kotlin.math.abs

class PlayerObservedObjectSystem {
    companion object: IComponentProvider {
        private val pluginContext = getComponent<IPluginContext>()
        private var lastAbsolutePosition: Position? = null
        private var lastLookOrientation: Direction? = null
        private const val MOVEMENT_THRESHOLD = 0.1f

        fun run() {
            pluginContext.packetFilterRegistry.registerPlayerPacketFilter(
                this.javaClass,
                PacketFilterRegistry.PlayerPacketFilterHolder(
                    { playerRef, packet ->
                        val clientMovementPacket = packet as? ClientMovement ?: return@PlayerPacketFilterHolder false

                        clientMovementPacket.absolutePosition?.let {
                            if (lastAbsolutePosition == null) {
                                lastAbsolutePosition = it
                                return@let
                            }

                            val xDelta = abs(it.x - lastAbsolutePosition!!.x)
                            val yDelta = abs(it.y - lastAbsolutePosition!!.y)
                            val zDelta = abs(it.z - lastAbsolutePosition!!.z)

                            if (xDelta > MOVEMENT_THRESHOLD || yDelta > MOVEMENT_THRESHOLD || zDelta > MOVEMENT_THRESHOLD) {
                                playerRef.sendMessage(Message.raw("Debug: Movement detected"))
                                lastAbsolutePosition = it
                                return@PlayerPacketFilterHolder false
                            }
                        }

                        clientMovementPacket.lookOrientation?.let {
                            if (lastLookOrientation == null) {
                                lastLookOrientation = it
                                return@let
                            }

                            val yawDelta = abs(it.yaw - lastLookOrientation!!.yaw)
                            val pitchDelta = abs(it.pitch - lastLookOrientation!!.pitch)
                            val rollDelta = abs(it.roll - lastLookOrientation!!.roll)

                            if (yawDelta > MOVEMENT_THRESHOLD || pitchDelta > MOVEMENT_THRESHOLD || rollDelta > MOVEMENT_THRESHOLD) {
                                playerRef.sendMessage(Message.raw("Debug: Look detected"))
                                lastLookOrientation = it
                                return@PlayerPacketFilterHolder false
                            }
                        }

                        return@PlayerPacketFilterHolder false
                    },
                    null
                )
            )
        }
    }
}