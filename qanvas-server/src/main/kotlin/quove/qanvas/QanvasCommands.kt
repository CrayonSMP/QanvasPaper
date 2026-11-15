package quove.qanvas

import net.minecraft.server.MinecraftServer
import org.bukkit.command.Command
import quove.qanvas.commands.QanvasCommand

object QanvasCommands {
    private val COMMANDS = mapOf(
        "qanvas" to QanvasCommand("qanvas")
    )

    fun registerCommands(server: MinecraftServer) {
        COMMANDS.forEach { (name: String, command: Command) ->
            server.server.commandMap.register(name, "Qanvas", command)
        }
    }
}
