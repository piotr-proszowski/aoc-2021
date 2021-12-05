package eu.proszkie.adventofcode.day05

import eu.proszkie.adventofcode.day05.LineFactory.lineBetween
import kotlin.math.sign

class HydrothermalVentsAnalyzer {
    fun analyze(lines: List<Line>) =
        lines.calculateVelocitiesOfCrossingPoints()
            .getPointsWithVelocityGreaterOrEqualThanTwo()
            .let(::Analysis)

    private fun List<Line>.calculateVelocitiesOfCrossingPoints() =
        flatMap(Line::points)
            .groupBy { it }
            .mapValues { it.value.size }

    private fun Map<Point, Int>.getPointsWithVelocityGreaterOrEqualThanTwo() =
        entries.filter { it.value >= 2 }.map { it.key }
}

data class Line(val points: Set<Point>)

object LineFactory {
    infix fun lineBetween(points: Pair<Point, Point>) = fromTwoPoints(points.first, points.second)

    private fun fromTwoPoints(a: Point, b: Point): Line {
        return when {
            isVertical(a, b) -> VerticalLine from a to b
            isHorizontal(a, b) -> HorizontalLine from a to b
            else -> DiagonalLine from a to b
        }
    }

    private fun isVertical(a: Point, b: Point) =
        a.x == b.x

    private fun isHorizontal(a: Point, b: Point) =
        a.y == b.y
}

object HorizontalLine {
    fun of(a: Point, b: Point) = Line(a.x.rangeTo(b.x).map { it x a.y }.toSet())

    infix fun from(a: Point) = HorizontalLineBuilder(a)

    data class HorizontalLineBuilder(val a: Point) {
        infix fun to(b: Point) = if (a.x < b.x) HorizontalLine.of(a, b) else HorizontalLine.of(b, a)
    }
}

object VerticalLine {
    fun of(a: Point, b: Point) = Line(a.y.rangeTo(b.y).map { a.x x it }.toSet())

    infix fun from(a: Point) = VerticalLineBuilder(a)

    data class VerticalLineBuilder(val a: Point) {
        infix fun to(b: Point) = if (a.y < b.y) VerticalLine.of(a, b) else VerticalLine.of(b, a)
    }
}

object DiagonalLine {
    infix fun from(a: Point) = DiagonalLineBuilder(a)

    data class DiagonalLineBuilder(val a: Point) {
        infix fun to(b: Point) = DiagonalLine.create(a, b)
    }

    private fun create(from: Point, to: Point): Line {
        if (to.x < from.x) {
            return create(to, from)
        }
        return createDiagonal(from, to)
    }

    private fun createDiagonal(from: Point, to: Point): Line {
        val a = to.y - from.y
        val b = from.x - to.x
        val c = from.x * a + from.y * b
        val sign = -b.sign

        val y: (Int) -> Double = { (c - a * it) / b.toDouble() }

        val diagonal = numbersFromLine(from, y, sign)
        return Line(diagonal.takeWhile { it != to }.plus(to).toSet())
    }

    private fun numbersFromLine(from: Point, y: (Int) -> Double, sign: Int) =
        generateSequence(from) { lastPoint ->
            val nextXCoordinate = lastPoint.x + 1 * sign
            y(nextXCoordinate)
                .takeIf(this::isAnInteger)
                ?.let(Double::toInt)
                ?.let { nextYCoordinate -> nextXCoordinate x nextYCoordinate }
        }

    private fun isAnInteger(it: Double): Boolean {
        return it - it.toInt() == 0.0
    }
}

data class Analysis(val mostDangerousCoordinates: List<Point>)

data class Point(val x: Int, val y: Int) {

    companion object {
        fun fromString(raw: String): Point {
            val coords = raw.split(",")
            return coords[0].toInt() x coords[1].toInt()
        }
    }
}

infix fun Int.x(other: Int) = Point(this, other)

object LinesFactory {
    fun from(rawLines: List<String>): List<Line> {
        return rawLines.map { it.split("\\s+->\\s+".toRegex()).map(Point.Companion::fromString) }
            .map { lineBetween(it[0] and it[1]) }
    }
}

infix fun Point.and(other: Point) = this to other
