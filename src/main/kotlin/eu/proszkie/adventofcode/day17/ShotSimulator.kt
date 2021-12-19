package eu.proszkie.adventofcode.day17

import eu.proszkie.adventofcode.takeWhileInclusive

class ShotSimulator {
    fun shot(from: Coords, velocities: Velocities): Trajectory {
        return Trajectory(from, velocities)
    }
}

data class Trajectory(
    val currentCoords: Coords,
    val velocities: Velocities,
    val highestYReached: Int = currentCoords.y
) {
    fun next(): Trajectory = Trajectory(
        currentCoords = Coords(currentCoords.x + velocities.x, currentCoords.y + velocities.y),
        velocities = velocities.next(),
        highestYReached = if (currentCoords.y + velocities.y > highestYReached) currentCoords.y + velocities.y else highestYReached
    )

    fun afterNumOfSteps(numOfSteps: Int): Trajectory {
        return (1..numOfSteps).fold(this) { result, _ -> result.next() }
    }

    fun takeIfOnWayThereIs(target: Coords): Trajectory? {
        val trajectory = generateSequence(this) { it.next() }
            .takeWhileInclusive { it.currentCoords != target && thereIsChanceToReachTheTarget(it, target) }
            .last()

        return if (trajectory.currentCoords == target) {
            trajectory
        } else {
            null
        }
    }

    private fun thereIsChanceToReachTheTarget(trajectory: Trajectory, target: Coords): Boolean {
        val current = trajectory.currentCoords
        if (current.x > target.x && current.y > target.y) {
            return false
        }

        if ((trajectory.velocities.x == 0 && current.x < target.x) || (trajectory.velocities.y <= 0 && current.y < target.y)) {
            return false
        }

        return true
    }
}

class VelocityFinder(private val shotSimulator: ShotSimulator) {
    fun findAllPossibleVelocities(target: Target): Set<Velocities> {
        return target.toCoords()
            .flatMap(this::findVelocitiesReachingTheTarget)
            .map(VelocitiesReachingTheTarget::velocities)
            .toSet()
    }

    fun deduceVelocity(target: Target): VelocitiesReachingTheTarget {
        return target.toCoords()
            .mapNotNull(this::deduceVelocity)
            .maxByOrNull(VelocitiesReachingTheTarget::highestYReached)!!
    }

    fun deduceVelocity(target: Coords): VelocitiesReachingTheTarget? {
        return getYRange(target)
            .mapNotNull { y -> deduceVelocity(y, target) }
            .maxByOrNull(VelocitiesReachingTheTarget::highestYReached)

    }

    private fun findVelocitiesReachingTheTarget(targetCoords: Coords) =
        getYRange(targetCoords)
            .mapNotNull { y -> deduceVelocity(y, targetCoords) }

    private fun getYRange(targetCoords: Coords) =
        (if (targetCoords.y > 0) (-targetCoords.y..targetCoords.y) else (targetCoords.y..-targetCoords.y))

    fun deduceVelocity(y: Int, target: Coords): VelocitiesReachingTheTarget? {
        return (0..target.x)
            .asSequence()
            .map { x -> tryToReachTheTarget(Velocities(x, y), target) }
            .takeWhileInclusive { it is VelocitiesNotReachingTheTarget }
            .filterIsInstance<VelocitiesReachingTheTarget>()
            .firstOrNull()
    }

    private fun tryToReachTheTarget(velocities: Velocities, target: Coords): VelocitiesWithTarget {
        val trajectory = shotSimulator.shot(Coords(0, 0), velocities)
        return trajectory.takeIfOnWayThereIs(target)
            ?.let { VelocitiesReachingTheTarget(velocities, target, it.highestYReached) }
            ?: VelocitiesNotReachingTheTarget(velocities, target)
    }
}

data class Velocities(val x: Int, val y: Int) {
    fun next(): Velocities {
        return Velocities(
            x = if (x == 0) x else x - 1,
            y = y - 1
        )
    }
}

sealed class VelocitiesWithTarget
data class VelocitiesReachingTheTarget(val velocities: Velocities, val target: Coords, val highestYReached: Int) :
    VelocitiesWithTarget()

data class VelocitiesNotReachingTheTarget(val velocities: Velocities, val target: Coords) : VelocitiesWithTarget()

data class Coords(val x: Int, val y: Int)

data class Target(val xRange: IntRange, val yRange: IntRange) {
    fun toCoords(): List<Coords> {
        return xRange.flatMap { x ->
            yRange.map { y -> Coords(x, y) }
        }
    }
}