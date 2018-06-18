package juandb.entelect.challenge.entity

import com.google.gson.annotations.SerializedName
import juandb.entelect.challenge.entity.Player.PlayerType

data class Cell(
		val x: Int,
		val y: Int,
		@SerializedName("cellOwner")
		val owner: PlayerType,
		val buildings: List<Building>,
		val missiles: List<Missile>)
