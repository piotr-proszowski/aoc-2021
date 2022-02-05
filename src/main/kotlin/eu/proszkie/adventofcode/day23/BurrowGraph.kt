package eu.proszkie.adventofcode.day23

class BurrowGraph(
    private val graph: Map<Coords, Set<Coords>>
) {


    fun pathBetween(pointA: Coords, pointB: Coords): Path? {
        return PathCache.findPathBetween(pointA, pointB) ?: PathCache.save(pointA, pointB,
            pathBetween(pointA, pointB, setOf())?.copy(startingPoint = pointA)
        )
    }

    private fun pathBetween(pointA: Coords, pointB: Coords, alreadySeen: Set<Coords>): Path? {
        if (pointA == pointB) {
            return Path(pointA, pointB, alreadySeen.plus(pointA))
        }

        if (alreadySeen.contains(pointA)) {
            return null
        }

        return graph[pointA]?.asSequence()
            ?.mapNotNull { visiting ->
                pathBetween(visiting, pointB, alreadySeen.plus(pointA))
            }?.firstOrNull()
    }

    companion object {
        fun from(coords: List<Coords>): BurrowGraph {
            val builder: MutableMap<Coords, MutableSet<Coords>> = HashMap()
            coords.forEach { builder[it] = mutableSetOf() }
            coords.forEach { coord -> coord.adjacent().forEach { builder[it]?.add(coord) } }
            return BurrowGraph(builder)
        }
    }
}

data class Path(
    val startingPoint: Coords,
    val destinationPoint: Coords,
    val visited: Set<Coords>
) {
    fun containsAnyOf(amphipodCoords: List<Coords>) = amphipodCoords.any(visited::contains)

    val totalDistance = visited.size - 1
}
