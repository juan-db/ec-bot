package juandb.entelect.challenge.entity

import com.google.gson.annotations.SerializedName
import juandb.entelect.challenge.entity.Player.PlayerType

data class Building(
		val x: Int,
		val y: Int,
		@SerializedName("playerType")
		val owner: PlayerType,
		val health: Int,
		val constructionTimeLeft: Int,
		val price: Int,
		val weaponDamage: Int,
		val weaponSpeed: Int,
		val weaponCooldownTimeLeft: Int,
		val weaponCooldownPeriod: Int,
		val destroyMultiplier: Int,
		val constructionScore: Int,
		val energyGeneratedPerTurn: Int,
		val buildingType: BuildingType) {
	enum class BuildingType(val id: Int) {
		DEFENSE(0), ATTACK(1), ENERGY(2), TESLA(4);
	}
}
