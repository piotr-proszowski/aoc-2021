package eu.proszkie.adventofcode.day11

import java.util.LinkedList

data class Octopuses(val coordsToEnergyLevel: Map<Coords, Int>, val amountOfFlashes: Int = 0) {
    fun nextStep(): Octopuses {
        val coordsToProgress: LinkedList<Coords> = LinkedList(coordsToEnergyLevel.keys)
        val coordsToOctopus: MutableMap<Coords, Octopus> = coordsToEnergyLevel.toOctopuses()
        var flashesCount = amountOfFlashes
        while (!coordsToProgress.isEmpty()) {
            val coords = coordsToProgress.poll()

            coords.let(coordsToOctopus::get)
                ?.let(Octopus::progress)
                ?.also { coordsToOctopus[coords] = it }
                ?.takeIf { it is OctopusThatJustFlashed }
                ?.also { flashesCount++ }
                ?.let { coordsToProgress.addAll(coords.adjacent()) }
        }

        return Octopuses(coordsToOctopus.simplify(), flashesCount)
    }

    fun findStepWhenAllOctopusesFlash(): Int =
        generateSequence(this to 0) { (octopuses, step) ->
            octopuses.nextStep() to step + 1
        }.first { (octopuses, _) ->
            octopuses.allFlashed()
        }.second

    private fun allFlashed() =
        coordsToEnergyLevel.values.all { it == 0 }

    override fun toString(): String {
        val groupedByY = coordsToEnergyLevel.entries.groupBy { it.key.y }
        return groupedByY.keys.sorted().mapNotNull { groupedByY[it] }.joinToString(separator = "\n") {
            it.sortedBy { it.key.x }.map { it.value }.joinToString(separator = " ")
        }
    }
}

private fun Map<Coords, Octopus>.simplify(): Map<Coords, Int> = mapValues { it.value.energyLevel }

private fun Map<Coords, Int>.toOctopuses(): MutableMap<Coords, Octopus> =
    entries.associate { it.key to SimpleOctopus(it.value) }
        .toMutableMap()

sealed class Octopus {
    abstract val energyLevel: Int
    abstract fun progress(): Octopus
}

data class SimpleOctopus(override val energyLevel: Int) : Octopus() {
    override fun progress(): Octopus =
        when (energyLevel) {
            9 -> OctopusThatJustFlashed()
            else -> SimpleOctopus(energyLevel + 1)
        }
}

data class OctopusThatJustFlashed(override val energyLevel: Int = 0) : Octopus() {
    override fun progress(): Octopus = ExhaustedOctopus()
}

data class ExhaustedOctopus(override val energyLevel: Int = 0) : Octopus() {
    override fun progress(): Octopus = ExhaustedOctopus()
}

data class Coords(val x: Int, val y: Int) {
    fun adjacent(): Collection<Coords> =
        listOf(
            copy(x = x + 1, y = y + 1),
            copy(x = x - 1, y = y - 1),
            copy(x = x - 1, y = y + 1),
            copy(x = x + 1, y = y - 1),
            copy(y = y - 1),
            copy(y = y + 1),
            copy(x = x + 1),
            copy(x = x - 1),
        )
}

object OctopusesFactory {
    fun fromRowsOfEnergyLevels(rows: List<List<Int>>): Octopuses {
        require(rows.all { it.all { it in 0..9 } }) { "Energy levels have to be in range <0, 9>" }

        return processRows(rows)
            .associate { it.first to it.second }
            .let(::Octopuses)
    }

    private fun processRows(rows: List<List<Int>>) =
        rows.flatMapIndexed { yCoord, row ->
            row.mapIndexed { xCoord, energyLevel ->
                Coords(xCoord, yCoord) to energyLevel
            }
        }
}