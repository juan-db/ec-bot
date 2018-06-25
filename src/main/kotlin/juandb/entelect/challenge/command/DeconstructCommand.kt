package juandb.entelect.challenge.command

class DeconstructCommand(private val x: Int, private val y: Int) : Command {
	companion object {
		/** Id for deconstruct command. */
		private const val deconstructId: Int = 3

		fun deconstructCommand(x: Int, y: Int): String = "$x,$y,$deconstructId"
	}

	override fun getWeight(): Int = 0 // TODO
	override fun getCommand(): String = deconstructCommand(x, y)

	override fun toString(): String = "${this.javaClass.name}[x:$x;y:$y]"
}