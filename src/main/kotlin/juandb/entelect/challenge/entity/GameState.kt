package juandb.entelect.challenge.entity

data class GameState(val players: Array<Player>,
					 val gameMap: Array<Array<Cell>>,
					 val gameDetails: GameDetails) {
	val rows = gameMap.mapIndexed { index, cells -> RowState(cells, index) }
}