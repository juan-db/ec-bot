package juandb.entelect.challenge.entity

import juandb.entelect.challenge.entity.Building.BuildingType
import java.util.*

data class GameDetails(
		val round: Int,
		val maxRounds: Int,
		val mapWidth: Int,
		val mapHeight: Int,
		val roundIncomeEnergy: Int,
		val buildingsStats: HashMap<BuildingType, BuildingStats>)