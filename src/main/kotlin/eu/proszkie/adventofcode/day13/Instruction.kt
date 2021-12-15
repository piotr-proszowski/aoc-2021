package eu.proszkie.adventofcode.day13

import kotlin.math.abs

data class Instruction(private val dots: Set<Dot>) {
    val dotsCount = dots.size

    fun printPage() {
        val maxX = dots.maxOf(Dot::x)
        val maxY = dots.maxOf(Dot::y)

        val message = (0..maxX).map { x ->
            (0..maxY).map { y ->
                if (dots.any { it.x == x && it.y == y }) "#" else "."
            }.joinToString(separator = " ")
        }.joinToString(separator = "\n")

        print(message)
    }

    fun fold(folds: List<Fold>): Instruction {
        return folds.fold(this) { acc, next -> acc.fold(next) }
    }

    fun fold(fold: Fold): Instruction {
        return when (fold) {
            is FoldX -> foldX(fold.raw)
            is FoldY -> foldY(fold.raw)
        }
    }

    private fun foldY(y: Int): Instruction {
        return copy(dots = dots.map { it.mirrorAtY(y) }.toSet())
    }

    private fun foldX(x: Int): Instruction {
        return copy(dots = dots.map { it.mirrorAtX(x) }.toSet())
    }
}

object InstructionFactory {
    fun createFromStrings(input: List<String>): Instruction {
        val dots = input.filter { !it.contains("fold") }
            .filter(String::isNotBlank)
            .map { it.split(",") }
            .map { it.map(String::toInt) }
            .map { Dot(it[0], it[1]) }
            .toSet()

        return Instruction(dots)
    }
}

object FoldsFactory {
    fun createFromStrings(input: List<String>): List<Fold> {
        return input.filter { it.contains("fold along") }
            .map { it.removePrefix("fold along ") }
            .map { it.split("=") }
            .map {
                when (it[0]) {
                    "x" -> FoldX(it[1].toInt())
                    "y" -> FoldY(it[1].toInt())
                    else -> throw IllegalStateException("Should be x or y but was: ${it[0]}")
                }
            }
    }
}

sealed class Fold
data class FoldX(val raw: Int) : Fold()
data class FoldY(val raw: Int) : Fold()

data class Dot(val x: Int, val y: Int) {
    fun mirrorAtY(mirror: Int): Dot {
        return if (y > mirror) {
            copy(y = mirror - abs(y - mirror))
        } else {
            this
        }
    }

    fun mirrorAtX(mirror: Int): Dot {
        return if (x > mirror) {
            copy(x = mirror - abs(x - mirror))
        } else {
            this
        }
    }
}