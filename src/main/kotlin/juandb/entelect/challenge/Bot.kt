package juandb.entelect.challenge

import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.entity.GameDetails
import juandb.entelect.challenge.entity.GameState
import juandb.entelect.challenge.entity.Player
import juandb.entelect.challenge.entity.RowState

import juandb.entelect.challenge.util.logger

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
	private val rows = gameState.gameMap.mapIndexed { index, cells -> RowState(index, cells) }
	private val buildableRows = rows.filter { it.friendlyEmptyCells.isNotEmpty() }

	fun run(): String {
		buildableRows.forEach {
			if (it.friendlyDefenseBuildings < 1 && (it.enemyMissiles > 0 || it.enemyAttackBuildings > 0)) {
				return if (BuildingType.DEFENSE.canAfford(myself)) {
					buildCommand(it.friendlyEmptyCells.last().x, it.index, BuildingType.DEFENSE)
				} else {
					doNothingCommand()
				}
			}
		}

		return doNothingCommand()
	}

}
