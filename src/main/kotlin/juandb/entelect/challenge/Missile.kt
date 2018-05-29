package juandb.entelect.challenge

import juandb.entelect.challenge.Player.PlayerType

data class Missile(
        val x: Int, val y: Int, val playerType: PlayerType,
        val damage: Int,
        val speed: Int)