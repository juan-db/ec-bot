package juandb.entelect.challenge

import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.entity.GameDetails
import juandb.entelect.challenge.entity.GameState
import juandb.entelect.challenge.entity.Player
import juandb.entelect.challenge.entity.RowState

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

	private val minDefenseRow = gameWidth / 2 - gameWidth / 2 / 4

	fun run(): String {
		// If I can't afford any buildings, do nothing
		if (BuildingType.values().none { it.canAfford(myself) }) {
			return doNothingCommand()
		}

		// check if an initial row of defense buildings is required for any row
		buildableRows.forEach { row ->
			if (!row.isDefended()) {
				// if the row is under attack of it contains the max amount of valuable buildings, defend it
				if (row.isUnderAttack() || row.hasMaxBuildings(minDefenseRow)) {
					return if (BuildingType.DEFENSE.canAfford(myself)) {
						buildCommand(row.friendlyEmptyCells.last().x, row.index, BuildingType.DEFENSE)
					} else {
						doNothingCommand()
					}
				}
			}
		}



		return doNothingCommand()
	}
}
