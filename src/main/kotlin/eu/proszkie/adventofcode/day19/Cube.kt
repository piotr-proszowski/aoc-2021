package eu.proszkie.adventofcode.day19

import eu.proszkie.adventofcode.day19.ManhattanDistanceCalculator as calculator

data class Cube(val beacons: Set<Point>, private val scanners: Set<Point>) {
    private val distancesToPoint = beacons.associateBy(this::distancesToAllPoints)

    fun distanceBetweenTwoFarthestScanners(): Int {
        val withStartingPoint = scanners.plus(Point(0, 0, 0))

        return withStartingPoint.flatMap { scanner ->
            withStartingPoint.map {
                calculator.calculate(scanner, it)
            }
        }.maxOf { it }
    }

    fun findCommonPointsWith(cube: Cube): Map<Point, Point> {
        val otherDistancesToPoint = cube.beacons.associateBy { point -> distancesToAllPoints(cube, point) }

        return distancesToPoint.entries.associate { entry ->
            entry.value to otherDistancesToPoint.filter { haveAtLeast11CommonDistances(it, entry) }.values.firstOrNull()
        }.filter { it.value != null }.mapValues { it.value!! }
    }

    private fun haveAtLeast11CommonDistances(
        it: Map.Entry<List<Int>, Point>,
        entry: Map.Entry<List<Int>, Point>
    ) = it.key.intersect(entry.key).size >= 11

    private fun distancesToAllPoints(cube: Cube, point: Point) =
        cube.beacons.map { calculator.calculate(point, it) }

    private fun distancesToAllPoints(point: Point) =
        beacons.map { calculator.calculate(point, it) }

    fun merge(cube: Cube): Cube {
        val (rotatedCube, movingVector) = cube.allRotations()
            .flatMap(Cube::permutateSigns)
            .asSequence()
            .map { it to findCommonPointsWith(it) }
            .map {
                it.first to it.second.entries.map {
                    Point(
                        it.key.x + it.value.x,
                        it.key.y + it.value.y,
                        it.key.z + it.value.z
                    )
                }
            }
            .first { it.second.toSet().size == 1 }

        return copy(
            beacons = beacons.plus(rotatedCube.beacons.map(Point::inverted).map { it.moveBy(movingVector.first()) }),
            scanners = scanners.plus(movingVector.first())
        )
    }

    fun rotateAlongsideX(): Cube {
        return copy(
            beacons = beacons.map { it.copy(y = it.z, z = -it.y) }.toSet()
        )
    }

    fun rotateAlongsideZ(): Cube {
        return copy(
            beacons = beacons.map { it.copy(x = it.y, y = -it.x) }.toSet()
        )
    }

    fun rotateAlongsideY(): Cube {
        return copy(
            beacons = beacons.map { it.copy(z = it.x, x = -it.z) }.toSet()
        )
    }

    fun allRotations(): Set<Cube> {
        return generateSequence(this, Cube::rotateAlongsideX)
            .take(4)
            .flatMap { rotatedX ->
                generateSequence(rotatedX, Cube::rotateAlongsideY).take(4)
                    .plus(generateSequence(rotatedX, Cube::rotateAlongsideZ).take(4))
            }
            .toSet()
    }

    fun permutateSigns(): Set<Cube> {
        return setOf(
            copy(beacons.map { it.copy(x = -it.x) }.toSet()),
            copy(beacons.map { it.copy(y = -it.y) }.toSet()),
            copy(beacons.map { it.copy(z = -it.z) }.toSet()),
            copy(beacons.map { it.copy(x = -it.x, y = -it.y) }.toSet()),
            copy(beacons.map { it.copy(x = -it.x, z = -it.z) }.toSet()),
            copy(beacons.map { it.copy(y = -it.y, z = -it.z) }.toSet()),
            copy(beacons.map { it.copy(x = -it.x, y = -it.y, z = -it.z) }.toSet()),
            this
        )
    }
}

data class Point(val x: Int, val y: Int, val z: Int) {
    fun moveBy(point: Point): Point {
        return copy(
            x = x + point.x,
            y = y + point.y,
            z = z + point.z
        )
    }

    fun inverted(): Point {
        return copy(
            x = -x,
            y = -y,
            z = -z
        )
    }
}