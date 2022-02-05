package eu.proszkie.adventofcode.day23

import java.util.PriorityQueue

data class Burrow(
    private val hallway: Hallway,
    private val rooms: List<Room>
) {
    private val graph: BurrowGraph = BurrowGraph.from(hallway.coords + rooms.flatMap { it.coords })

    fun findRoom(coords: Coords): Room? {
        return rooms.find { it.contains(coords) }
    }

    fun placesToStandByInHallway() = hallway.coords - rooms.map(Room::coordsInFrontOfRoom)
    fun findDesiredRoomFor(amphipodType: AmphipodType): Room = rooms.find { it.isDesiredFor(amphipodType) }!!
    fun findPathBetween(pointA: Coords, pointB: Coords): Path? {
        return graph.pathBetween(pointA, pointB)
    }
}


data class Hallway(
    val coords: List<Coords>
)

data class Room(
    val coords: Set<Coords>,
    private val desiredFor: AmphipodType
) {
    private val deepestCoords: Coords = this.coords.maxByOrNull { it.y }!!
    val coordsInFrontOfRoom = coords.minByOrNull { it.y }!!.up()
    fun contains(coords: Coords) = this.coords.contains(coords)
    fun isDesiredFor(amphipodType: AmphipodType) = desiredFor == amphipodType
    fun isDeepest(coords: Coords) = deepestCoords == coords
    fun deepestFreeSpace(isSpaceOccupied: (Coords) -> Boolean) =
        coords.sortedByDescending { it.y }.firstOrNull { !isSpaceOccupied(it) }
}

data class Coords(val x: Int, val y: Int) {
    fun up() = copy(x = x, y = y - 1)
    private fun down() = copy(x = x, y = y + 1)
    private fun right() = copy(x = x + 1, y = y)
    private fun left() = copy(x = x - 1, y = y)
    fun adjacent() = setOf(up(), down(), right(), left())

    override fun toString(): String {
        return "{$x x $y}"
    }

}

data class BurrowState(
    private val burrow: Burrow,
    private val amphipods: Map<Coords, Amphipod>,
    val energyUsed: Int = 0
) {
    val minimalCostToReachFinalState =
        amphipods.map {
            it.value to burrow.findPathBetween(
                it.key,
                burrow.findDesiredRoomFor(it.value.amphipodType)
                    .deepestFreeSpace { coords: Coords -> this.isSpaceOccupied(coords) } ?: it.key
            )
        }.sumOf {
            it.first.amphipodType.energyNeededToMove * it.second?.totalDistance!!
        }

    fun findNextValidStates(): List<BurrowState> {
        return amphipods.values.flatMap { it.findPossibleMoves(this) }
            .mapNotNull(this::withAppliedMove)
    }

    private fun withAppliedMove(move: AmphipodMove): BurrowState? {
        return when (move) {
            is MoveFromRoomToHallway -> copy(
                energyUsed = move.energyNeeded + energyUsed,
                amphipods = amphipods.minus(move.from).plus(move.to to amphipods[move.from]!!.withCoords(move.to))
            )
            else -> handleMoveIntoRoom(move)
        }
    }

    private fun handleMoveIntoRoom(move: AmphipodMove): BurrowState? {
        val roomThatWillBeEntered = findRoom(move.to)
        val desiredRoom = findDesiredRoomFor(move.amphipodType)
        if (roomThatWillBeEntered != desiredRoom || thereIsAmphipodOfOtherTypeInsideTheRoom(desiredRoom)) {
            return null
        }
        return copy(
            energyUsed = move.energyNeeded + energyUsed,
            amphipods = amphipods.minus(move.from)
                .plus(move.to to amphipods[move.from]?.toNotMovable()!!.withCoords(move.to))
        )
    }

    private fun thereIsAmphipodOfOtherTypeInsideTheRoom(desiredRoom: Room): Boolean {
        return desiredRoom.coords.mapNotNull { amphipods[it] }
            .map { it.amphipodType }
            .any { !desiredRoom.isDesiredFor(it) }
    }

    fun findPathBetween(pointA: Coords, pointB: Coords): Path? {
        return burrow.findPathBetween(pointA, pointB)?.let { path ->
            val amphipodCoords = amphipods.values.filter { it.coords != path.startingPoint }.map(Amphipod::coords)
            if (path.containsAnyOf(amphipodCoords)) {
                null
            } else {
                path
            }
        }
    }

    fun findRoom(coords: Coords): Room? = burrow.findRoom(coords)
    fun placesToStandByInHallway(): List<Coords> = burrow.placesToStandByInHallway()
    fun findDesiredRoomFor(amphipodType: AmphipodType): Room = burrow.findDesiredRoomFor(amphipodType)
    fun findCheapestFinalState(): BurrowState {
        val queue: PriorityQueue<BurrowState> =
            PriorityQueue { a, b ->
                (a.minimalCostToReachFinalState + a.energyUsed).compareTo(b.minimalCostToReachFinalState + b.energyUsed)
            }
        queue.add(this)

        var cheapest: BurrowState? = null
        val alreadySeen: MutableSet<BurrowState> = HashSet()
        while (!queue.isEmpty()) {
            val current = queue.poll()

            if ((cheapest != null && current.energyUsed >= cheapest.energyUsed) || alreadySeen.contains(current)) {
                continue
            }
            alreadySeen.add(current)
            if (current.isFinal()) {
                if (cheapest == null || current.energyUsed < cheapest.energyUsed)
                    cheapest = current
            }
            queue.addAll(current.findNextValidStates())
        }

        return cheapest ?: throw IllegalStateException(":(")
    }

    private fun isFinal() = amphipods.all { it.value is NotMovableAmphipod }

    override fun toString(): String {
        val amphipodsToPrint = amphipods.entries.associate {
            it.key to it.value.amphipodType.name.first()
        }
        return (0..amphipodsToPrint.keys.maxOf { it.y }).map { y ->
            (0..amphipodsToPrint.keys.maxOf { it.x }).map { x ->
                amphipodsToPrint[Coords(x, y)] ?: '.'
            }.joinToString(separator = "")
        }.joinToString(separator = "\n")
    }

    fun isSpaceOccupied(coords: Coords) = amphipods[coords] != null
}