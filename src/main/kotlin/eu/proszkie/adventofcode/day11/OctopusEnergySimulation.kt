package eu.proszkie.adventofcode.day11

class OctopusEnergySimulation {
    fun countFlashes(octopusGrid: OctopusGrid, steps: Int): Int {
        return (0 until steps).fold(octopusGrid to 0) { acc, _ ->
            val next = acc.first.nextStep()
            next to acc.second + next.howMuchJustFlashed()
        }.second
    }
}

data class OctopusGrid(val octopuses: Map<Coords, Octopus>) {
    fun nextStep(): OctopusGrid {
        val afterLevelsIncreased = octopuses.entries.associateBy({ it.key }) { it.value.withIncreasedLevel() }
        return withFlashesCalculated(afterLevelsIncreased)
            .resetFlashCount()
    }

    private fun resetFlashCount(): OctopusGrid {
        return OctopusGrid(octopuses.mapValues { it.value.resetFlashCount() })
    }

    private fun withFlashesCalculated(
        grid: Map<Coords, Octopus>,
        previouslyFlashed: Set<Coords> = emptySet()
    ): OctopusGrid {
        val flashedOctopuses = grid.filter { it.value.level == 0 }

        val afterFlash = flashedOctopuses
            .filter { !previouslyFlashed.contains(it.key) }
            .flatMap { it.key.adjacent() }
            .groupBy { it }
            .mapValues { it.value.size }
            .mapNotNull {
                val octopus = grid[it.key]
                val numOfLevelIncreases = it.value
                octopus?.let {
                    (0 until numOfLevelIncreases).fold(it) { acc, _ -> acc.flashed() }
                }
            }
            .associateBy { it.coords }

        if (afterFlash.isEmpty()) {
            return OctopusGrid(grid + afterFlash)
        }
        return withFlashesCalculated(grid + afterFlash,
            previouslyFlashed + flashedOctopuses.keys + afterFlash.filter { it.value.flashCount > 1 && it.value.level == 0 }
                .map(Map.Entry<Coords, Octopus>::key)
        )
    }

    fun howMuchJustFlashed(): Int {
        return octopuses.count { it.value.level == 0 }
    }
}

object OctopusGridFactory {
    fun create(lines: List<String>): OctopusGrid {
        return lines.mapIndexed { y, line -> transformLineToOctopuses(line, y) }
            .reduce() { previous, current -> previous + current }
            .associateBy(Octopus::coords)
            .let(::OctopusGrid)
    }

    private fun transformLineToOctopuses(line: String, y: Int) =
        line.mapIndexed { x, value -> Octopus(value.digitToInt(), Coords(x, y)) }
}

data class Octopus(val level: Int, val coords: Coords, val flashCount: Int = 0) {

    fun withIncreasedLevel(): Octopus {
        return copy(level = if (level + 1 > 9) 0 else level + 1)
    }

    fun flashed(): Octopus {
        return if (level == 0) {
            this
        } else {
            withIncreasedLevel()
        }.copy(flashCount = flashCount + 1)
    }

    fun resetFlashCount(): Octopus {
        return copy(flashCount = 0)
    }
}

data class Coords(val x: Int, val y: Int) {
    fun adjacent() = listOf(up(), down(), right(), left(), upRight(), upLeft(), downRight(), downLeft())
    private fun up() = Coords(x, y + 1)
    private fun down() = Coords(x, y - 1)
    private fun right() = Coords(x + 1, y)
    private fun left() = Coords(x - 1, y)
    private fun upRight() = Coords(x + 1, y + 1)
    private fun upLeft() = Coords(x - 1, y + 1)
    private fun downRight() = Coords(x + 1, y - 1)
    private fun downLeft() = Coords(x - 1, y - 1)
}
