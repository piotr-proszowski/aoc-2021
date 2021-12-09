package eu.proszkie.adventofcode.day09

data class HeightMap(val locations: Map<Coords, Location>) {

    fun findProductOfNBiggestBasins(n: Int): Int {
        return findBasins()
            .map(Basin::size)
            .sortedByDescending { it }
            .take(n)
            .reduce { acc, next -> acc * next }

    }

    private fun findBasins(): List<Basin> {
        return locations.values
            .filter { location -> location.allNeighboursHaveHigherRisk() }
            .map { findBasinsInternal(it, setOf()) }
    }

    private fun findBasinsInternal(current: Location, alreadyFound: Set<Location>): Basin {
        val higherRiskAdjacentLocations = current.coords.neighbours()
            .mapNotNull(locations::get)
            .filter { it.raw != 9 }
            .filter { it.hasHigherRiskThan(current.raw) }
            .filter { alreadyFound.doesNotContain(it) }

        return when {
            higherRiskAdjacentLocations.isEmpty() -> Basin(alreadyFound + current)
            else -> considerAdjacentLocationsWithHigherRisks(higherRiskAdjacentLocations, alreadyFound, current)
        }

    }

    private fun considerAdjacentLocationsWithHigherRisks(
        higherAdjacentLocations: List<Location>,
        alreadyFound: Set<Location>,
        current: Location
    ) = higherAdjacentLocations.map { findBasinsInternal(it, alreadyFound + current) }
        .flatMap(Basin::locations)
        .toSet()
        .let(::Basin)

    private fun Set<Location>.doesNotContain(location: Location) = !this.contains(location)

    fun riskLevels(): List<Int> {
        return locations.values.filter { it.allNeighboursHaveHigherRisk() }.toSet()
            .map(Location::raw)
            .map { it + 1 }
    }

    private fun Location.allNeighboursHaveHigherRisk() = this.coords.neighbours()
        .mapNotNull(locations::get)
        .all { this.raw < it.raw }

    private fun Location.hasHigherRiskThan(value: Int) = this.raw > value
}

data class Basin(val locations: Set<Location>) {
    fun size(): Int = locations.size
}

data class Location(val raw: Int, val coords: Coords)

data class Coords(val x: Int, val y: Int) {
    fun neighbours() = listOf(up(), down(), right(), left())
    private fun up() = Coords(x, y + 1)
    private fun down() = Coords(x, y - 1)
    private fun right() = Coords(x + 1, y)
    private fun left() = Coords(x - 1, y)
}

class HeightMapFactory {
    companion object {
        fun load(lines: List<String>): HeightMap {
            return lines.mapIndexed { y, line -> transformLineToLocations(line, y) }
                .reduce() { previous, current -> previous + current }
                .associateBy(Location::coords)
                .let(::HeightMap)
        }

        private fun transformLineToLocations(line: String, y: Int) =
            line.mapIndexed { x, value -> Location(value.digitToInt(), Coords(x, y)) }
    }
}