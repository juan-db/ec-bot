package juandb.entelect.challenge

import juandb.entelect.challenge.Building.BuildingType
import juandb.entelect.challenge.Player.PlayerType

class Bot(private val gameState: GameState) {
	private val gameDetails: GameDetails = gameState.gameDetails
	private val gameWidth: Int = gameDetails.mapWidth
	private val gameHeight: Int = gameDetails.mapHeight
	private val myself: Player = gameState.players.first { p -> p.playerType == PlayerType.A }
	private val opponent: Player = gameState.players.first { p -> p.playerType == PlayerType.B }
	private val buildings: List<Building> = gameState.gameMap.flatten().map { cellStateContainer -> cellStateContainer.buildings }.flatten()
	private val missiles: List<Missile> = gameState.gameMap.flatten().map { cellStateContainer -> cellStateContainer.missiles }.flatten()

	fun run(): String {
		return when {
			shouldDefendAnyRow() -> tryDefendRow()
			canBuildEnergyBuilding() -> tryBuildEnergyBuilding()
			else -> doNothingCommand()
		}
	}

	/* ===== Checks for if the row is an energy or attack row. ===== */
	private val energyRowEvenness = 1
	private val attackRowEvenness = 0

	private fun isEnergyRow(row: Int) = row % 2 == energyRowEvenness
	private fun isAttackRow(row: Int) = row % 2 == attackRowEvenness


	/* ===== Energy checks and handlers ===== */
	private fun getFirstEmptyEnergyCell(row: Int): Cell? = rowCells(row, myself.playerType).firstOrNull { it.buildings.isEmpty() && it.x < minDefenseIndex }
	private fun canBuildEnergyBuilding(row: Int): Boolean = BuildingType.ENERGY.canAfford(myself) && getFirstEmptyEnergyCell(row) != null
	private fun buildEnergyBuilding(row: Int): String {
		if (!isEnergyRow(row)) {
			return doNothingCommand()
		}

		val firstEnergyCell = getFirstEmptyEnergyCell(row) ?: throw IllegalStateException("No space for an energy building in row")
		return buildCommand(firstEnergyCell.x, firstEnergyCell.y, BuildingType.ENERGY)
	}
	private fun canBuildEnergyBuilding(): Boolean = (0 until gameHeight).filter { isEnergyRow(it) }.any { canBuildEnergyBuilding(it) }
	private fun tryBuildEnergyBuilding(): String {
		return (0 until gameHeight).filter { isEnergyRow(it) }.firstOrNull { canBuildEnergyBuilding(it) }?.let {
			buildEnergyBuilding(it)
		} ?: doNothingCommand()
	}


	//<editor-fold desc="Defense checks and handlers">
	private val minDefenseIndex = (Math.max(0, (gameWidth / 2).let { it - it / 5 } - 1)).let { System.out.printf("min defense index: %d%n", it); it }

	/**
	 * @return true if the row has an enemy attack building.
	 */
	private fun isRowUnderAttack(row: Int): Boolean {
		return rowCells(row, opponent.playerType).any { it.buildings.any { it.buildingType == BuildingType.ATTACK } }
	}

	/**
	 * @return return true if the row contains a friendly defense building.
	 */
	private fun isRowDefended(row: Int): Boolean {
		return rowCells(row, myself.playerType).any { it.buildings.any { it.buildingType == BuildingType.DEFENSE } }
	}

	/**
	 * @return true if the row has an opponent attack building and doesn't have a friendly defense building.
	 */
	private fun shouldDefendRow(row: Int): Boolean = isRowUnderAttack(row) && !isRowDefended(row)

	/**
	 * @return the first empty tile designated for defense buildings in the row or null if no tile in the row matches
	 * this criteria.
	 */
	private fun getFirstEmptyDefenseTile(row: Int): Cell? {
		return rowCells(row, myself.playerType).firstOrNull { it.x >= minDefenseIndex && it.buildings.isEmpty() }
	}

	/**
	 * @return true if you can afford a defense building and there is an empty tile for the building in the row.
	 */
	private fun canDefendRow(row: Int): Boolean {
		return BuildingType.DEFENSE.canAfford(myself) && getFirstEmptyDefenseTile(row) != null
	}

