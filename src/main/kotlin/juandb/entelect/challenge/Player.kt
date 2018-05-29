package juandb.entelect.challenge

data class Player(
        val playerType: PlayerType,
        val energy: Int,
        val health: Int,
        val hitsTaken: Int,
        val score: Int) {

    enum class PlayerType {
        A,
        B
    }
}