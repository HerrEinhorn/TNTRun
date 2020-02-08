/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import me.leon.tntrun.registerAsConfig
import net.darkdevelopers.darkbedrock.darkness.spigot.events.countdown.EndGameCountdownCallEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.events.countdown.LobbyCountdownCallEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.events.countdown.SaveTimeCountdownCallEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.listen
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.loadBukkitWorld
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.schedule
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.EventsTemplate
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.io.File

class GameStateManager(
    private val plugin: Plugin
) : EventsTemplate() {

    private val config = Config(plugin.dataFolder)
    private val gameWorld: GameWorld = config.gameWorlds.random()
    private val lobby: Lobby = Lobby(plugin, config.lobbySpawn, config.gameName, config.minPlayers, gameWorld.name)
    private val saveTime: SaveTime = SaveTime(plugin, gameWorld.spawn)
    private val endGame: EndGame = EndGame(plugin, config.lobbySpawn)
    private val inGame: InGame by lazy { InGame(plugin, gameWorld.spawn.blockY - 10) { endGame.start() } }

    fun init() {
        lobby.start()
        listen<LobbyCountdownCallEvent>(plugin) { event ->
            val countdown = event.lobbyCountdown
            if (countdown.seconds == 20) plugin.schedule {
                gameWorld.spawn.world = (gameWorld.spawn.world?.name ?: "GameWorld").loadBukkitWorld().apply {
                    plugin.schedule {
                        for (x in -20..20)
                            for (z in -20..20)
                                getBlockAt(x, 100, z).setType(Material.TNT, false)
                    }
                }
            }
            if (countdown.seconds > 0) return@listen
            lobby.stop()
            saveTime.start()
            plugin.schedule {
                countdown.players.forEach { it.teleport(gameWorld.spawn) }
            }
        }.add()
        listen<SaveTimeCountdownCallEvent>(plugin) {
            if (it.saveTimeCountdown.seconds > 0) return@listen
            saveTime.stop()
            inGame.start()
        }.add()
        listen<EndGameCountdownCallEvent>(plugin) { event ->
            val countdown = event.endGameCountdown
            if (countdown.players.any { it.isOnline } && countdown.seconds > 0) return@listen
            endGame.stop()
            lobby.start()
        }.add()
    }

    fun shutdown() {
        lobby.stop()
        saveTime.stop()
        inGame.stop()
        endGame.stop()
        reset()
    }

    class Config(dataFolder: File) {

        val lobbySpawn: Location = Location(Bukkit.getWorlds().first(), -46.5, 148.0, 1253.5, 145f, 0f)
        val gameName = "TNTRun"
        val minPlayers = 2
        val gameWorlds: Set<GameWorld> = setOf(
            GameWorld("LarsDerSprengMeister", Location(Bukkit.getWorld("GameWorld"), 0.0, 100.0, 0.0))
        )

        init {
            registerAsConfig(dataFolder)
        }

    }

}
