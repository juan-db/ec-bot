package juandb.entelect.challenge.entity

import juandb.entelect.challenge.entity.Player.PlayerType

data class Cell(
		val x: Int, val y: Int, val owner: PlayerType,
		val buildings: List<Building>,
		val missiles: List<Missile>)
