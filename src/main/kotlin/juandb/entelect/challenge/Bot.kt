package juandb.entelect.challenge

import juandb.entelect.challenge.entity.*
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.util.logger
import kotlin.math.max
import kotlin.math.min

class Bot(private val gameState: GameState) {
	private val logger by logger()

	private val gameDetails: GameDetails = gameState.gameDetails
	init {
		logger.info("${gameDetails.round}")
	}

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

	private val actualAttackBuildingCount = rows.sumBy { it.friendlyAttackBuildings }
	private val actualEnergyBuildingCount = rows.sumBy { it.friendlyEnergyBuildings }
	private val idealEnergyBuildingCount = min(buildingStats[mostExpensiveBuilding]!!.price / buildingStats[BuildingType.ENERGY]!!.energyGeneratedPerTurn + 1, myWidth * gameHeight / 3)

	init {
		logger.info("attack buildings [$actualAttackBuildingCount]; energy buildings [$actualEnergyBuildingCount]")
	}

	fun run(): String {
		// If I can't afford any buildings or there are no empty tiles, do nothing
		if (BuildingType.values().none { it.canAfford(myself) } || buildableRows.isEmpty()) {
			return doNothingCommand()
		}

		/*
		 * If there is somewhere that urgently needs defense, defend there.
		 */
		buildableRows.maxBy { it.enemyAttackBuildings - (it.friendlyDefenseBuildings * 3) }?.let {
			if (it.enemyAttackBuildings - (it.friendlyDefenseBuildings * 3) > 3) {
				it.friendlyEmptyCells.reversed().firstOrNull()?.let {
					return buildCommand(it.x, it.y, BuildingType.DEFENSE, myself)
				}
			}
		}

		/*
		 * At the start of the game, build until we reach the ideal energy building count while alternating between
		 * building energy and attack buildings.
		 *
		 * Basically, build 2 energy buildings, then 1 attack building, then 2 energy buildings, then 1 attack building,
		 * until we reach the ideal energy building count.
		 */
		if (actualEnergyBuildingCount < idealEnergyBuildingCount) {
			if (actualEnergyBuildingCount / max(actualAttackBuildingCount, 1) > 2) {
				logger.info("need to build attack building")
				buildableRows.filter { it.index >= actualAttackBuildingCount * 2 }.forEach {
					logger.info("attempting to put attack building in row ${it.index}")
					it.friendlyEmptyCells.firstOrNull()?.let {
						return buildCommand(it.x, it.y, BuildingType.ATTACK, myself)
					}
				}
			}
			(0 until myWidth).forEach { index ->
				buildableRows.forEach { row ->
					for (cell in row.friendlyEmptyCells) {
						if (cell.x == index) {
							return buildCommand(cell.x, cell.y, BuildingType.ENERGY, myself)
						} else if (cell.x > index) {
							break
						}
					}
				}
			}
		}

		/*
		 * For every 3 enemy attack buildings there should be 1 defense building, following is the reasoning:
		 * Missiles do 5 damage each, defense buildings have 20 hp. Using these numbers we can conclude it would take 4
		 * missiles to destroy a defense building. Therefore, if there are 3 attack buildings in a row, 1 defense
		 * building can fend off all their missiles for one round and still have enough hp to defend against one more.
		 */
		buildableRows.filter { it.needsDefense() }.maxBy { it.friendlyDefenseBuildings - it.enemyAttackBuildings }!!.let {
			it.friendlyEmptyCells.firstOrNull()?.let {
				return buildCommand(it.x, it.y, BuildingType.DEFENSE, myself)
			}
		}

		/*
		 * Fill the board with attack buildings.
		 */
		buildableRows.maxBy { it.friendlyEmptyCells.size }?.friendlyEmptyCells?.firstOrNull()?.let {
			return buildCommand(it.x, it.y, BuildingType.ATTACK, myself);
		}

		return doNothingCommand()
	}

	/**
	 * Returns a build command for the specified building type at the specified coordinates if the given player can
	 * afford it.
	 *
	 * If the player cannot afford it the function will return doNothingCommand
	 *
	 * @return a build command for the given arguments if the player can afford it, doNothingCommand otherwise.
	 */
	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType, player: Player): String {
		return if (buildingType.canAfford(player)) {
			logger.info("Building ${buildingType.name} at [$x,$y].")
			buildCommand(x, y, buildingType)
		} else {
			logger.info("Attempted to build building ${buildingType.name} at [$x,$y] but couldn't afford it.")
			doNothingCommand()
		}
	}

	/**
	 * @return the build command for the given building type at the given coordinates.
	 */
	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String {
		return "" + x + "," + y + "," + buildingType.id
	}

	/**
	 * @return a command that does nothing.
	 */
	private fun doNothingCommand(): String {
		return ""
	}

	/**
	 * @return true if the given player can afford the given building type.
	 */
	private fun BuildingType.canAfford(player: Player): Boolean {
		return player.energy >= gameDetails.buildingsStats[this]!!.price
	}
}
