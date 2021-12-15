package eu.proszkie.adventofcode.day15

class PathFinder(private val riskLevelMap: RiskLevelMap) {

    fun findPathWithLowestRisk(): Path? {
        val naiveLowestRiskPath = findNaiveLowestRiskPath()
        return findPathWithLowestRisk(riskLevelMap.topLeftCorner, naiveLowestRiskPath.totalRisk())
            .minByOrNull(Path::totalRisk)
    }

    private fun findNaiveLowestRiskPath(): Path {
        return findNaiveLowestRiskPath(riskLevelMap.topLeftCorner)
    }

    private fun findNaiveLowestRiskPath(current: RiskLevelField, visited: Set<RiskLevelField> = emptySet()): Path {
        if (current.coords == riskLevelMap.destination) {
            return Path(visitedPlusCurrent(current, visited))
        }

        return current.coords.adjacent()
            .mapNotNull(riskLevelMap::at)
            .filter { !visited.contains(it) }
            .minByOrNull(RiskLevelField::riskLevel)
            .let { findNaiveLowestRiskPath(it!!, visitedPlusCurrent(current, visited)) }
    }

    private fun visitedPlusCurrent(
        current: RiskLevelField,
        visited: Set<RiskLevelField>
    ) = if (current == riskLevelMap.topLeftCorner && visited.isEmpty()) visited else visited + current

    private fun findPathWithLowestRisk(
        current: RiskLevelField?,
        highBound: Int,
        visited: Set<RiskLevelField> = emptySet(),
        riskLevelSum: Int = 0,
    ): List<Path> {
        if (current == null) {
            return emptyList()
        }

        if (current.coords == riskLevelMap.destination) {
            return listOf(Path(visitedPlusCurrent(current, visited)))
        }

        return current.coords.adjacent()
            .mapNotNull { riskLevelMap.at(it) }
            .filter { !visited.contains(it) }
            .filter { riskLevelSum + it.riskLevel < highBound }
            .flatMap { findPathWithLowestRisk(it, highBound, visitedPlusCurrent(current, visited), riskLevelSum + it.riskLevel) }
    }
}

data class RiskLevelMap(private val fields: List<RiskLevelField>, val destination: Coords) {

    private val groupedByCoords = fields.associateBy(RiskLevelField::coords)

    fun at(it: Coords): RiskLevelField? {
        return groupedByCoords.get(it)
    }

    val topLeftCorner: RiskLevelField = at(Coords(0, 0))!!
}

data class Path(private val raw: Collection<RiskLevelField>) {
    fun totalRisk() = raw.sumOf(RiskLevelField::riskLevel)
}

data class RiskLevelField(val riskLevel: Int, val coords: Coords)

data class Coords(val x: Int, val y: Int) {
    fun adjacent() = listOf(up(), down(), right(), left())
    private fun up() = Coords(x, y + 1)
    private fun down() = Coords(x, y - 1)
    private fun right() = Coords(x + 1, y)
    private fun left() = Coords(x - 1, y)
}

object RiskLevelMapFactory {
    fun createFromStrings(input: List<String>): RiskLevelMap {
        return input.flatMapIndexed { y, line ->
            line.mapIndexed { x, riskLevel ->
                RiskLevelField(riskLevel.digitToInt(), Coords(x, y))
            }
        }.let { RiskLevelMap(it, Coords(input.first().length - 1, input.size - 1)) }
    }
}