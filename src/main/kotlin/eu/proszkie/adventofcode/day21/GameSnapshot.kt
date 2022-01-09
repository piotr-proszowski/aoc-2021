package eu.proszkie.adventofcode.day21

data class GameSnapshot(
    private val board: Board,
    private val leaderboard: Map<PlayerId, Score>,
    private val dice: DeterministicDiceState,
    private val playerQueue: List<PlayerId>
) {
    fun worstPlayerScore(): Long {
        return leaderboard.entries.minByOrNull { it.value }!!.value.raw
    }

    fun amountOfDiceThrows(): Int {
        return dice.countOfThrows
    }

    fun next(): GameSnapshot {
        val (nextDiceState, diceValuesSum) = dice.throwThreeTimes()
        val playerOnMove = playerQueue[0]
        val modifiedBoard = board with playerOnMove movedBy diceValuesSum
        return GameSnapshot(
            board = modifiedBoard,
            leaderboard = leaderboard.plus(playerOnMove to leaderboard[playerOnMove]!! + Score(modifiedBoard[playerOnMove])),
            dice = nextDiceState,
            playerQueue = playerQueue.drop(1).plus(playerOnMove)
        )
    }

    fun rewindToMomentWhenAnyPlayerReachGivenAmountOfPoints(threshold: Long): GameSnapshot {
        return generateSequence(this, GameSnapshot::next)
            .dropWhile { !it.leaderboard.map { it.value.raw }.any { it >= threshold } }
            .take(1)
            .first()
    }

}

data class PlayerId(val raw: Int)
data class Score(val raw: Long) : Comparable<Score> {
    operator fun plus(other: Score): Score {
        return Score(raw + other.raw)
    }

    override fun compareTo(other: Score): Int {
        return raw.compareTo(other.raw)
    }
}

data class Space(val raw: Long) {
    fun awayBy(by: Int): Space {
        val nextValue = (raw + by) % 10
        return Space(if(nextValue == 0L) 10 else nextValue)
    }
}

class Board(private val playerToSpace: Map<PlayerId, Space>) {
    infix fun with(playerOnMove: PlayerId): MoveBuilder {
        return MoveBuilder(this, playerOnMove)
    }

    operator fun get(player: PlayerId): Long {
        return playerToSpace[player]!!.raw
    }

    fun withMovedPlayerBy(player: PlayerId, by: Int): Board {
        return Board(playerToSpace = playerToSpace.plus(player to playerToSpace[player]!!.awayBy(by)))
    }
}

data class MoveBuilder(val board: Board, val player: PlayerId) {
    infix fun movedBy(stepsToDo: Int): Board {
        return board.withMovedPlayerBy(player, stepsToDo)
    }
}