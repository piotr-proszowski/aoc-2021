package eu.proszkie.adventofcode.day23

import eu.proszkie.adventofcode.day23.Element.FreeSpace
import eu.proszkie.adventofcode.day23.ElementFactory.fromToken
import eu.proszkie.adventofcode.day23.MovableFactory.fromElement
import java.util.UUID
import kotlin.math.abs

data class Burrow(
    val rooms: Collection<Room>,
    val burrowId: UUID,
    val stopsCounter: Map<Coords, Int> = mapOf()
) {
    private val elements: Map<Coords, Element> = BurrowsCache.findById(burrowId)!!
    private val movableElements = elements.filter { it.value.isMovable }.mapValues { fromElement(it.value) }

    fun isFinal() = rooms.all { it.isFinal(elements) }
    fun allChangeCandidates(): List<BurrowStateChange> {
        return movableElements.entries.flatMap { entry ->
            val (startingCoords, movableElement) = entry
            startingCoords.adjacent()
                .map { destination -> BurrowStateChange(movableElement, startingCoords, destination) }
        }
    }

    fun applyChange(change: BurrowStateChange, previousChange: BurrowStateChange?): BurrowChangeResult {
        return processChange(change, previousChange)
            .let { updateStopsCounter(it, change, previousChange) }
    }

    private fun updateStopsCounter(
        result: BurrowChangeResult,
        change: BurrowStateChange,
        previousChange: BurrowStateChange?
    ): BurrowChangeResult {
        return when (result) {
            is BurrowChangeSucceeded -> result.copy(
                burrow = result.burrow.copy(
                    stopsCounter = updatedStopsCounter(
                        change,
                        previousChange
                    )
                )
            )
            is BurrowChangeFailed -> result
        }
    }

    private fun updatedStopsCounter(change: BurrowStateChange, previousChange: BurrowStateChange?): Map<Coords, Int> {
        if (previousChange == null) {
            return stopsCounter
        }

        if (previousChange.movable != change.movable) {
            return stopsCounter.plus(
                previousChange.destination to stopsCounter.getOrDefault(
                    previousChange.destination,
                    0
                ) + 1
            ).minus(previousChange.startingCoords)
                .plus(
                    change.destination to stopsCounter.getOrDefault(
                        change.startingCoords,
                        0
                    )
                ).minus(change.startingCoords)
        } else {
            return stopsCounter.plus(
                change.destination to stopsCounter.getOrDefault(
                    change.startingCoords,
                    0
                )
            ).minus(change.startingCoords)
        }
    }

    private fun processChange(
        change: BurrowStateChange,
        previousChange: BurrowStateChange?
    ) = (destinationIsFreeSpace(change)
        ?: movableElementCannotStopAtFrontOfRoom(change, previousChange)
        ?: cannotEnterToRoomThatIsNotDesired(change)
        ?: triesToComebackToTheSpaceInWhichItWasDuringSingleWalk(change, previousChange)
        ?: triesToStopAndAfterBreakNotGoDirectlyIntoTheRoom(change, previousChange)
        ?: triesToStopInsideTheRoom(change, previousChange)
        ?: BurrowChangeSucceeded(copy(burrowId = BurrowsCache.saveOrFindExisting(elements.applyChange(change)))))

    private fun Map<Coords, Element>.applyChange(change: BurrowStateChange): Map<Coords, Element> {
        return this.plus(
            listOf(
                change.startingCoords to FreeSpace,
                change.destination to Element.getByToken(change.movable.token)!!
            )
        )
    }

    private fun destinationIsFreeSpace(change: BurrowStateChange): BurrowChangeFailed? {
        return if (elements[change.destination] != FreeSpace) {
            TriedToStepIntoNotFreeSpace
        } else {
            null
        }
    }

    private fun movableElementCannotStopAtFrontOfRoom(
        currentChange: BurrowStateChange,
        previousChange: BurrowStateChange?
    ): BurrowChangeFailed? {
        val amountOfMovableElementsInFrontOfRoom = findMovableElementsInFrontOfRoom()
        val isDifferentElementOnMoveThanPreviously = previousChange?.destination != currentChange.startingCoords

        if (isDifferentElementOnMoveThanPreviously && amountOfMovableElementsInFrontOfRoom > 0) {
            return TriedToStopInFrontOfTheRoom
        }

        return null
    }

    private fun findMovableElementsInFrontOfRoom() = rooms.map { it.coordsInFrontOfRoom }
        .mapNotNull { elements[it] }
        .count(Element::isMovable)

    private fun cannotEnterToRoomThatIsNotDesired(change: BurrowStateChange): BurrowChangeFailed? {

        val room = rooms.find { it.contains(change.destination) }
        if (room != null) {
            if (room.contains(change.startingCoords)) {
                return null
            }
        }

        if (room != null &&
            !room.contains(change.startingCoords) &&
            room.forToken != change.movable.token
        ) {
            return TriedToStepIntoRoomWhichIsNotDestinationRoom
        }

        return room
            ?.let { it.allCoords.mapNotNull { elements[it] } }
            ?.let { it.all { it == FreeSpace || it.isTheSameInstanceAs(change.movable) } }
            ?.let { if (it) null else TriedToStepIntoRoomWhichIsNotDestinationRoom }
    }

    private fun triesToComebackToTheSpaceInWhichItWasDuringSingleWalk(
        change: BurrowStateChange,
        previousChange: BurrowStateChange?
    ) =
        if (change.destination == previousChange?.startingCoords && change.movable == previousChange.movable) {
            TriedToComebackToTheSpaceInWhichAlreadyItWasDuringSingleWalk
        } else {
            null
        }

    private fun triesToStopAndAfterBreakNotGoDirectlyIntoTheRoom(
        change: BurrowStateChange,
        previousChange: BurrowStateChange?
    ): BurrowChangeFailed? {
        if (previousChange == null || previousChange.destination == change.startingCoords) {
            return null
        }

        if (stopsCounter[previousChange.destination] == 1 && rooms.none { it.contains(previousChange.destination) }) {
            return TriedToStopAndThenGoNotDirectlyToTheRoom
        }

        return null
    }

    private fun triesToStopInsideTheRoom(
        change: BurrowStateChange,
        previousChange: BurrowStateChange?
    ): BurrowChangeFailed? {
        if (previousChange == null || previousChange.destination == change.startingCoords) {
            return null
        }

        val room = rooms.find { it.contains(previousChange.destination) }
        if (room != null && !room.isTheDeepestCoord(previousChange.destination.y) && room.amountOfMovablesInRoom(
                elements
            ) == 1
        ) {
            return TriedToStopInsideTheRoom
        }

        return null
    }

    private fun Element.isTheSameInstanceAs(movable: Movable): Boolean {
        return if (this.isMovable) {
            return fromElement(this) == movable
        } else {
            false
        }
    }

    override fun toString(): String {
        val minCoords = elements.entries.minOf { it.key }
        val maxCoords = elements.entries.maxOf { it.key }

        val rowSize = maxCoords.x - minCoords.x + 1
        val sortedCoords = elements.keys.sorted().windowed(size = rowSize, step = rowSize)
        return sortedCoords.joinToString(separator = "\n") {
            it.map { elements[it] }.joinToString(separator = "")
        }
    }

    fun minimalCostToReachDesiredState(): Int {
        return elements.filter { it.value.isMovable }
            .map { costToReachDesiredRoom(it.key, it.value.token) }
            .sum()
    }

    private fun costToReachDesiredRoom(startingPoint: Coords, token: Char): Int {
        val desiredRoom = rooms.find { it.forToken == token }!!
        val multiplier = fromElement(fromToken(token)!!).energyNeededToMove
        return (distanceBetween(startingPoint, desiredRoom)) * multiplier
    }

    private fun distanceBetween(startingPoint: Coords, desiredRoom: Room) =
        horizontalDistance(startingPoint, desiredRoom) + verticalDistance(startingPoint, desiredRoom)

    private fun verticalDistance(startingPoint: Coords, desiredRoom: Room): Int {
        return if (rooms.any { it.contains(startingPoint) }) {
            abs(startingPoint.y - 1) * 2
        } else {
            (desiredRoom.allCoords.first().y - 1)
        }
    }

    private fun horizontalDistance(a: Coords, b: Room): Int {
        return abs(b.allCoords.first().x - a.x)
    }

    fun amountOfFinalRooms() = rooms.count { it.isFinal(elements) }
    fun revert(change: BurrowStateChange): Burrow {
        return copy(
            rooms = rooms,
            burrowId = BurrowsCache.saveOrFindExisting(revertChange(change.startingCoords, change.destination))
        )
    }

    private fun revertChange(startingPoint: Coords, destination: Coords): Map<Coords, Element> {
        return elements.plus(startingPoint to elements[destination]!!)
            .plus(destination to FreeSpace)
    }
}

sealed class BurrowChangeResult
data class BurrowChangeSucceeded(val burrow: Burrow) : BurrowChangeResult()
sealed class BurrowChangeFailed : BurrowChangeResult()
object TriedToStepIntoNotFreeSpace : BurrowChangeFailed()
object TriedToStepIntoRoomWhichIsNotDestinationRoom : BurrowChangeFailed()
object TriedToStopInFrontOfTheRoom : BurrowChangeFailed()
object TriedToComebackToTheSpaceInWhichAlreadyItWasDuringSingleWalk : BurrowChangeFailed()
object TriedToStopAndThenGoNotDirectlyToTheRoom : BurrowChangeFailed()
object TriedToStopInsideTheRoom : BurrowChangeFailed()
