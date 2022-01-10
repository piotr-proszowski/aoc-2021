package eu.proszkie.adventofcode.day21

data class GameSnapshot(
    private val board: Board,
    private val leaderboard: Map<PlayerId, Score>,
    private val playerQueue: List<PlayerId>
) {
    private lateinit var dice: DiceState

    constructor(
        board: Board,
        leaderboard: Map<PlayerId, Score>,
        playerQueue: List<PlayerId>,
        diceState: DiceState
    ) : this(board, leaderboard, playerQueue) {
        dice = diceState
    }

    fun worstPlayerScore(): Long {
        return leaderboard.entries.minByOrNull { it.value }!!.value.raw
    }

    fun amountOfDiceThrows(): Int {
        return dice.countOfThrows
    }

    fun next(): List<GameSnapshot> {
        return dice.throwThreeTimes()
            .map { threeThrowsResult ->
                val (nextDiceState, diceValuesSum) = threeThrowsResult
                val playerOnMove = playerQueue[0]
                val modifiedBoard = board with playerOnMove movedBy diceValuesSum
                GameSnapshot(
                    board = modifiedBoard,
                    leaderboard = leaderboard.plus(playerOnMove to leaderboard[playerOnMove]!! + Score(modifiedBoard[playerOnMove])),
                    playerQueue = playerQueue.drop(1).plus(playerOnMove),
                    diceState = nextDiceState,
                )
            }
    }

    fun rewindToMomentWhenAnyPlayerReachGivenAmountOfPoints(threshold: Long): Map<GameSnapshot, Long> {
        return generateSequence(mapOf(this to 1L)) {
            val gamesThatDidNotEndYet = it.entries.filter { !it.key.gameEnded(threshold) }
            val gamesThatEnded = it.entries.filter { it.key.gameEnded(threshold) }.associate { it.key to it.value }

            val nextSnapshots = gamesThatDidNotEndYet.map { it.key.next() to it.value }
                .flatMap { snapshot -> snapshot.first.map { it to snapshot.second } }
                .groupBy { it.first }
                .mapValues { it.value.sumOf { it.second } }

            gamesThatEnded.keys.plus(nextSnapshots.keys)
                .associateWith { (gamesThatEnded[it] ?: 0L) + (nextSnapshots[it] ?: 0L) }
        }.dropWhile { !it.all { it.key.gameEnded(threshold) } }
            .take(1)
            .first()
    }

    private fun gameEnded(threshold: Long) =
        this.leaderboard.map { it.value.raw }.any { it >= threshold }

    fun winnerForThreshold(threshold: Long): PlayerId? {
        return this.leaderboard.filter { it.value.raw >= threshold }.map { it.key }.firstOrNull()
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
        return Space(if (nextValue == 0L) 10 else nextValue)
    }
}

data class Board(private val playerToSpace: Map<PlayerId, Space>) {
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