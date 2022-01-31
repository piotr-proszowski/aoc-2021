package eu.proszkie.adventofcode.day23

object BurrowFactory {
    fun fromString(raw: String): Burrow {
        val elements = raw.split("\n")
            .filter(String::isNotBlank)
            .withIndex()
            .flatMap(this::processLine)
            .associate { it.coords to it.element }

        val rooms = findRooms(elements)
        return Burrow(rooms, BurrowsCache.saveOrFindExisting(elements))
    }

    private fun processLine(it: IndexedValue<String>): List<CoordsWithElement> {
        val (y, line) = it
        return line.withIndex().map {
            val (x, element) = it
            processToken(Coords(x, y), element)
        }
    }

    private fun processToken(coords: Coords, token: Char): CoordsWithElement {
        return CoordsWithElement(
            coords = coords,
            element = (ElementFactory.fromToken(token) ?: Element.OutOfBurrow)
        )
    }

    data class CoordsWithElement(val coords: Coords, val element: Element)

    private fun findRooms(rawBurrow: Map<Coords, Element>): Collection<Room> {
        val tokens = listOf('A', 'B', 'C', 'D')
        return rawBurrow.map { it.key }
            .mapNotNull { coords -> findRoom(coords, rawBurrow) }
            .toSet()
            .mapIndexed { index, room ->
                room.copy(forToken = tokens[index])
            }
    }

    private fun findRoom(coords: Coords, rawBurrow: Map<Coords, Element>): Room? {
        if (isInsideRoom(coords, rawBurrow)) {
            val up = generateSequence(coords.up(), Coords::up).takeWhile { isInsideRoom(it, rawBurrow) }.toSet()
            val down = generateSequence(coords.down(), Coords::down).takeWhile { isInsideRoom(it, rawBurrow) }.toSet()

            return Room(up.plus(down).plus(coords).filter { rawBurrow[it] != Element.Wall }.toSet(), 'X')
        }

        return null
    }

    private fun isInsideRoom(coords: Coords, rawBurrow: Map<Coords, Element>): Boolean {
        return !rawBurrow[coords].isWall() && rawBurrow[coords.left()].isWall() && rawBurrow[coords.right()].isWall()
    }

    private fun Element?.isWall() = this == Element.Wall
}