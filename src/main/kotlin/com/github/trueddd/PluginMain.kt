package com.github.trueddd

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused") // Used by minecraft server core
class PluginMain : JavaPlugin() {

    private val pluginConfig = PluginConfig()

    private val raceManager: RaceManager by lazy { RaceManager(pluginConfig) }

    override fun onEnable() {
        super.onEnable()
        println("Plugin enabled")
        saveDefaultConfig()
        pluginConfig.buildFromConfig(config)
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        println("sender: $sender; command: $command; label: $label; args: $args")
        return when (command.label) {
            // /races
            "races" -> {
                sender.sendMessage("Currently available races: [${pluginConfig.races.joinToString(", ")}]")
                true
            }
            // /race trueddd default
            "race" -> {
                val player = try {
                    server.getPlayer(args[0])
                } catch (e: Exception) {
                    sender.sendMessage("No player name was defined")
                    return false
                }
                val raceName = try {
                    args[1]
                } catch (e: Exception) {
                    sender.sendMessage("No race was defined")
                    return false
                }
                raceManager.setRace(sender, player, raceName)
            }
            else -> return super.onCommand(sender, command, label, args)
        }
    }
}