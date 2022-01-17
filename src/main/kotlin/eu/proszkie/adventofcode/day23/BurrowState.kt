package eu.proszkie.adventofcode.day23

import kotlin.math.abs

data class ElementWithCoords(val element: ElementInBurrow, val coords: Coords)
data class MovableElementWithCoords(val element: Movable, val coords: Coords)

data class Room(val elements: Set<ElementWithCoords>) {
    fun isFinal(): Boolean {
        return elements.map { it.element }.filterIsInstance<FreeSpace>().isEmpty() &&
                elements.map { it.element }.map { it.javaClass }.filter { it != FreeSpace.javaClass }.toSet().size == 1
    }
}


data class RoomsCoords(val raw: Map<Coords, List<Coords>>) {
    operator fun get(coords: Coords): List<Coords>? {
        return raw[coords]
    }
}

data class Burrow(private val input: Map<Coords, ElementInBurrow>, private val roomsCoords: RoomsCoords?) {

    private val raw: Map<Coords, ElementInBurrow> = input + input.entries.filter { it.value is Movable }
        .associate { it.key to (it.value as Movable).withVisitedPlace(it.key) }

    fun findPossibleMoves() = findAllMovableElements().map { it.coords to it.coords.adjacent() }
        .flatMap {
            val from = it.first
            it.second.map { to -> BurrowChange(from, to) }
        }.filter { raw[it.destination] is FreeSpace }

    private fun findAllMovableElements() =
        raw.filter { it.value is Movable }.map { MovableElementWithCoords(it.value as Movable, it.key) }

    fun with(element: ElementInBurrow): ChangeBuilder {
        return ChangeBuilder(element, this)
    }

    operator fun get(from: Coords): ElementInBurrow? = raw[from]

    data class ChangeBuilder(val element: ElementInBurrow, val burrow: Burrow) {
        fun at(coords: Coords): Burrow {
            return Burrow(
                input = burrow.raw.plus(coords to element),
                roomsCoords = burrow.roomsCoords()
            )
        }
    }

    fun findAllThatAreInFrontOfRoom() =
        findAllMovableElements().filter { this.isInFrontOfRoom(it.coords) }.map(MovableElementWithCoords::element)

    fun isInFrontOfRoom(destination: Coords): Boolean {
        return destination.adjacent().map(this::at).count { it is FreeSpace || it is Movable } == 3
    }

    private fun at(coords: Coords) = raw[coords]

    fun getRoom(coords: Coords): Room? {
        return if (roomsCoords == null) {
            if (!isInsideTheRoom(coords)) {
                return null
            }
            val up = generateSequence(coords.up(), Coords::up).takeWhile(this::isInsideTheRoom).toList()
            val down = generateSequence(coords.down(), Coords::down).takeWhile(this::isInsideTheRoom).toList()
            Room(
                up.plus(down).plus(coords)
                    .mapNotNull { coords -> this.at(coords)?.let { element -> ElementWithCoords(element, coords) } }
                    .toSet()
            )
        } else {
            roomsCoords[coords]?.map { it to this[it] }
                ?.map { ElementWithCoords(it.second!!, it.first) }
                ?.toSet()
                ?.let { Room(it) }
        }

    }

    private fun roomsCoords(): RoomsCoords =
        roomsCoords ?: initializeRoomsCoords()

    private fun initializeRoomsCoords(): RoomsCoords {
        return findAllRooms().flatMap { room ->
            room.elements.map(ElementWithCoords::coords)
                .map { it to room }
        }.associate { it.first to it.second.elements.map(ElementWithCoords::coords) }
            .let(::RoomsCoords)
    }

    fun isInsideTheRoom(coords: Coords) =
        this[coords] !is Wall && this[coords.right()] is Wall && this[coords.left()] is Wall


    fun findAllRooms(): Set<Room> {
        return raw.map { it.key }
            .filter(this::isInsideTheRoom)
            .mapNotNull(this::getRoom).toSet()
    }

    override fun toString(): String {
        val sorted = raw.keys.sorted()
        val minY = sorted.minOrNull()!!.y
        val maxY = sorted.maxOrNull()!!.y
        return (minY..maxY).joinToString(separator = "\n") { y ->
            sorted.filter { it.y == y }.map { this[it] }.joinToString(separator = "")
        }
    }
}

