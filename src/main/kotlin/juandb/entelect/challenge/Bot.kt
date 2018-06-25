package juandb.entelect.challenge

import juandb.entelect.challenge.command.BuildCommand
import juandb.entelect.challenge.command.Command
import juandb.entelect.challenge.command.DeconstructCommand
import juandb.entelect.challenge.command.DoNothingCommand
import juandb.entelect.challenge.entity.*
import juandb.entelect.challenge.entity.Building.BuildingType
import juandb.entelect.challenge.util.logger

class Bot(private val gameState: GameState) {
	private val logger by logger()

	private val gameDetails: GameDetails = gameState.gameDetails

	init {
		logger.info("========== Round: ${gameDetails.round} ==========")
	}

	private val buildingStats: Map<BuildingType, BuildingStats> = gameDetails.buildingsStats

	/* Dimensions */
	private val gameWidth: Int = gameDetails.mapWidth
	private val gameHeight: Int = gameDetails.mapHeight
	/** The width of my side of the board. */
	private val myWidth: Int = gameWidth / 2

	/* Players */
	private val myself: Player = gameState.players.first { it.playerType == Player.PLAYER }
	private val opponent: Player = gameState.players.first { it.playerType == Player.ENEMY }

	private val mostExpensiveBuilding = buildingStats.maxBy { it.value.price }!!.key

	private val rows = gameState.gameMap.mapIndexed { index, cells -> RowState(index, cells) }
	private val buildableRows = rows.filter { it.friendlyEmptyCells.isNotEmpty() }
	private val occupiedRows = rows.filter { it.friendlyOccupiedCells.isNotEmpty() }

	private val commands: List<Command> = generateAvailableCommands()

	init {
		val sb = StringBuilder("Possible command count: ${commands.size}\n")
		commands.forEach { sb.append(it).appendln() }
		logger.info(sb.toString())
	}

	fun run(): String {
		return commands.maxBy { it.getWeight() }!!.getCommand()
	}

	private fun generateAvailableCommands(): List<Command> {
		val output = ArrayList<Command>()

		/* Do nothing command. */
		output.add(DoNothingCommand(gameState))

		/* Add all possible build commands where I can afford the building. */
		val affordableBuildings = gameDetails.buildingsStats.filter { (_, stats) -> stats.canAfford(myself) }
		output.addAll(affordableBuildings.flatMap { building ->
			buildableRows.flatMap { row ->
				row.friendlyEmptyCells.map { cell ->
					BuildCommand(gameState, cell.x, cell.y, building.key)
				}
			}
		})

		/* Add deconstruction command for all cells with buildings. */
		output.addAll(occupiedRows.flatMap { row ->
			row.friendlyOccupiedCells.map {
				DeconstructCommand(gameState, it.x, it.y)
			}
		})

		return output
	}
}
