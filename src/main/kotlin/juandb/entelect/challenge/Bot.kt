package juandb.entelect.challenge

import juandb.entelect.challenge.Building.BuildingType
import juandb.entelect.challenge.Player.PlayerType

class Bot(private val gameState: GameState) {
	companion object {
		private fun doNothingCommand(): String {
			return ""
		}
		private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String {
			return "" + x + "," + y + "," + buildingType.id
		}
	}
	private val gameDetails: GameDetails = gameState.gameDetails
	private val gameWidth: Int = gameDetails.mapWidth
	private val gameHeight: Int = gameDetails.mapHeight
	private val myself: Player = gameState.players.first { it.playerType == Player.PLAYER }
	private val opponent: Player = gameState.players.first { it.playerType == Player.ENEMY }

	fun run(): String {
		return when {
			else -> doNothingCommand()
		}
	}

}
