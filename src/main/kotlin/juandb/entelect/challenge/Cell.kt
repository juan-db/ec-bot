package juandb.entelect.challenge

import juandb.entelect.challenge.Player.PlayerType

data class Cell(
		val x: Int, val y: Int, val cellOwner: PlayerType,
		val buildings: List<Building>,
		val missiles: List<Missile>)