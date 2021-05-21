package com.github.trueddd

import de.tr7zw.nbtapi.NBTCompound
import de.tr7zw.nbtapi.NBTFile
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class RaceManager(private val pluginConfig: PluginConfig) {

    private fun getPlayerNBT(playerDataFile: File): NBTFile? {
        return try {
            NBTFile(playerDataFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val listRefRegex = Regex("(?<listName>\\w+)\\[(?<pos>.+)]", RegexOption.DOT_MATCHES_ALL)

    private fun NBTCompound.applyValue(fieldName: String, attribute: NbtData) {
        when (attribute) {
            is NbtData.Byte -> setByte(fieldName, attribute.value)
            is NbtData.Short -> setShort(fieldName, attribute.value)
            is NbtData.Int -> setInteger(fieldName, attribute.value)
            is NbtData.Long -> setLong(fieldName, attribute.value)
            is NbtData.Float -> setFloat(fieldName, attribute.value)
            is NbtData.Double -> setDouble(fieldName, attribute.value)
            is NbtData.String -> setString(fieldName, attribute.value)
        }
    }

    private fun NBTCompound.setValueOnPath(path: String, attribute: NbtData) {
        val listRef = listRefRegex.find(path)
        val nextNode = path.substringBefore('/', "")
        when {
            listRef != null -> {
                val listName = listRef.groups["listName"]?.value ?: throw IllegalStateException("listName is null")
                val list = getCompoundList(listName) ?: throw IllegalStateException("Couldn\'t find list with name \'${listName}\'")
                val position = listRef.groups["pos"]?.value ?: throw IllegalStateException("position is null")
                if (position.contains(":")) {
                    val splitPosition = position.split(":", limit = 2)
                    val fieldName = splitPosition.first()
                    val fieldValue = splitPosition.last().replace(".", "_")
                    list.firstOrNull { it.getString(fieldName)?.replace(".", "_") == fieldValue }
                        ?.let{
                            val nextPath = path.substringAfter("]/", "")
                            if (nextPath.isBlank()) {
                                throw IllegalStateException("Path is invalid: [${fieldName}:${fieldValue}] must have child")
                            }
                            it.setValueOnPath(nextPath, attribute)
                        }
                        ?: throw IllegalStateException("Item with \'${fieldName}\' == \'${fieldValue}\' was not found in \'${listName}\' list")
                } else {
                    TODO()
                }
            }
            nextNode.isEmpty() -> applyValue(path, attribute)
            else -> getCompound(nextNode)
                ?.setValueOnPath(path.substringAfter('/'), attribute)
                ?: throw IllegalStateException("Compound $nextNode not found in $path")
        }
    }

    fun setRace(dataFolder: File, sender: CommandSender, player: Player, raceName: String): Boolean {
        val worldSuffix = if (File("${Bukkit.getWorldContainer().absolutePath}/playerdata").exists()) {
            ""
        } else {
            "/${pluginConfig.worldName}"
        }
        val path = "${Bukkit.getWorldContainer()}${worldSuffix}/playerdata/${player.uniqueId}.dat"
        println("Path to player data: $path")
        val dataFile = File(path)
        val playerData = getPlayerNBT(dataFile) ?: return false
        val attributes = pluginConfig.attributes[raceName] ?: return false
        return try {
            attributes.forEach { (name, value) ->
                playerData.setValueOnPath(name, value)
            }
            player.saveData()
            dataFile.delete()
            dataFile.createNewFile()
            playerData.writeCompound(dataFile.outputStream())
            player.loadData()
            pluginConfig.changePlayerRace(dataFolder, player, raceName)
            sender.sendMessage("Successfully changed attributes to $raceName race.")
            true
        } catch (e: Exception) {
            sender.sendMessage("Error occurred while changing attributes to $raceName race.")
            e.printStackTrace()
            false
        }
    }
}