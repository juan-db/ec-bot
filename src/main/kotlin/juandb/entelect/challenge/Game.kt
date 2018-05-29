package juandb.entelect.challenge

import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Game {
	private const val COMMAND_FILE_NAME = "command.txt"
	private const val STATE_FILE_NAME = "state.json"

	@JvmStatic
	fun main(args: Array<String>) {
		val gameState = parseGameState(STATE_FILE_NAME)
		val bot = Bot(gameState)
		val command = bot.run()

		writeBotResponseToFile(COMMAND_FILE_NAME, command)
	}

	private fun parseGameState(stateFileLocation: String): GameState {
		val state: String = try {
			File(stateFileLocation).readText()
		} catch (ioe: IOException) {
			ioe.printStackTrace()
			""
		}

		return Gson().fromJson(state, GameState::class.java)
	}

	private fun writeBotResponseToFile(commandFileLocation: String, command: String) {
		BufferedWriter(FileWriter(File(commandFileLocation))).use {
			try {
				it.write(command)
				it.flush()
			} catch (ioe: IOException) {
				ioe.printStackTrace()
			}
		}
	}
}
