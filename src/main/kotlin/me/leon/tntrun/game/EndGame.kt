/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.EndGameCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.schedule
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.LobbyEventsTemplate
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class EndGame(
    private val plugin: Plugin,
    private val spawn: Location
) : StartStop {

    private val countdown: EndGameCountdown = EndGameCountdown(seconds = 3)

    override fun start() {
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
        LobbyEventsTemplate.reset()
        countdown.stop()
//        Bukkit.shutdown()
    }

}
