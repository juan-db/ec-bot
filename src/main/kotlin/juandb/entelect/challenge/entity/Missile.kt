package juandb.entelect.challenge.entity

import com.fasterxml.jackson.annotation.JsonProperty
import juandb.entelect.challenge.entity.Player.PlayerType

data class Missile(
		val x: Int, val y: Int,
		@JsonProperty("playerType")
		val owner: PlayerType,
		val damage: Int,
		val speed: Int)