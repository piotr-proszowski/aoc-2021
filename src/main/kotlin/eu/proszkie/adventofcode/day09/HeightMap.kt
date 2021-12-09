package eu.proszkie.adventofcode.day09

data class HeightMap(val locations: Map<Coords, Location>) {

    fun findProductOfSizesOfThreeBiggestBasins(): Int {
        return findBasins()sortedByDescending(Basin::size)
            .take(3)
            .fold(1) { acc, next -> acc * next.size() }

    }

    private fun findBasins(): List<Basin> {
        return locations.values.filter { it.allNeighboursAreHigher() }
            .map { findBasinsInternal(it, setOf()) }
    }

    private fun findBasinsInternal(current: Location, alreadyFound: Set<Location>): Basin {
        val higherAdjacentLocations = current.coords.neighbours()
            .mapNotNull(locations::get)
            .filter { it.raw != 9 }
            .filter { it.isHigherThan(current.raw) }
            .filter { !alreadyFound.contains(it) }

        if (higherAdjacentLocations.isEmpty()) {
            return Basin(alreadyFound + current)
        }

        return higherAdjacentLocations.map { findBasinsInternal(it, alreadyFound + current) }
            .flatMap(Basin::locations)
            .toSet()
            .let(::Basin)
    }

    fun riskLevels(): List<Int> {
        return locations.values.filter { it.allNeighboursAreHigher() }.toSet()
            .map(Location::raw)
            .map { it + 1 }
    }

    private fun Location.allNeighboursAreHigher() = this.coords.neighbours()
        .mapNotNull(locations::get)
        .all { this.raw < it.raw }

    private fun Location.isHigherThan(value: Int) = this.raw > value
}

data class Basin(val locations: Set<Location>) {
    fun size(): Int = locations.size
}

data class Location(val raw: Int, val coords: Coords)

data class Coords(val x: Int, val y: Int) {
    fun neighbours() = listOf(up(), down(), right(), left())
    fun up() = Coords(x, y + 1)
    fun down() = Coords(x, y - 1)
    fun right() = Coords(x + 1, y)
    fun left() = Coords(x - 1, y)
}

class HeightMapFactory {
    companion object {
        fun load(lines: List<String>): HeightMap {
            val coordsToLocations = lines.mapIndexed { y, line ->
                val xAxis = 0 until lines.first().length
                xAxis.map { x -> Coords(x, y) to Location(line[x].digitToInt(), Coords(x, y)) }
                    .associate { it.first to it.second }
            }.reduce() { previous, current -> previous + current }

            return HeightMap(coordsToLocations)
        }
    }
}