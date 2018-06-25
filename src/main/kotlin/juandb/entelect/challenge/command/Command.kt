package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.GameState

abstract class Command(protected val gameState: GameState) {
	abstract fun getWeight(): Int
	abstract fun getCommand(): String
}