	/**
	 * @return a command to build a defense building in the first open tile starting from the enemy's side in the
	 * specified row.
	 */
	private fun defendRow(row: Int): String {
		val tile = getFirstEmptyDefenseTile(row)
				?: throw IllegalStateException("There is no space for a defense building in row $row")
		return if (BuildingType.DEFENSE.canAfford(myself)) buildCommand(tile.x, tile.y, BuildingType.DEFENSE) else doNothingCommand()
	}

	/**
	 * @return true if any row on the map should and can be defended.
	 */
	private fun shouldDefendAnyRow(): Boolean = (0 until gameHeight).any { shouldDefendRow(it) }

	/**
	 * @return a build command to defend the row if possible
	 */
	private fun tryDefendRow(): String {
		if (!BuildingType.DEFENSE.canAfford(myself))
			return doNothingCommand()

		val rowNeedsDefending = (0 until gameHeight).firstOrNull { shouldDefendRow(it) } ?: throw IllegalStateException("No rows need defending")
		return defendRow(rowNeedsDefending)
	}
	//</editor-fold>

	/* Utility functions */
	private fun rowCells(row: Int, player: PlayerType) = gameState.gameMap[row].filter { it.cellOwner == player }

	private fun doNothingCommand(): String = ""
	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String = StringBuilder().append(x).append(",").append(y).append(",").append(buildingType.id).toString()


