package juandb.entelect.challenge.entity

import juandb.entelect.challenge.entity.Player.PlayerType

data class Missile(
		val x: Int, val y: Int, val owner: PlayerType,
		val damage: Int,
		val speed: Int)