package eu.proszkie.adventofcode.day11

class OctopusEnergySimulator {
    fun findFirstStepWhenAllOctopusFlashSimultaneously(octopusGrid: OctopusGrid): Int {
        TODO()
    }

    fun countFlashes(octopusGrid: OctopusGrid, steps: Int): Int {
        TODO()
    }

    fun simulate(octopusGrid: OctopusGrid, toStep: Int): OctopusGrid {
        return (1..toStep).fold(octopusGrid) { acc, _ -> acc.nextStep() }
    }
}

data class OctopusGrid(private val octopuses: List<Octopus>, private val allFlashesThatHappened: List<Flash> = emptyList()) {
    private val groupedByCoords = octopuses.groupedByCoords()
    private val recentFlashes = octopuses.toFlashes()


    fun nextStep(): OctopusGrid {
        TODO()
//        return levelUp()
//            .withFlashesApplied(recentFlashes)
    }

}

private fun Collection<Octopus>.toFlashes() = this.filter(Octopus::justFlashed).map(Octopus::coords).map(::Flash)

fun Collection<Octopus>.groupedByCoords() = this.associateBy(Octopus::coords)

data class Flash(val coords: Coords)

object OctopusGridFactory {
    fun create(lines: List<String>): OctopusGrid {
        return lines.mapIndexed { y, line -> transformLineToOctopuses(line, y) }
            .reduce() { previous, current -> previous + current }
            .let(::OctopusGrid)
    }

    private fun transformLineToOctopuses(line: String, y: Int) =
        line.mapIndexed { x, value -> Octopus(value.digitToInt(), Coords(x, y)) }
}

data class Octopus(val level: Int, val coords: Coords, val justFlashed: Boolean = false) {

    fun increaseLevel(): Octopus =
        when (level) {
            9 -> copy(level = 0, justFlashed = true)
            else -> copy(level = level + 1, justFlashed = false)
        }

    fun flash(): Octopus = when (level) {
        0 -> if (justFlashed) this else increaseLevel()
        else -> increaseLevel()
    }
}

private fun noFlashes(): Set<Flash> = emptySet()

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
