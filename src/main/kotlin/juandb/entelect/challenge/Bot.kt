package juandb.entelect.challenge

import juandb.entelect.challenge.Building.BuildingType
import juandb.entelect.challenge.Player.PlayerType
import java.util.*

class Bot(private val gameState: GameState) {
	private val gameDetails: GameDetails = gameState.gameDetails
	private val gameWidth: Int = gameDetails.mapWidth
	private val gameHeight: Int = gameDetails.mapHeight
	private val myself: Player = gameState.players.first { p -> p.playerType == PlayerType.A }
	private val opponent: Player = gameState.players.first { p -> p.playerType == PlayerType.B }
	private val buildings: List<Building> = gameState.gameMap.flatten().map { cellStateContainer -> cellStateContainer.buildings }.flatten()
	private val missiles: List<Missile> = gameState.gameMap.flatten().map { cellStateContainer -> cellStateContainer.missiles }.flatten()

	/** Greatest index on the map that my energy buildings can (should) occupy. */
	private val maxEnergyColIndex: Int = gameWidth / 10
	/** Lowest index on the map that my defense buildings can (should) occupy. */
	private val minDefenseColIndex: Int = (gameWidth / 2).let { it - it / 5 - 1 }

	fun run(): String {
		println("my energy = ${myself.energy}; building cost = ${BuildingType.ENERGY.cost}")

		return when {
			canBuildEnergyBuilding() -> buildEnergyBuilding()
			haveRowsWithoutDefense() -> defendRow((0 until gameHeight).first{ !hasDefense(it) })
			hasEnoughEnergyForMostExpensiveBuilding() -> buildRandom()
			else -> doNothingCommand()
		}
	}

	/** @return true if there is an enemy missile in the row. */
	private fun isUnderAttack(row: Int): Boolean = missiles.firstOrNull { it.y == row && it.playerType == opponent.playerType } != null

	/** @return true if there is a defense building in the row. */
	private fun isDefended(row: Int): Boolean = getAllBuildingsForPlayer(myself.playerType, { it.isDefenceBuilding() }, row).isNotEmpty()

	/** @return true if there is a missile in the row and there is no defense building in the row. */
	private fun shouldDefendRow(row: Int): Boolean = isUnderAttack(row) && !isDefended(row)

	/**
	 * Checks three conditions: (1) if the player can afford a defense building, (2) if there is a missile in the row,
	 * and (3) if there is an empty tile far away enough for the player to build a defense building in time.
	 *
	 * @return true if all above conditions are met, false is any one or more above conditions are not met.
	 */
	private fun canDefendRow(row: Int): Boolean {
		val canAffordDefense = myself.energy >= BuildingType.DEFENSE.cost
		if (!canAffordDefense) {
			return false
		}

		val firstMissile = missiles.firstOrNull { it.y == row } ?: return false
		val firstEmptyTile = gameState.gameMap[row].filter { it.cellOwner == myself.playerType }.reversed().firstOrNull { it.buildings.isEmpty() }
				?: return false
		val distanceBetweenEmptyTileAndMissile = firstEmptyTile.x - firstMissile.x

		return distanceBetweenEmptyTileAndMissile > firstMissile.speed * BuildingType.DEFENSE.constructionTime
	}

	/**
	 * Finds the first row that should and can be defended and returns its index.
	 *
	 * If no rows that should and can be defended are found, null is returned.
	 *
	 * @return the index of the first row that should and can be defended, null if no such row is found.
	 */
	private fun getRowNeedsDefending(): Int? {
		for (row in 0 until gameHeight) {
			if (shouldDefendRow(row) && canDefendRow(row)) {
				return row
			}
		}
		return null
	}