data class BurrowState(
    val burrow: Burrow,
    val previousChange: BurrowChange? = null,
    val energyUsed: Long = 0
) {
    fun findPossibleChanges(): List<BurrowChange> {
        return burrow.findPossibleMoves()
    }

    fun applyChange(change: BurrowChange) = change.applyTo(this)

    fun applyChanges(changes: List<BurrowChange>) = changes.fold(Success(this) as ChangeAttemptResult) { acc, next ->
        when (acc) {
            is Success -> acc.changedBurrow.applyChange(next)
            else -> acc
        }
    }

    fun isFinal() = burrow.findAllRooms()
        .allAreInFinalState()

    private fun Collection<Room>.allAreInFinalState() = all(Room::isFinal)

    override fun toString(): String {
        return "$burrow"
    }

    private fun calculate(from: Coords, to: Coords): Int {
        return abs(from.x - to.x) + abs(from.y - to.y)
    }
}

data class Coords(val x: Int, val y: Int) : Comparable<Coords> {
    fun adjacent() = listOf(
        Coords(x + 1, y),
        Coords(x - 1, y),
        Coords(x, y + 1),
        Coords(x, y - 1)
    )

    fun right() = copy(x = x + 1)
    fun left() = copy(x = x - 1)
    fun up() = copy(y = y - 1)
    fun down() = copy(y = y + 1)

    override fun compareTo(other: Coords): Int {
        return if (y.compareTo(other.y) == 0) {
            if (x.compareTo(other.x) == 0) {
                0
            } else {
                x.compareTo(other.x)
            }
        } else {
            y.compareTo(other.y)
        }
    }
}

interface ElementInBurrow {
    companion object {
        fun fromToken(token: Char): ElementInBurrow? = when (token) {
            '#' -> Wall
            '.' -> FreeSpace
            ' ' -> OutsideBurrow
            'A' -> Amber(setOf(), false)
            'B' -> Bronze(setOf(), false)
            'C' -> Copper(setOf(), false)
            'D' -> Desert(setOf(), false)
            else -> null
        }
    }
}

sealed class Movable(open val visitedPlaces: Set<Coords>, open val hasStopped: Boolean = false) : ElementInBurrow {
    fun withVisitedPlace(coords: Coords): Movable {
        return when (this) {
            is Amber -> copy(visitedPlaces = visitedPlaces + coords)
            is Bronze -> copy(visitedPlaces = visitedPlaces + coords)
            is Copper -> copy(visitedPlaces = visitedPlaces + coords)
            is Desert -> copy(visitedPlaces = visitedPlaces + coords)
        }
    }

    fun stopped(): Movable {
        return when (this) {
            is Amber -> copy(hasStopped = true, visitedPlaces = if (hasStopped) visitedPlaces else setOf())
            is Bronze -> copy(hasStopped = true, visitedPlaces = if (hasStopped) visitedPlaces else setOf())
            is Copper -> copy(hasStopped = true, visitedPlaces = if (hasStopped) visitedPlaces else setOf())
            is Desert -> copy(hasStopped = true, visitedPlaces = if (hasStopped) visitedPlaces else setOf())
        }
    }

    abstract fun energyNeededToMove(): Long
}

object Wall : ElementInBurrow {
    override fun toString(): String = "#"
}

object FreeSpace : ElementInBurrow {
    override fun toString(): String = "."
}

object OutsideBurrow : ElementInBurrow {
    override fun toString(): String = " "
}

data class Amber(override val visitedPlaces: Set<Coords>, override val hasStopped: Boolean) :
    Movable(visitedPlaces, hasStopped) {
    override fun energyNeededToMove(): Long = 1
    override fun toString(): String = "A"
}

data class Bronze(override val visitedPlaces: Set<Coords>, override val hasStopped: Boolean) :
    Movable(visitedPlaces, hasStopped) {
    override fun energyNeededToMove(): Long = 10
    override fun toString(): String = "B"
}

data class Copper(override val visitedPlaces: Set<Coords>, override val hasStopped: Boolean) :
    Movable(visitedPlaces, hasStopped) {
    override fun energyNeededToMove(): Long = 100
    override fun toString(): String = "C"
}

data class Desert(override val visitedPlaces: Set<Coords>, override val hasStopped: Boolean) :
    Movable(visitedPlaces, hasStopped) {
    override fun energyNeededToMove(): Long = 1000
    override fun toString(): String = "D"
}

