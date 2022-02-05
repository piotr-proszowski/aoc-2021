package eu.proszkie.adventofcode.day23

sealed class Amphipod {
    abstract val coords: Coords
    abstract val amphipodType: AmphipodType
    abstract fun findPossibleMoves(burrowState: BurrowState): List<AmphipodMove>
    fun toNotMovable(): NotMovableAmphipod = NotMovableAmphipod(coords, amphipodType)
    abstract fun withCoords(from: Coords): Amphipod

}

data class NotMovableAmphipod(
    override val coords: Coords,
    override val amphipodType: AmphipodType,
) : Amphipod() {
    override fun findPossibleMoves(burrowState: BurrowState): List<AmphipodMove> = listOf()
    override fun withCoords(from: Coords): Amphipod {
        return copy(coords = from)
    }
}

data class MovableAmphipod(
    override val coords: Coords,
    override val amphipodType: AmphipodType,
) : Amphipod() {
    override fun findPossibleMoves(burrowState: BurrowState): List<AmphipodMove> {
        return burrowState.findRoom(coords)
            ?.let { movesWhenAmphiopodIsInsideTheRoom(burrowState) }
            ?: movesWhenAmphipodIsInHallway(burrowState)

    }

    override fun withCoords(from: Coords): Amphipod {
        return copy(coords = from)
    }

    private fun movesWhenAmphipodIsInHallway(burrowState: BurrowState) =
        findMoveToDesiredRoom(burrowState)?.let(::listOf) ?: listOf()

    private fun movesWhenAmphiopodIsInsideTheRoom(burrowState: BurrowState): List<AmphipodMove> {
        val movesToHallway = findMovesToHallway(burrowState)
        val moveToRoom = findMoveToDesiredRoom(burrowState)
        return moveToRoom?.let { movesToHallway + moveToRoom }
            ?: movesToHallway
    }

    private fun findMovesToHallway(burrowState: BurrowState): List<MoveFromRoomToHallway> {
        val movesToHallway = burrowState.placesToStandByInHallway().mapNotNull { placeToStandBy ->
            burrowState.findPathBetween(coords, placeToStandBy)
        }.map { path ->
            MoveFromRoomToHallway(
                path.startingPoint,
                path.destinationPoint,
                amphipodType,
                path.totalDistance * amphipodType.energyNeededToMove
            )
        }
        return movesToHallway
    }

    private fun findMoveToDesiredRoom(burrowState: BurrowState): MoveFromRoomToRoom? {
        if (amphipodIsInDesiredRoom(burrowState)) {
            return null
        }
        val deepestFreeSpace = deepestFreeSpace(burrowState, burrowState.findDesiredRoomFor(amphipodType))

        return deepestFreeSpace?.let {
            burrowState.findPathBetween(coords, deepestFreeSpace)?.let { path ->
                MoveFromRoomToRoom(
                    path.startingPoint,
                    path.destinationPoint,
                    amphipodType,
                    path.totalDistance * amphipodType.energyNeededToMove
                )
            }
        }
    }

    private fun deepestFreeSpace(burrowState: BurrowState, desiredRoom: Room) =
        desiredRoom.deepestFreeSpace { coords: Coords -> burrowState.isSpaceOccupied(coords) }

    private fun amphipodIsInDesiredRoom(burrowState: BurrowState) =
        burrowState.findDesiredRoomFor(amphipodType) == burrowState.findRoom(coords)
}

enum class AmphipodType(val energyNeededToMove: Int) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000)
}

sealed class AmphipodMove {
    abstract val from: Coords
    abstract val to: Coords
    abstract val amphipodType: AmphipodType
    abstract val energyNeeded: Int
}

data class MoveFromRoomToHallway(
    override val from: Coords,
    override val to: Coords,
    override val amphipodType: AmphipodType,
    override val energyNeeded: Int
) : AmphipodMove()

data class MoveFromRoomToRoom(
    override val from: Coords,
    override val to: Coords,
    override val amphipodType: AmphipodType,
    override val energyNeeded: Int
) : AmphipodMove()

data class MoveFromHallwayToRoom(
    override val from: Coords,
    override val to: Coords,
    override val amphipodType: AmphipodType,
    override val energyNeeded: Int
) : AmphipodMove()
