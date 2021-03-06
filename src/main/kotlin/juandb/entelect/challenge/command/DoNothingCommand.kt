package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.GameState
import java.util.*

class DoNothingCommand(gameState: GameState) : Command(gameState) {
	companion object {
		private val random: Random = Random()
	}

	override fun getWeight(): Double = Math.random() * 2.0 // TODO
	override fun getCommand(): String = ""

	override fun toString(): String = this.javaClass.name
}