package juandb.entelect.challenge.entity

import com.google.gson.annotations.SerializedName
import juandb.entelect.challenge.entity.Player.PlayerType

data class Missile(
		val x: Int, val y: Int,
		@SerializedName("playerType")
		val owner: PlayerType,
		val damage: Int,
		val speed: Int)