data class BurrowChange(val from: Coords, val destination: Coords) {
    fun applyTo(burrow: BurrowState): ChangeAttemptResult {
        val raw = burrow.burrow
        val movable = raw[from]
        return fail(burrow, movable) ?: success(burrow, movable!!)
    }

    private fun success(
        burrowState: BurrowState,
        elementToMove: ElementInBurrow
    ): Success {
        val (burrow, previousChange, energyUsed) = burrowState
        val previouslyMovedElement = previousChange?.destination?.let { burrow[it] }
        require(elementToMove is Movable)
        val modifiedBurrow = if (elementToMove != previouslyMovedElement && previouslyMovedElement != null) {
            require(previouslyMovedElement is Movable)
            burrow.with(FreeSpace).at(from)
                .with(previouslyMovedElement.stopped()).at(previousChange.destination)
                .with(elementToMove.withVisitedPlace(destination)).at(destination)
        } else {
            burrow.with(FreeSpace).at(from)
                .with(elementToMove.withVisitedPlace(destination)).at(destination)
        }

        return Success(
            BurrowState(
                burrow = modifiedBurrow,
                previousChange = this,
                energyUsed = energyUsed + elementToMove.energyNeededToMove()
            )
        )
    }

    private fun fail(
        burrowState: BurrowState,
        elementToMove: ElementInBurrow?,
    ): ChangeAttemptResult? {
        val (burrow, previousChange, _) = burrowState
        if (burrow[destination] != FreeSpace) {
            return CannotMoveToTakenPlace
        }
        if (elementToMove !is Movable) {
            return AttemptOfMovingNotMovableElement
        }
        val movableInFrontOfRoom = burrow.findAllThatAreInFrontOfRoom()

        val burrowAfterMove = burrow.with(FreeSpace).at(from).with(elementToMove).at(destination)
        if (burrowAfterMove.isInFrontOfRoom(destination) && movableInFrontOfRoom.isNotEmpty() ||
            movableInFrontOfRoom.isNotEmpty() && elementToMove !in movableInFrontOfRoom
        ) {
            return AmphipodStopsImmediatelyOutsideTheRoom
        }

        val room = burrow.getRoom(destination)
        if (room != null) {
            if (elementToMove.visitedPlaces.contains(destination)) {
                return AmphipodAlreadyVisitedThisRoom
            }
            if (theRoomIsNotEmpty(room) && thereAreOtherTypesInRoom(room, elementToMove)) {
                return AmphipodTriesToEnterRoomWhereAmphipodOfOtherTypeIs
            }
        }


        if (previousChange != null) {
            val previouslyMovedElement = burrow[previousChange.destination]
            if (previousChange.from == destination && elementToMove == previouslyMovedElement) {
                return GoesBackToTheSamePlaceAsInPreviousMove
            }

            if (elementToMove != previouslyMovedElement && previouslyMovedElement != null) {
                require(previouslyMovedElement is Movable)
                if (previouslyMovedElement.hasStopped && !burrow.isInsideTheRoom(previousChange.destination)) {
                    return AmphipodTriesToStopSecondTime
                }
            }

            if (previouslyMovedElement != elementToMove &&
                (burrow[previousChange.destination.up()] is FreeSpace || burrow[previousChange.destination.up()] is Movable) &&
                (burrow[previousChange.destination.down()] is FreeSpace)
            ) {
                return GoesBackToTheSamePlaceAsInPreviousMove
            }
        }

        return null
    }

    private fun theRoomIsNotEmpty(room: Room) =
        room.elements.map { it.element }.filterIsInstance<FreeSpace>().size != room.elements.size

    private fun thereAreOtherTypesInRoom(
        room: Room,
        elementToMove: Movable
    ) = !room.elements.map { it.element }.map { it.javaClass }.contains(elementToMove.javaClass)
}

sealed class ChangeAttemptResult
data class Success(val changedBurrow: BurrowState) : ChangeAttemptResult()
object CannotMoveToTakenPlace : ChangeAttemptResult()
object AttemptOfMovingNotMovableElement : ChangeAttemptResult()
object AmphipodStopsImmediatelyOutsideTheRoom : ChangeAttemptResult()
object AmphipodAlreadyVisitedThisRoom : ChangeAttemptResult()
object AmphipodTriesToEnterRoomWhereAmphipodOfOtherTypeIs : ChangeAttemptResult()
object GoesBackToTheSamePlaceAsInPreviousMove : ChangeAttemptResult()
object AmphipodTriesToStopSecondTime : ChangeAttemptResult()