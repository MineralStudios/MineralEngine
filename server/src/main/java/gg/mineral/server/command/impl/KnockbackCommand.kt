package gg.mineral.server.command.impl

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor

class KnockbackCommand : Command("", "knockback") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        if (arguments.size == 0) {
            commandExecutor.msg("Usage: /knockback <x> <y> <z> <extraX> <extraY> <extraZ> <yLimit> <friction>")
            return
        }

        try {
            x = arguments[0]!!.toDouble()
            y = arguments[1]!!.toDouble()
            z = arguments[2]!!.toDouble()
            extraX = arguments[3]!!.toDouble()
            extraY = arguments[4]!!.toDouble()
            extraZ = arguments[5]!!.toDouble()
            yLimit = arguments[6]!!.toDouble()
            friction = arguments[7]!!.toDouble()
        } catch (e: NumberFormatException) {
            commandExecutor.msg("Invalid number format.")
            return
        }

        commandExecutor.msg(
            ("Knockback set to: " + x + " " + y + " " + z + " " + extraX + " " + extraY + " " + extraZ + " " + yLimit
                    + " " + friction)
        )
    }

    companion object {
        var x: Double = 0.35
        var y: Double = 0.35
        var z: Double = 0.35
        var extraX: Double = 0.425
        var extraY: Double = 0.085
        var extraZ: Double = 0.425
        var yLimit: Double = 0.4
        var friction: Double = 2.0
    }
}
