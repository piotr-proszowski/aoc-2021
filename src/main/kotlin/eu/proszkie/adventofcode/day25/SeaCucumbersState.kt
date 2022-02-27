package eu.proszkie.adventofcode.day25

data class SeaCucumbersState(
    private val raw: Map<Coords, MapElement>,
    private val maxXCoord: Int = raw.map { it.key }.map { it.x }.maxOf { it },
    private val maxYCoord: Int = raw.map { it.key }.map { it.y }.maxOf { it }
) {
    fun nextState(): SeaCucumbersState =
        moveAllElementsThatGoRight()
            .moveAllElementsThatGoDown()

    private fun moveAllElementsThatGoRight(): SeaCucumbersState {
        val transitions = raw.getTransitionsToTheRight()
        val currentMap = raw.toMutableMap()
        executeAllTransitions(transitions, currentMap)
        return SeaCucumbersState(
            raw = currentMap,
            maxXCoord = maxXCoord,
            maxYCoord = maxYCoord
        )
    }

    private fun moveAllElementsThatGoDown(): SeaCucumbersState {
        val transitions = raw.getTransitionsDown()
        val currentMap = raw.toMutableMap()
        executeAllTransitions(transitions, currentMap)
        return SeaCucumbersState(
            raw = currentMap,
            maxXCoord = maxXCoord,
            maxYCoord = maxYCoord
        )
    }

    private fun executeAllTransitions(
        transitions: List<Transition>,
        currentMap: MutableMap<Coords, MapElement>
    ) {
        val doableTransitions = findDoableTransitions(transitions, currentMap).toMutableList()
        doableTransitions.onEach { executeTransition(it, currentMap) }
    }

    private fun executeTransition(transition: Transition, currentMap: MutableMap<Coords, MapElement>) {
        val element = currentMap[transition.from]!!
        currentMap[transition.to] = element
        currentMap[transition.from] = EmptySpace
    }

    private fun findDoableTransitions(
        transitions: List<Transition>,
        currentMap: Map<Coords, MapElement>
    ): List<Transition> =
        transitions.filter { currentMap[it.to] == EmptySpace }

    private fun Map<Coords, MapElement>.getTransitionsToTheRight() =
        entries.filter { it.value is GoingRightCucumber }
            .map { it.key }
            .map { Transition(it, nextRightCoords(it)) }

    private fun nextRightCoords(from: Coords): Coords {
        return if (from.x == maxXCoord) {
            Coords(0, from.y)
        } else {
            Coords(from.x + 1, from.y)
        }
    }

    private fun Map<Coords, MapElement>.getTransitionsDown() =
        entries.filter { it.value is GoingDownCucumber }
            .map { it.key }
            .map { Transition(it, nextDownCoords(it)) }

    private fun nextDownCoords(from: Coords): Coords {
        return if (from.y == maxYCoord) {
            Coords(from.x, 0)
        } else {
            Coords(from.x, from.y + 1)
        }
    }

    override fun toString(): String {
        val lines = raw.entries.groupBy { it.key.y }
            .map { it.value }
        return (0..maxYCoord).map { yCoord ->
            processLine(lines[yCoord])
        }.joinToString(separator = "\n")
    }

    private fun processLine(elements: List<Map.Entry<Coords, MapElement>>) =
        elements.sortedBy { it.key.x }.map { it.value }.joinToString(separator = "")


}

data class Transition(val from: Coords, val to: Coords)