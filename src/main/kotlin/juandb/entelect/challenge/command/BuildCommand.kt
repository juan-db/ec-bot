package juandb.entelect.challenge.command

import juandb.entelect.challenge.entity.Building

class BuildCommand(private val x: Int, private val y: Int, private val buildingType: Building.BuildingType) : Command {
	companion object {
		/**
		 * @return the build command for the given building type at the given coordinates.
		 */
		fun buildCommand(x: Int, y: Int, buildingType: Building.BuildingType): String {
			return "$x,$y,${buildingType.id}"
		}
	}

	override fun getWeight(): Int = 0 // TODO
	override fun getCommand(): String = buildCommand(x, y, buildingType)

	override fun toString(): String = "${this.javaClass.name}[x:$x;y:$y;type:${buildingType.name}]"
}