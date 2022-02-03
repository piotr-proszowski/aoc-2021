package eu.proszkie.adventofcode.day23.awfulapproach

object CoordsCache {
    private val coordsToAdjacent: MutableMap<Coords, Set<Coords>> = HashMap()

    fun getAdjacentFor(coords: Coords): Set<Coords> {
        return coordsToAdjacent[coords] ?: save(coords)
    }

    private fun save(coords: Coords): Set<Coords> {
        val adjacent = coords.adjacent()
        coordsToAdjacent[coords] = adjacent
        return adjacent
    }
}