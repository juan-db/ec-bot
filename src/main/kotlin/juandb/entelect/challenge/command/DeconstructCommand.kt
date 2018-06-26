package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.GameState
import java.util.*

class DeconstructCommand(gameState: GameState, private val x: Int, private val y: Int) : Command(gameState) {
	companion object {
		/** Id for deconstruct command. */
		private const val deconstructId: Int = 3

		fun deconstructCommand(x: Int, y: Int): String = "$x,$y,$deconstructId"

		private val random: Random = Random()
	}

	override fun getWeight(): Int = random.nextInt(2) - 15// TODO
	override fun getCommand(): String = deconstructCommand(x, y)

	override fun toString(): String = "${this.javaClass.name}[x:$x;y:$y]"
}