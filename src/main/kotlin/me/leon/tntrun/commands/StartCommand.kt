/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.commands

import net.darkdevelopers.darkbedrock.darkness.general.configs.default
import net.darkdevelopers.darkbedrock.darkness.general.configs.getValue
import net.darkdevelopers.darkbedrock.darkness.spigot.commands.Command
import net.darkdevelopers.darkbedrock.darkness.spigot.configs.commands.AbstractCommandConfig
import net.darkdevelopers.darkbedrock.darkness.spigot.configs.commands.extensions.register
import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.LobbyCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendTo
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin


class StartCommandConfig(values: Map<String, Any?>) : AbstractCommandConfig(values) {
    override val commandName: String by values.default { "Start" }
    override val permission: String by values.default { "tntrun.commands.$commandName" }
    override val usage: String by values.default { "[seconds]" }
    override val maxLength: Int by values.default { 1 }

    val secondsMustBeAnInteger by values.default { "[TNTRun] Seconds must be an integer" }
    val successfullyStarted by values.default { "[TNTRun] Game successfully started" }
}


fun registerStartCommand(
    javaPlugin: JavaPlugin,
    config: Map<String, Any?>,
    countdown: () -> LobbyCountdown
): Command = StartCommandConfig(config).register(
    javaPlugin,
    tabCompleter = TabCompleter { _, _, _, args ->
        if (args.isNotEmpty()) emptyList()
        else (1..10).map { it.toString() }
    }) { sender, args, commandConfig ->

    val newSeconds = if (args.isEmpty()) 10 else args[0].toIntOrNull() ?: run {
        commandConfig.secondsMustBeAnInteger.sendTo(sender)
        return@register
    }
    countdown().apply {
        println(this.players.joinToString())
        start()
        seconds = newSeconds
    }
    commandConfig.successfullyStarted.sendTo(sender)
}
