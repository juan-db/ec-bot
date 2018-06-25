package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.Building
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.entity.BuildingStats
import juandb.entelect.challenge.entity.GameState
import java.util.*
import kotlin.math.min

class BuildCommand(gameState: GameState,
                   private val x: Int,
                   private val y: Int,
                   private val buildingType: Building.BuildingType)
	: Command(gameState) {
	companion object {
		/**
		 * @return the build command for the given building type at the given coordinates.
		 */
		fun buildCommand(x: Int, y: Int, buildingType: Building.BuildingType): String {
			return "$x,$y,${buildingType.id}"
		}

		private val random: Random = Random()
	}

	private val mapWidth: Int = gameState.gameDetails.mapWidth
	private val myWidth: Int = mapWidth / 2
	private val mapHeight: Int = gameState.gameDetails.mapHeight

	private val buildingsStats: Map<BuildingType, BuildingStats> = gameState.gameDetails.buildingsStats

	override fun getWeight(): Int {
		if (buildingType == BuildingType.ENERGY) {
			val idealEnergyBuildingCount = min(myWidth * mapHeight / 3,
			                                   buildingsStats.values.maxBy { it.price }!!.price.let {
				                                   it / (buildingsStats[BuildingType.ENERGY]?.energyGeneratedPerTurn
						                                   ?: it)
			                                   })
			val currentEnergyBuildingCount = gameState.getRows()
					.sumBy { it.cells.count { it.buildings.any { it.buildingType == BuildingType.ENERGY } } }
			return idealEnergyBuildingCount - currentEnergyBuildingCount
		}
		return random.nextInt(10)
	}

	override fun getCommand(): String = buildCommand(x, y, buildingType)

	override fun toString(): String = "${this.javaClass.name}[x:$x;y:$y;type:${buildingType.name}]"
}