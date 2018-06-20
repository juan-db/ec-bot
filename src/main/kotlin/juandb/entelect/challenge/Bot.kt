package juandb.entelect.challenge

import juandb.entelect.challenge.entity.*
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.util.logger
import kotlin.math.min

class Bot(private val gameState: GameState) {
	private val logger by logger()

	private val gameDetails: GameDetails = gameState.gameDetails
	private val buildingStats: Map<BuildingType, BuildingStats> = gameDetails.buildingsStats

	/* Dimensions */
	private val gameWidth: Int = gameDetails.mapWidth
	private val gameHeight: Int = gameDetails.mapHeight
	/** The width of my side of the board. */
	private val myWidth: Int = gameWidth / 2

	/* Players */
	private val myself: Player = gameState.players.first { it.playerType == Player.PLAYER }
	private val opponent: Player = gameState.players.first { it.playerType == Player.ENEMY }

	private val mostExpensiveBuilding = buildingStats.maxBy { it.value.price }!!.key

	private val rows = gameState.gameMap.mapIndexed { index, cells -> RowState(index, cells) }
	private val buildableRows = rows.filter { it.friendlyEmptyCells.isNotEmpty() }

	private val minDefenseRow = myWidth - myWidth / 4

	private val actualEnergyBuildingCount = rows.sumBy { it.friendlyEnergyBuildings }
	private val idealEnergyBuildingCount = min(buildingStats[mostExpensiveBuilding]!!.price / buildingStats[BuildingType.ENERGY]!!.energyGeneratedPerTurn + 1, myWidth * gameHeight / 3)

	fun run(): String {
		// If I can't afford any buildings or there are no empty tiles, do nothing
		if (BuildingType.values().none { it.canAfford(myself) } || buildableRows.isEmpty()) {
			return doNothingCommand()
		}

		/* For every 3 enemy attack buildings there should be 1 defense building, following is the reasoning:
		 * Missiles do 5 damage each, defense buildings have 20 hp. Using these numbers we can conclude it would take 4
		 * missiles to destroy a defense building. Therefore, if there are 3 attack buildings in a row, 1 defense
		 * building can fend off all their missiles for one round and still have enough hp to defend against one more.
		 */
		TODO("build a defense building for each row that requires defense")
	}

	private fun doNothingCommand(): String {
		return ""
	}

	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String {
		return "" + x + "," + y + "," + buildingType.id
	}

	private fun BuildingType.canAfford(player: Player): Boolean {
		return player.energy >= gameDetails.buildingsStats[this]!!.price
	}
}
