package eu.proszkie.adventofcode.day22

import kotlin.math.max
import kotlin.math.min

data class Reactor(private val cubes: Map<Coordinates, Cube> = mapOf()) {
    fun amountOfTurnedOnCubes(): Int {
        return cubes.values.filterIsInstance<EnabledCube>().size
    }

    fun withCuboid(cuboid: Cuboid) = Reactor(cubes.plus(cuboid.cubes))
}

sealed class Cuboid(
    xRange: LongRange,
    yRange: LongRange,
    zRange: LongRange,
    cube: Cube
) {
    val cubes: Map<Coordinates, Cube> = onlyAllowedRange(xRange).flatMap { x ->
        onlyAllowedRange(yRange).flatMap { y ->
            onlyAllowedRange(zRange).map { z -> Coordinates(x, y, z) }
        }
    }.associateWith { cube }

    private fun onlyAllowedRange(xRange: LongRange): LongRange {
        return LongRange(
            max(-50, xRange.first),
            min(xRange.last, 50)
        )
    }
}

data class EnabledCuboid(
    private val xRange: LongRange,
    private val yRange: LongRange,
    private val zRange: LongRange
) : Cuboid(xRange, yRange, zRange, EnabledCube)


data class DisabledCuboid(
    private val xRange: LongRange,
    private val yRange: LongRange,
    private val zRange: LongRange
) : Cuboid(xRange, yRange, zRange, DisabledCube)

sealed class Cube
object EnabledCube: Cube()
object DisabledCube: Cube()

data class Coordinates(val x: Long, val y: Long, val z: Long)