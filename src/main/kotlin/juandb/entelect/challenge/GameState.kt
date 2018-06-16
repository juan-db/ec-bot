package juandb.entelect.challenge

data class GameState(
		val players: Array<Player>,
		val gameMap: Array<RowState>,
		var gameDetails: GameDetails)