	/* ======================================================================== */
	/* ============================== DEPRECATED ============================== */
	/* ======================================================================== */

//	/** Greatest index on the map that my energy buildings can (should) occupy. */
//	private val maxEnergyColIndex: Int = gameWidth / 10
//	/** Lowest index on the map that my defense buildings can (should) occupy. */
//	private val minDefenseColIndex: Int = (gameWidth / 2).let { it - it / 5 - 1 }
//
//	/** @return true if there is an enemy missile in the row. */
//	private fun isRowUnderAttack(row: Int): Boolean = missiles.firstOrNull { it.y == row && it.playerType == opponent.playerType } != null
//
//	/** @return true if there is a defense building in the row. */
//	private fun isRowDefended(row: Int): Boolean = getAllBuildingsForPlayer(myself.playerType, { it.isDefenceBuilding() }, row).isNotEmpty()
//
//	/** @return true if there is a missile in the row and there is no defense building in the row. */
//	private fun shouldDefendRow(row: Int): Boolean = isRowUnderAttack(row) && !isRowDefended(row)
//
//	/**
//	 * Checks three conditions: (1) if the player can afford a defense building, (2) if there is a missile in the row,
//	 * and (3) if there is an empty tile far away enough for the player to build a defense building in time.
//	 *
//	 * @return true if all above conditions are met, false is any one or more above conditions are not met.
//	 */
//	private fun canDefendRow(row: Int): Boolean {
//		val canAffordDefense = myself.energy >= BuildingType.DEFENSE.cost
//		if (!canAffordDefense) {
//			return false
//		}
//
//		val firstMissile = missiles.firstOrNull { it.y == row } ?: return false
//		val firstEmptyTile = gameState.gameMap[row].filter { it.cellOwner == myself.playerType }.reversed().firstOrNull { it.buildings.isEmpty() }
//				?: return false
//		val distanceBetweenEmptyTileAndMissile = firstEmptyTile.x - firstMissile.x
//
//		return distanceBetweenEmptyTileAndMissile > firstMissile.speed * BuildingType.DEFENSE.constructionTime
//	}
//
//	/**
//	 * Finds the first row that should and can be defended and returns its index.
//	 *
//	 * If no rows that should and can be defended are found, null is returned.
//	 *
//	 * @return the index of the first row that should and can be defended, null if no such row is found.
//	 */
//	private fun getRowNeedsDefending(): Int? {
//		for (row in 0 until gameHeight) {
//			if (shouldDefendRow(row) && canDefendRow(row)) {
//				return row
//			}
//		}
//		return null
//	}
//
//	private fun haveRowsWithoutDefense(): Boolean = (0 until gameHeight).any { !hasDefense(it) }
//	private fun hasDefense(row: Int): Boolean = buildings.any { it.buildingType == BuildingType.DEFENSE && it.y == row && it.owner == myself.playerType }
//	private fun defendRow(row: Int): String {
//		return gameState.gameMap.flatten().filter { it.y == row && it.cellOwner == myself.playerType }.reversed().firstOrNull { it.x >= minDefenseColIndex && it.buildings.isEmpty() }?.let{
//			buildCommand(it.x, it.y, BuildingType.DEFENSE)
//		} ?: throw IllegalStateException("There are no empty tiles for defense buildings")
//	}
//
//	/* ===== Energy building check and handler ===== */
//	private fun getFirstSpaceForEnergyBuilding(): Cell? = gameState.gameMap.flatten().firstOrNull { it.x <= maxEnergyColIndex && it.buildings.isEmpty() }
//	private fun canBuildEnergyBuilding(): Boolean = BuildingType.ENERGY.cost <= myself.energy && getFirstSpaceForEnergyBuilding() != null
//	private fun buildEnergyBuilding(): String {
//		return getFirstSpaceForEnergyBuilding()?.let {
//			buildCommand(it.x, it.y, BuildingType.ENERGY)
//		} ?: throw IllegalStateException("No space for an energy building")
//	}
//
//	private fun buildRandom(): String {
//		val emptyCells = gameState.gameMap.flatten()
//				.filter { c -> c.buildings.isEmpty() && c.x < gameWidth / 2 }
//		if (emptyCells.isEmpty()) {
//			return doNothingCommand()
//		}
//		val randomEmptyCell = getRandomElementOfList(emptyCells)
//		val buildingTypes = ArrayList(gameDetails.buildingPrices.keys)
//		val randomBuildingType = getRandomElementOfList(buildingTypes)
//		return if (!canAffordBuilding(randomBuildingType)) {
//			doNothingCommand()
//		} else buildCommand(randomEmptyCell.x, randomEmptyCell.y, randomBuildingType)
//	}
//
//	private fun hasEnoughEnergyForMostExpensiveBuilding(): Boolean {
//		return gameDetails.buildingPrices.values.stream()
//				.filter { bp -> bp < myself.energy }
//				.toArray().size == 3
//	}
//
//	private fun doNothingCommand(): String {
//		return ""
//	}
//
//	private fun placeBuildingInRow(buildingType: BuildingType, y: Int): String {
//		val emptyCells = gameState.gameMap.flatten()
//				.filter {
//					(it.buildings.isEmpty()
//							&& it.y == y
//							&& it.x < gameWidth / 2 - 1)
//				}
//		if (emptyCells.isEmpty()) {
//			return buildRandom()
//		}
//		val randomEmptyCell = getRandomElementOfList(emptyCells)
//		return buildCommand(randomEmptyCell.x, randomEmptyCell.y, buildingType)
//	}
//
//	private fun <T> getRandomElementOfList(list: List<T>): T {
//		return list[Random().nextInt(list.size)]
//	}
//
//	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String {
//		return StringBuilder().let {
//			it.append(x).append(",").append(y).append(",")
//
//			when (buildingType) {
//				BuildingType.DEFENSE -> it.append("0")
//				BuildingType.ATTACK -> it.append("1")
//				BuildingType.ENERGY -> it.append("2")
//			}
//
//			it.toString()
//		}
//	}
//
//	private fun getAllBuildingsForPlayer(playerType: PlayerType, filter: (Building) -> Boolean, y: Int): List<Building> {
//		return buildings
//				.filter { b -> b.owner == playerType && b.y == y }
//				.filter(filter)
//	}
//
//	private fun canAffordBuilding(buildingType: BuildingType): Boolean {
//		val cost = when (buildingType) {
//			BuildingType.DEFENSE -> gameDetails.buildingPrices.getOrDefault(BuildingType.DEFENSE, 100000)
//			BuildingType.ATTACK -> gameDetails.buildingPrices.getOrDefault(BuildingType.ATTACK, 100000)
//			BuildingType.ENERGY -> gameDetails.buildingPrices.getOrDefault(BuildingType.ENERGY, 100000)
//		}
//		return myself.energy >= cost
//	}

}
