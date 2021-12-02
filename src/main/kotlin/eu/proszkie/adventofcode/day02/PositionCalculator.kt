package eu.proszkie.adventofcode.day02

class PositionCalculator {
    fun calculatePosition(moves: List<String>): Position {
        return parse(moves).fold(Position.ZERO) { position, move ->
            move.nextPosition(position)
        }
    }

    private fun parse(moves: List<String>): List<Move> {
        return moves.flatMap { Move.fromString(it) }
    }
}

data class Position(
    val horizontal: Int,
    val depth: Int,
    val aim: Int
) {
    companion object {
        val ZERO: Position = Position(0, 0, 0)
    }
}

sealed class Move() {
    abstract fun nextPosition(position: Position): Position

    companion object {
        fun fromString(move: String): List<Move> {
            val directionWithAmount = move.split(" ")
            val direction = directionWithAmount[0]
            val amount = directionWithAmount[1].toInt()
            return (1..amount).map {
                when (direction) {
                    "forward" -> Forward()
                    "up" -> Up()
                    "down" -> Down()
                    else -> throw IllegalStateException("Wrong direction")
                }
            }
        }
    }
}

class Forward : Move() {
    override fun nextPosition(position: Position): Position {
        return position.copy(horizontal = position.horizontal + 1, depth = position.depth + position.aim)
    }
}

class Down : Move() {
    override fun nextPosition(position: Position): Position {
        return position.copy(aim = position.aim + 1)
    }
}

class Up : Move() {
    override fun nextPosition(position: Position): Position {
        return position.copy(aim = position.aim - 1)
    }
}