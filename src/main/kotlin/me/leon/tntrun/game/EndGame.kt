/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.EndGameCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityDamage
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityDamageByEntity
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.schedule
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.LobbyEventsTemplate
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class EndGame(
    private val plugin: Plugin,
    private val spawn: Location,
    private val gameStateManager: GameStateManager
) : StartStop {

    private val countdown: EndGameCountdown = EndGameCountdown(seconds = 3)

    override fun start() {
        block(true)
        plugin.schedule {
            countdown.players.forEach {
                it.teleport(spawn)
                it.gameMode = GameMode.SURVIVAL
            }
        }
        LobbyEventsTemplate.setup(plugin, spawn)
        countdown.start()
    }

    override fun stop() {
        block(false)
        LobbyEventsTemplate.reset()
//        if (countdown.players.size < 2) {
//            gameStateManager.shutdown()
//            gameStateManager.init()
//        } else
        countdown.stop()
    }

    private fun block(value: Boolean) {
        cancelEntityDamage = value
        cancelEntityDamageByEntity = value
    }

}
