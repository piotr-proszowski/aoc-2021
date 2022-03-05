package eu.proszkie.adventofcode.day12

class CavesDistinctPathsFinder(private val caves: Collection<Cave>) {
    private val groupedById = caves.associateBy(Cave::id)
    fun findDistinctPaths(): Collection<Path> {
        val startingCave = caves.find { it.id == "start" }!!
        return findPathsToEnd(startingCave, listOf(startingCave.id))
    }

    private fun findPathsToEnd(cave: Cave, alreadyVisitedCaves: List<String>): Collection<Path> {
        if (cave.id == "end") {
            return setOf(Path(alreadyVisitedCaves))
        }

        val desiredCaves =
            cave.outcoming.mapNotNull(groupedById::get).filter { !isSmallAndWasVisited(it, alreadyVisitedCaves) }

        if (desiredCaves.isEmpty()) {
            return emptyList()
        }

        return desiredCaves
            .map { findPathsToEnd(it, alreadyVisitedCaves + it.id) }
            .filter { !it.isEmpty() }
            .flatten()
    }

    fun findDistinctPathsPart2(): Collection<Path> {
        val startingCave = caves.find { it.id == "start" }!!
        return findPathsToEndPart2(startingCave, listOf(startingCave.id))
    }

    private fun findPathsToEndPart2(cave: Cave, alreadyVisitedCaves: List<String>): Collection<Path> {
        if (cave.id == "end") {
            return setOf(Path(alreadyVisitedCaves))
        }

        val desiredCaves =
            cave.outcoming.mapNotNull(groupedById::get).filter { !isSmallAndWasVisited(it, alreadyVisitedCaves) }
                .toSet()
        val otherDesiredCaves = cave.outcoming.mapNotNull(groupedById::get).filter(Cave::isSmall)
            .filter { wasOnlyVisitedOnceAndThereIsNoOtherSmallCaveVisitedTwiceAndItsNotStartAndEnd(it, alreadyVisitedCaves) }
            .toSet()

        if ((desiredCaves + otherDesiredCaves).isEmpty()) {
            return emptyList()
        }

        return (desiredCaves + otherDesiredCaves)
            .map { findPathsToEndPart2(it, alreadyVisitedCaves + it.id) }
            .filter { !it.isEmpty() }
            .flatten()
    }

    private fun wasOnlyVisitedOnceAndThereIsNoOtherSmallCaveVisitedTwiceAndItsNotStartAndEnd(cave: Cave, visited: List<String>): Boolean {
        if(!visited.groupBy { it }.filter { it.key.lowercase() == it.key }.filter { it.value.size > 1 }.isEmpty()) {
            return false
        }
        return visited.count { it == cave.id } < 2 && cave.id !in listOf("start", "end")
    }

    private fun isSmallAndWasVisited(cave: Cave, alreadyVisitedCaves: List<String>): Boolean {
        return cave.isSmall() && alreadyVisitedCaves.contains(cave.id)
    }
}


data class Path(val raw: List<String>)

object CavesFactory {
    fun fromString(lines: List<String>): Collection<Cave> {
        return lines.map { it.split("-") }
            .flatMap { listOf(Cave(it[0], outcoming = setOf(it[1])), Cave(it[1], outcoming = setOf(it[0]))) }
            .groupBy(Cave::id)
            .mapValues { it.value.reduce { acc, next -> acc.copy(outcoming = acc.outcoming + next.outcoming) } }
            .values
    }
}

data class Cave(val id: String, val outcoming: Set<String> = setOf()) {
    fun isSmall(): Boolean {
        return id.lowercase() == id
    }
}