	private fun haveRowsWithoutDefense(): Boolean = (0 until gameHeight).any { !hasDefense(it) }
	private fun hasDefense(row: Int): Boolean = buildings.any { it.buildingType == BuildingType.DEFENSE && it.y == row && it.owner == myself.playerType }
	private fun defendRow(row: Int): String {
		return gameState.gameMap.flatten().filter { it.y == row && it.cellOwner == myself.playerType }.reversed().firstOrNull { it.x >= minDefenseColIndex && it.buildings.isEmpty() }?.let{
			buildCommand(it.x, it.y, BuildingType.DEFENSE)
		} ?: throw IllegalStateException("There are no empty tiles for defense buildings")
	}

	/* ===== Energy building check and handler ===== */
	private fun getFirstSpaceForEnergyBuilding(): Cell? = gameState.gameMap.flatten().firstOrNull { it.x <= maxEnergyColIndex && it.buildings.isEmpty() }
	private fun canBuildEnergyBuilding(): Boolean = BuildingType.ENERGY.cost <= myself.energy && getFirstSpaceForEnergyBuilding() != null
	private fun buildEnergyBuilding(): String {
		return getFirstSpaceForEnergyBuilding()?.let {
			buildCommand(it.x, it.y, BuildingType.ENERGY)
		} ?: throw IllegalStateException("No space for an energy building")
	}

	private fun buildRandom(): String {
		val emptyCells = gameState.gameMap.flatten()
				.filter { c -> c.buildings.isEmpty() && c.x < gameWidth / 2 }
		if (emptyCells.isEmpty()) {
			return doNothingCommand()
		}
		val randomEmptyCell = getRandomElementOfList(emptyCells)
		val buildingTypes = ArrayList(gameDetails.buildingPrices.keys)
		val randomBuildingType = getRandomElementOfList(buildingTypes)
		return if (!canAffordBuilding(randomBuildingType)) {
			doNothingCommand()
		} else buildCommand(randomEmptyCell.x, randomEmptyCell.y, randomBuildingType)
	}

	private fun hasEnoughEnergyForMostExpensiveBuilding(): Boolean {
		return gameDetails.buildingPrices.values.stream()
				.filter { bp -> bp < myself.energy }
				.toArray().size == 3
	}

	private fun doNothingCommand(): String {
		return ""
	}

	private fun placeBuildingInRow(buildingType: BuildingType, y: Int): String {
		val emptyCells = gameState.gameMap.flatten()
				.filter {
					(it.buildings.isEmpty()
							&& it.y == y
							&& it.x < gameWidth / 2 - 1)
				}
		if (emptyCells.isEmpty()) {
			return buildRandom()
		}
		val randomEmptyCell = getRandomElementOfList(emptyCells)
		return buildCommand(randomEmptyCell.x, randomEmptyCell.y, buildingType)
	}

	private fun <T> getRandomElementOfList(list: List<T>): T {
		return list[Random().nextInt(list.size)]
	}

	private fun buildCommand(x: Int, y: Int, buildingType: BuildingType): String {
		return StringBuilder().let {
			it.append(x).append(",").append(y).append(",")

			when (buildingType) {
				BuildingType.DEFENSE -> it.append("0")
				BuildingType.ATTACK -> it.append("1")
				BuildingType.ENERGY -> it.append("2")
			}

			it.toString()
		}
	}

	private fun getAllBuildingsForPlayer(playerType: PlayerType, filter: (Building) -> Boolean, y: Int): List<Building> {
		return buildings
				.filter { b -> b.owner == playerType && b.y == y }
				.filter(filter)
	}

	private fun canAffordBuilding(buildingType: BuildingType): Boolean {
		val cost = when (buildingType) {
			BuildingType.DEFENSE -> gameDetails.buildingPrices.getOrDefault(BuildingType.DEFENSE, 100000)
			BuildingType.ATTACK -> gameDetails.buildingPrices.getOrDefault(BuildingType.ATTACK, 100000)
			BuildingType.ENERGY -> gameDetails.buildingPrices.getOrDefault(BuildingType.ENERGY, 100000)
		}
		return myself.energy >= cost
	}

}
