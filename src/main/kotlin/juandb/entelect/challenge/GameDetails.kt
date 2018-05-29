package juandb.entelect.challenge

import juandb.entelect.challenge.Building.BuildingType
import java.util.*

data class GameDetails(
		val round: Int,
		val mapWidth: Int,
		val mapHeight: Int,
		val buildingPrices: HashMap<BuildingType, Int>)