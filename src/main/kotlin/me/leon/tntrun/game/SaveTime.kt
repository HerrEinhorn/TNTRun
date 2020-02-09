/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.SaveTimeCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityDamage
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityDamageByEntity
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.LobbyEventsTemplate
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class SaveTime(
    private val plugin: Plugin,
    private val spawn: Location
) : StartStop {

    private val countdown: SaveTimeCountdown = SaveTimeCountdown(5)

    override fun start() {
        block(true)
        LobbyEventsTemplate.setup(plugin, spawn)
        countdown.start()
    }

    override fun stop() {
        block(false)
        LobbyEventsTemplate.reset()
        countdown.stop()
    }

    private fun block(value: Boolean) {
        cancelEntityDamage = value
        cancelEntityDamageByEntity = value
    }

}
