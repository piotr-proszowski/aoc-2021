package eu.proszkie.adventofcode.day11

class OctopusEnergySimulator {
    fun findFirstStepWhenAllOctopusFlashSimultaneously(octopusGrid: OctopusGrid): Int {
        return generateSequence(octopusGrid withAll noFlashes().toList()) {
            val afterNextStep = simulateNextStep(it)
            afterNextStep.grid withAll afterNextStep.flashes
        }.indexOfFirst { it.allFlashesThatHappened.size == it.grid.size }
    }

    fun countFlashes(octopusGrid: OctopusGrid, steps: Int): Int {
        return simulate(octopusGrid, steps).allFlashesThatHappened.size
    }

    fun simulate(octopusGrid: OctopusGrid, toStep: Int): GridWithAllFlashesWhichHappened {
        return (0 until toStep)
            .fold(octopusGrid withAll noFlashes().toList()) { acc, _ ->
                val gridWithFlashesThatJustHappened = simulateNextStep(acc)
                val (grid, flashesInCurrentStep) = gridWithFlashesThatJustHappened
                grid withAll flashesInCurrentStep.toList() + acc.allFlashesThatHappened.toList()
            }
    }

    private fun simulateNextStep(gridsWithFlashes: GridWithAllFlashesWhichHappened): GridWithFlashesInCurrentStep{
        val (grid, _) = gridsWithFlashes
        val (leveledUpGrid, flashesToApply) = grid.levelUpAll()
        val updatedGridWithAppliedFlashes = flashesToApply.let(leveledUpGrid::withFlashesApplied)
        return updatedGridWithAppliedFlashes.grid with updatedGridWithAppliedFlashes.flashes
    }
}

data class OctopusGrid(val grid: Map<Coords, Octopus>) {
    val size: Int = grid.size

    fun levelUpAll(): GridWithFlashesInCurrentStep {
        val octopusesLeveledUp = grid.values.map(Octopus::increaseLevel)
        return copy(grid = octopusesLeveledUp.groupedByCoords()) with octopusesLeveledUp.toFlashes()
    }

    fun withFlashesApplied(flashesToApply: Collection<Flash>, flashesAppliedInCurrentStep: Set<Flash> = noFlashes()): GridWithFlashesInCurrentStep {
        val flashes = flashesToApply.subtract(flashesAppliedInCurrentStep)
        return if(flashes.isEmpty()) {
            this with flashesAppliedInCurrentStep
        } else {
            val updatedOctopuses = flashes.map(Flash::coords)
                .flatMap(Coords::adjacent)
                .map(::Flash)
                .filter { !flashesAppliedInCurrentStep.contains(it) }
                .map(Flash::coords)
                .mapNotNull(grid::get)
                .filter(Octopus::justFlashed)
                .map(Octopus::flash)

            return OctopusGrid(grid + updatedOctopuses.groupedByCoords())
                .withFlashesApplied(updatedOctopuses.toFlashes(), flashes + flashesAppliedInCurrentStep)
        }
    }

}

private fun Collection<Octopus>.toFlashes() = this.filter(Octopus::justFlashed).map(Octopus::coords).map(::Flash)

fun Collection<Octopus>.groupedByCoords() = this.associateBy(Octopus::coords)

data class Flash(val coords: Coords)

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

data class Octopus(val level: Int, val coords: Coords, val justFlashed: Boolean = false) {

    fun increaseLevel(): Octopus =
        when (level) {
            9 -> copy(level = 0, justFlashed = true)
            else -> copy(level = level + 1)
        }

    fun flash(): Octopus = when (level) {
        0 -> if (justFlashed) this else increaseLevel()
        else -> increaseLevel()
    }
}

data class GridWithAllFlashesWhichHappened(val grid: OctopusGrid, val allFlashesThatHappened: Collection<Flash>)
data class GridWithFlashesInCurrentStep(val grid: OctopusGrid, val flashes: Collection<Flash>)

private infix fun OctopusGrid.with(flashes: Collection<Flash>) = GridWithFlashesInCurrentStep(this, flashes)
private infix fun OctopusGrid.withAll(flashes: Collection<Flash>) = GridWithAllFlashesWhichHappened(this, flashes)

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
