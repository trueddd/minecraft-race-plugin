package com.github.trueddd

import de.tr7zw.nbtapi.NBTCompound
import de.tr7zw.nbtapi.NBTFile
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class RaceManager(private val pluginConfig: PluginConfig) {

    private fun getPlayerNBT(player: Player): NBTFile? {
        val playerDataFile = File("${Bukkit.getWorldContainer()}/${pluginConfig.worldName}/playerdata/${player.uniqueId}.dat")
        return try {
            NBTFile(playerDataFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun NBTCompound.setValueOnPath(path: String, attribute: NbtData) {
        val nextNode = path.substringBefore('/', "")
        if (nextNode.isEmpty()) {
            when (attribute) {
                is NbtData.Byte -> setByte(path, attribute.value)
                is NbtData.Short -> setShort(path, attribute.value)
                is NbtData.Int -> setInteger(path, attribute.value)
                is NbtData.Long -> setLong(path, attribute.value)
                is NbtData.Float -> setFloat(path, attribute.value)
                is NbtData.Double -> setDouble(path, attribute.value)
                is NbtData.String -> setString(path, attribute.value)
            }
        } else {
            getCompound(nextNode).setValueOnPath(path.substringAfter('/'), attribute)
        }
    }

    // fixme: add support for List Attributes
    fun setRace(sender: CommandSender, player: Player, raceName: String): Boolean {
        val playerData = getPlayerNBT(player) ?: return false
        val attributes = pluginConfig.attributes[raceName] ?: return false
        return try {
            attributes.forEach { (name, value) ->
                playerData.setValueOnPath(name, value)
            }
            playerData.save()
            player.saveData()
            player.loadData()
            sender.sendMessage("Successfully changed attributes to $raceName race.")
            true
        } catch (e: Exception) {
            sender.sendMessage("Error occurred while changing attributes to $raceName race.")
            e.printStackTrace()
            false
        }
    }
}