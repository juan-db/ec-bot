package juandb.entelect.challenge.entity

import com.fasterxml.jackson.annotation.JsonProperty
import juandb.entelect.challenge.entity.Player.PlayerType

data class Cell(
		val x: Int,
		val y: Int,
		@JsonProperty("cellOwner")
		val owner: PlayerType,
		val buildings: List<Building>,
		val missiles: List<Missile>)
