package juandb.entelect.challenge.entity

data class BuildingStats(val health: Int,
                         val constructionTime: Int,
                         val price: Int,
                         val weaponDamage: Int,
                         val weaponSpeed: Int,
                         val weaponCooldownPeriod: Int,
                         val energyGeneratedPerTurn: Int,
                         val destroyMultiplier: Int,
                         val constructionScore: Int) {
	fun canAfford(player: Player) = player.energy >= price
}
