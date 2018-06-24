package juandb.entelect.challenge.command

interface Command {
	fun getWeight(): Int
	fun getCommand(): String
}