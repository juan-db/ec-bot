package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.GameState

abstract class Command(protected val gameState: GameState) {
	abstract fun getWeight(): Double
	abstract fun getCommand(): String
}