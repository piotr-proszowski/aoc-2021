package eu.proszkie.adventofcode.day15

import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class PathFinder {

    fun findTotalRiskOfPathWithLowestRisk(riskLevelMap: RiskLevelMap): Int {
        val destination = Coords(riskLevelMap.fields.keys.maxOf { it.x }, riskLevelMap.fields.keys.maxOf { it.y })

        return generateSequence(riskLevelMap, RiskLevelMap::djikstraIteration)
            .dropWhile { it.at(destination)!!.distance == null }
            .first()
            .at(destination)!!.distance!!
    }

}

data class RiskLevelMap(
    val fields: MutableMap<Coords, RiskLevelField>,
    val destination: Coords = Coords(fields.keys.maxOf { it.x }, fields.keys.maxOf { it.y }),
    val queue: PriorityQueue<RiskLevelField> = minPriorityQueue(fields.get(Coords(0, 0))!!, destination),
) {

    fun djikstraIteration(): RiskLevelMap {
        val current = queue.poll()
        val modifiedFields = current.coords.adjacent()
            .mapNotNull(this::at)
            .filter { it.distance == null }
            .map { it.copy(distance = (current.distance ?: 0) + it.riskLevel) }

        queue.addAll(modifiedFields)

        return modifiedFields
            .let(this::withFieldsOverwritten)
            .withVisited(current)
    }

    fun at(it: Coords): RiskLevelField? {
        return fields.get(it)
    }

    fun withVisited(current: RiskLevelField): RiskLevelMap {
        fields[current.coords] = current.copy(visited = true)
        return this
    }

    fun withFieldsOverwritten(modifiedFields: List<RiskLevelField>): RiskLevelMap {
        modifiedFields.forEach { fields[it.coords] = it }
        return this
    }

    operator fun plus(other: RiskLevelMap): RiskLevelMap {
        return copy(fields = (fields + other.fields).toMutableMap(), queue = queue)
    }

    operator fun plus(others: List<RiskLevelMap>): RiskLevelMap {
        return others.fold(this) { acc, next -> acc + next }
    }

    fun size(): Int {
        return fields.size
    }
}

private fun minPriorityQueue(initialField: RiskLevelField, destination: Coords): PriorityQueue<RiskLevelField> {
    val queue = PriorityQueue<RiskLevelField>({ a, b -> (a.distance!!).compareTo(b.distance!!) })
    queue.add(initialField)
    return queue
}

data class RiskLevelField(
    val riskLevel: Int,
    val coords: Coords,
    val distance: Int? = null,
    val visited: Boolean = false
) {
    fun withIncreasedRiskLevel(): RiskLevelField {
        val newRiskLevel = when (riskLevel) {
            9 -> 1
            else -> riskLevel + 1
        }
        return copy(riskLevel = newRiskLevel)
    }

    fun withNewCoordinate(newCoordinate: Coords): RiskLevelField {
        return copy(coords = newCoordinate)
    }
}

data class Coords(val x: Int, val y: Int) {
    fun adjacent() = listOf(up(), down(), right(), left())
    private fun up() = Coords(x, y + 1)
    private fun down() = Coords(x, y - 1)
    private fun right() = Coords(x + 1, y)
    private fun left() = Coords(x - 1, y)

    fun movedOnXAxisBy(size: Int): Coords {
        return copy(x = x + size)
    }

    fun movedOnYAxisBy(size: Int): Coords {
        return copy(y = y + size)
    }
}

object RiskLevelMapFactory {
    fun createFromStrings(input: List<String>): RiskLevelMap {
        return input.flatMapIndexed { y, line ->
            line.mapIndexed { x, riskLevel ->
                RiskLevelField(riskLevel.digitToInt(), Coords(x, y))
            }
        }.let { RiskLevelMap(it.associateBy(RiskLevelField::coords).toMutableMap()) }
    }

    fun createTilesFromStrings(input: List<String>): RiskLevelMap {
        val firstTile = input.flatMapIndexed { y, line ->
            line.mapIndexed { x, riskLevel ->
                RiskLevelField(riskLevel.digitToInt(), Coords(x, y))
            }
        }.let { RiskLevelMap(it.associateBy(RiskLevelField::coords).toMutableMap()) }

        val allTiles =
            toTilesInRightDirection(firstTile) + toTilesInDownDirection(firstTile).flatMap(this::toTilesInRightDirection)
        return allTiles.reduce { prev, next -> prev + next }
    }

    private fun toTilesInRightDirection(initialTile: RiskLevelMap): List<RiskLevelMap> {
        return (1..4).fold(listOf(initialTile)) { acc, _ ->
            val lastTile = acc.last()
            val size = sqrt(lastTile.size().toDouble()).toInt()
            acc + lastTile.copy(
                fields = lastTile.fields.map {
                    val newCoordinate = it.key.movedOnXAxisBy(size)
                    newCoordinate to it.value.withIncreasedRiskLevel().withNewCoordinate(newCoordinate)
                }.associate { it.first to it.second }
                    .toMutableMap()
            )
        }
    }

    private fun toTilesInDownDirection(initialTile: RiskLevelMap): List<RiskLevelMap> {
        return (1..4).fold(listOf(initialTile)) { acc, _ ->
            val lastTile = acc.last()
            val size = sqrt(lastTile.size().toDouble()).toInt()
            acc + lastTile.copy(
                fields = lastTile.fields.map {
                    val newCoordinate = it.key.movedOnYAxisBy(size)
                    newCoordinate to it.value.withIncreasedRiskLevel().withNewCoordinate(newCoordinate)
                }.associate { it.first to it.second }
                    .toMutableMap()
            )
        }
    }

}