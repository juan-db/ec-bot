package juandb.entelect.challenge

import juandb.entelect.challenge.Player.PlayerType

data class Building(
		val x: Int, val y: Int, val owner: PlayerType,
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

	/* TODO values in here are hard-coded because afaik they don't get provided in the state.json file. */
    enum class BuildingType(val id: Int, val cost: Int, val health: Int, val constructionTime: Int,
                            val constructedCharacter: Char, val underConstructionCharacter: Char) {
        DEFENSE(0, 30, 20, 3, 'D', 'd'),
        ATTACK(1, 30, 5, 1, 'A', 'a'),
        ENERGY(2, 20, 5, 1, 'E', 'e');

		fun canAfford(player: Player) = this.cost <= player.energy
    }

    fun isAttackBuilding() : Boolean {
        return buildingType == BuildingType.ATTACK
    }

    fun isDefenceBuilding() : Boolean {
        return buildingType == BuildingType.DEFENSE
    }
}
