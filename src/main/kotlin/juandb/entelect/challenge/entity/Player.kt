package juandb.entelect.challenge.entity

data class Player(
		val playerType: PlayerType,
		val energy: Int,
		val health: Int,
		val hitsTaken: Int,
		val score: Int) {
	companion object {
		val PLAYER = PlayerType.A
		val ENEMY = PlayerType.B
	}

    enum class PlayerType {
        A,
        B
    }
}