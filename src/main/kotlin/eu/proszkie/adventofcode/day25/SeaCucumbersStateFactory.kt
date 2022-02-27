package eu.proszkie.adventofcode.day25

object SeaCucumbersStateFactory {
    fun fromStringLines(input: List<String>): SeaCucumbersState =
        input.flatMapIndexed { yCoord, line -> collectCucumbersWithCoords(yCoord, line) }
            .associate { it.coords to it.element }
            .let(::SeaCucumbersState)

    private fun collectCucumbersWithCoords(yCoord: Int, line: String): List<MapElementWithCoords> =
        line.mapIndexed { xCoord, token -> collectCucumberWithCoords(Coords(xCoord, yCoord), token) }

    private fun collectCucumberWithCoords(coords: Coords, token: Char) =
        MapElementWithCoords(MapElement.from(token), coords)
}

data class MapElementWithCoords(
    val element: MapElement,
    val coords: Coords
)

sealed class MapElement {
    companion object {
        fun from(token: Char): MapElement =
            when (token) {
                '>' -> GoingRightCucumber
                'v' -> GoingDownCucumber
                '.' -> EmptySpace
                else -> throw IllegalStateException("Unknown token: $token")
            }
    }
}

object GoingRightCucumber : MapElement() {
    override fun toString(): String = ">"
}

object GoingDownCucumber : MapElement() {
    override fun toString(): String = "v"
}

object EmptySpace : MapElement() {
    override fun toString(): String = "."
}

data class Coords(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}