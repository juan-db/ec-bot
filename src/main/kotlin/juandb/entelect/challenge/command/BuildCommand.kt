package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.Building
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.entity.BuildingStats
import juandb.entelect.challenge.entity.GameState
import juandb.entelect.challenge.entity.Player
import java.util.Random
import kotlin.math.max
import kotlin.math.min

abstract class BuildCommand(gameState: GameState,
                            protected val x: Int,
                            protected val y: Int,
                            protected val buildingType: Building.BuildingType)
	: Command(gameState) {
	companion object {
		/**
		 * @return the build command for the given building type at the given coordinates.
		 */
		fun buildCommand(x: Int, y: Int, buildingType: Building.BuildingType): String {
			return "$x,$y,${buildingType.id}"
		}

		private class DefenseCommand(gameState: GameState, x: Int, y: Int, buildingType: BuildingType)
			: BuildCommand(gameState, x, y, buildingType) {
			override fun getWeight(): Double {
				return gameState.getRows().firstOrNull { it.index == y }?.let {
					val urgency = it.enemyAttackBuildings * 4 - it.friendlyDefenseBuildings
					val firstMissile = it.cells.map { it.missiles.firstOrNull { it.owner == Player.ENEMY } }.firstOrNull { it != null }
					val missileDistance = (firstMissile?.x ?: x) - x
					val valuableBuildingsBefore = it.friendlyOccupiedCells.filter { it.x < x }
							.count { it.buildings.any { it.buildingType == BuildingType.ATTACK }
									|| it.buildings.any { it.buildingType == BuildingType.ENERGY } }
					urgency + valuableBuildingsBefore - missileDistance / (firstMissile?.speed ?: 1)
				}?.toDouble() ?: 0.0
			}
		}

		private class AttackCommand(gameState: GameState, x: Int, y: Int, buildingType: BuildingType)
			: BuildCommand(gameState, x, y, buildingType) {
			override fun getWeight(): Double {
				return gameState.getRows().firstOrNull { it.index == y }?.let {
					// Not a good idea to attack a row with a lot of enemy defense buildings
					val defenseBias = myWidth - it.enemyDefenseBuildings * 3
					// Good idea to attack row with a lot of valuable buildings
					val valuableBias = it.enemyAttackBuildings + it.enemyEnergyBuildings * 1.5
					return defenseBias + valuableBias
				} ?: 0.0
			}
		}

		private class EnergyCommand(gameState: GameState, x: Int, y: Int, buildingType: BuildingType)
			: BuildCommand(gameState, x, y, buildingType) {
			override fun getWeight(): Double {
				val mostExpensiveEssentialBuilding = max(buildingsStats[BuildingType.DEFENSE]!!.price,
				                                         buildingsStats[BuildingType.ATTACK]!!.price)
				val necessaryGenerationPerTurn = mostExpensiveEssentialBuilding - gameState.gameDetails.roundIncomeEnergy
				val energyBuildingEnergyPerTurn = buildingsStats[BuildingType.ENERGY]!!.energyGeneratedPerTurn
				// Build enough energy buildings so you generate enough energy to build the least expensive
				// building each turn plus some extra energy buildings for good measure.
				val idealEnergyBuildingCount = min(myWidth * mapHeight / 3,
				                                   necessaryGenerationPerTurn / energyBuildingEnergyPerTurn + 3)
				val currentEnergyBuildingCount = gameState.getRows().sumBy { it.friendlyEnergyBuildings }

				val currentEnergy = gameState.myself?.energy ?: 0

				var weight = idealEnergyBuildingCount - currentEnergyBuildingCount
				-currentEnergy / 50
				-y / (myWidth + 1)

				return weight.toDouble()
			}
		}

		fun newBuildCommand(gameState: GameState, x: Int, y: Int, buildingType: BuildingType): BuildCommand {
			return when (buildingType) {
				BuildingType.DEFENSE -> DefenseCommand(gameState, x, y, buildingType)
				BuildingType.ATTACK -> AttackCommand(gameState, x, y, buildingType)
				BuildingType.ENERGY -> EnergyCommand(gameState, x, y, buildingType)
				else -> object : BuildCommand(gameState, x, y, buildingType) {
					override fun getWeight(): Double = Math.random() * 5.0
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