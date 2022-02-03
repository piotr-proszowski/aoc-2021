package eu.proszkie.adventofcode.day23.awfulapproach

data class Room(val allCoords: Set<Coords>, val forToken: Char) {
    val coordsInFrontOfRoom = allCoords.minByOrNull { it.y }!!.up()
    fun isFinal(elements: Map<Coords, Element>) = allElementsInRoom(elements)
        .allAreMovable()
        .and { it.allAreTheSame() }

    fun contains(coords: Coords) = allCoords.contains(coords)

    fun amountOfMovablesInRoom(elements: Map<Coords, Element>) = allElementsInRoom(elements).count(Element::isMovable)

    private fun allElementsInRoom(elements: Map<Coords, Element>) = allCoords.mapNotNull { elements[it] }

    private fun Collection<Element>.allAreMovable() = this.all(Element::isMovable) to this

    private fun Collection<Element>.allAreTheSame() = this.toSet().size == 1

    private infix fun Pair<Boolean, Collection<Element>>.and(other: (Collection<Element>) -> Boolean) =
        this.first && other.invoke(this.second)

    fun isTheDeepestCoord(y: Int): Boolean {
        return allCoords.maxOf { it.y } == y
    }

    fun movablesInRoom(elements: Map<Coords, Element>): Collection<Element> {
        return elements.filter { contains(it.key) }
            .filter { it.value.isMovable }
            .values
    }

}