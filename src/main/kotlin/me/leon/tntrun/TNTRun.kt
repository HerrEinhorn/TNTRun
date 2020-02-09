/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun

import com.google.gson.JsonObject
import me.leon.tntrun.commands.registerStartCommand
import me.leon.tntrun.game.GameStateManager
import net.darkdevelopers.darkbedrock.darkness.general.configs.formatToConfigPattern
import net.darkdevelopers.darkbedrock.darkness.general.configs.toConfigMap
import net.darkdevelopers.darkbedrock.darkness.general.functions.load
import net.darkdevelopers.darkbedrock.darkness.general.functions.save
import net.darkdevelopers.darkbedrock.darkness.general.functions.toConfigData
import net.darkdevelopers.darkbedrock.darkness.general.functions.toMap
import net.darkdevelopers.darkbedrock.darkness.spigot.plugin.DarkPlugin

class TNTRun : DarkPlugin() {

    private val gameStateManager by lazy { GameStateManager(this) }

    override fun onEnable() = onEnable {
        gameStateManager.init()
        "Start".registerCommandConfig {
            registerStartCommand(this, it) { gameStateManager.lobby.countdown }
        }
    }

    override fun onDisable() = onDisable {
        gameStateManager.shutdown()
    }

    private fun String.registerCommandConfig(code: (Map<String, Any?>) -> Unit) {
        val configData = "${this}Command".formatToConfigPattern().toConfigData(dataFolder)
        val values = configData.load<JsonObject>().toMap()
        code(values)
        configData.save(values.toConfigMap())
    }

}

