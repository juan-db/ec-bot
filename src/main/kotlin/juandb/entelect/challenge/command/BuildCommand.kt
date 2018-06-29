package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.Building
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.entity.BuildingStats
import juandb.entelect.challenge.entity.GameState
import juandb.entelect.challenge.util.logger
import java.util.*
import kotlin.math.min

abstract class BuildCommand(gameState: GameState,
                            protected val x: Int,
                            protected val y: Int,
                            protected val buildingType: Building.BuildingType)
	: Command(gameState) {
	companion object {
		val logger by logger()
		/**
		 * @return the build command for the given building type at the given coordinates.
		 */
		fun buildCommand(x: Int, y: Int, buildingType: Building.BuildingType): String {
			return "$x,$y,${buildingType.id}"
		}

		private val random: Random = Random()

		fun newBuildCommand(gameState: GameState, x: Int, y: Int, buildingType: BuildingType): BuildCommand {
			return when (buildingType) {
				BuildingType.DEFENSE -> object : BuildCommand(gameState, x, y, buildingType) {
					override fun getWeight(): Int {
						return gameState.rows.firstOrNull { it.index == y }?.let {
							it.enemyAttackBuildings * 2 - it.friendlyDefenseBuildings
						} ?: 0
					}
				}

				BuildingType.ATTACK -> object : BuildCommand(gameState, x, y, buildingType) {
					override fun getWeight(): Int {
						return gameState.rows.firstOrNull { it.index == y }?.let {
							myWidth - it.enemyDefenseBuildings * 3
						} ?: 0
					}
				}

				BuildingType.ENERGY -> object : BuildCommand(gameState, x, y, buildingType) {
					override fun getWeight(): Int {
						val leastExpensiveBuildingPrice = buildingsStats.values.minBy { it.price }!!.price
						val necessaryGenerationPerTurn = leastExpensiveBuildingPrice - gameState.gameDetails.roundIncomeEnergy
						val energyBuildingEnergyPerTurn = buildingsStats[BuildingType.ENERGY]!!.energyGeneratedPerTurn
						// Build enough energy buildings so you generate enough energy to build the least expensive
						// building each turn plus some extra energy buildings for good measure.
						val idealEnergyBuildingCount = min(myWidth * mapHeight / 3,
						                                   necessaryGenerationPerTurn / energyBuildingEnergyPerTurn + 3)
						val currentEnergyBuildingCount = gameState.rows.sumBy { it.friendlyEnergyBuildings }

						val currentEnergy = gameState.myself?.energy ?: 0

						return idealEnergyBuildingCount - currentEnergyBuildingCount - currentEnergy / 50
					}
				}

				else -> object : BuildCommand(gameState, x, y, buildingType) {
					override fun getWeight(): Int {
						return random.nextInt(5)
					}
				}
			}
		}
	}

	protected val mapWidth: Int = gameState.gameDetails.mapWidth
	protected val myWidth: Int = mapWidth / 2
	protected val mapHeight: Int = gameState.gameDetails.mapHeight

	protected val buildingsStats: Map<BuildingType, BuildingStats> = gameState.gameDetails.buildingsStats

	override fun getCommand(): String = buildCommand(x, y, buildingType)

	override fun toString(): String = "${this.javaClass.name}[x:$x;y:$y;type:${buildingType.name}]"
}