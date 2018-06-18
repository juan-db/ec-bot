package juandb.entelect.challenge.entity

import juandb.entelect.challenge.entity.Building.BuildingType
import java.util.*

data class GameDetails(
		val round: Int,
		val mapWidth: Int,
		val mapHeight: Int,
		val buildingPrices: HashMap<BuildingType, Int>)