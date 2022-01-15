package eu.proszkie.adventofcode.day22

data class Cuboid(
    private val x: Set<Long>,
    private val y: Set<Long>,
    private val z: Set<Long>,
    private val toSubtract: List<Cuboid> = listOf()
) {
    fun amountOfCubes(): Long =
        (x.size.toLong() * y.size.toLong() * z.size.toLong()) - toSubtract.sumOf(Cuboid::amountOfCubes)

    fun intersection(cuboid: Cuboid): Cuboid? {
        val xIntersection = x.map { it }.intersect(cuboid.x.map { it }.toSet())
        val yIntersection = y.map { it }.intersect(cuboid.y.map { it }.toSet())
        val zIntersection = z.map { it }.intersect(cuboid.z.map { it }.toSet())

        val result = Cuboid(xIntersection, yIntersection, zIntersection)
        return if (result.amountOfCubes() == 0L) {
            null
        } else {
            result
        }
    }

    operator fun minus(other: Cuboid): Cuboid {
        return copy(
            toSubtract = this.intersection(other)
                ?.let { subtrahend -> toSubtract.map { it - subtrahend }.plus(subtrahend) } ?: toSubtract
        )
    }
}

data class CuboidWithAction(val cuboid: Cuboid, val action: Action) {
    operator fun minus(other: CuboidWithAction?): CuboidWithAction? {
        if (other?.cuboid == null) {
            return this
        }
        return copy(cuboid = cuboid.minus(other.cuboid))
    }

    fun intersection(other: Cuboid): CuboidWithAction? {
        return cuboid.intersection(other)?.let {
            copy(cuboid = it)
        }
    }
}

sealed class Action

object Enable : Action()
object Disable : Action()

class Reactor(private val cuboidsWithAction: List<CuboidWithAction> = listOf()) {
    fun withActionsPerformed(newActions: List<CuboidWithAction>): Reactor {
        return Reactor(cuboidsWithAction = cuboidsWithAction.plus(newActions))
    }

    fun amountOfEnabledCubes(): Long {
        return cuboidsWithAction.fold(listOf<CuboidWithAction>()) { acc, next ->
            println("Amount of processed cubes: ${acc.size}")
            val result = acc.mapNotNull { it - next }
            if(next.action is Enable) result.plus(next) else result
        }.filter { it.action is Enable }.sumOf { it.cuboid.amountOfCubes() }
    }

    fun amountOfEnabledCubesDuringInitialization(): Long {
        val initializationCube = Cuboid((-50L..50L).toSet(), (-50L..50L).toSet(), (-50L..50L).toSet())
        return cuboidsWithAction.fold(listOf<CuboidWithAction>()) { acc, next ->
            next.intersection(initializationCube)?.let { nextCuboid ->
                acc.mapNotNull { it - nextCuboid }.plus(nextCuboid)
            } ?: acc
        }.filter { it.action is Enable }
            .sumOf { it.cuboid.amountOfCubes() }
    }
}