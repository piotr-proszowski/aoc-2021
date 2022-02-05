package eu.proszkie.adventofcode.day23

object PathCache {

    private val cache: MutableMap<Pair<Coords, Coords>, Set<Coords>> = HashMap()

    fun findPathBetween(pointA: Coords, pointB: Coords): Path? {
        return cache[pointA to pointB]?.let { Path(pointA, pointB, it) }
    }

    fun save(pointA: Coords, pointB: Coords, path: Path?): Path? {
        path?.let {
            cache[pointA to pointB] = it.visited
        }
        return path
    }
}