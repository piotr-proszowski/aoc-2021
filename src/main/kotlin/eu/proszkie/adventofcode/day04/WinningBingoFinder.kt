package eu.proszkie.adventofcode.day04

import java.util.*


class BingoSolver {
    fun findFirstWinningBingoBoard(bingo: Bingo): WinningBingoBoard {
        val desiredBingo = callNumbersUntilFirstWinningBoard(bingo)
            .callNextNumber()

        return WinningBingoBoard(desiredBingo.lastCalledNumber, desiredBingo.winningBoards().first())
    }

    private fun callNumbersUntilFirstWinningBoard(bingo: Bingo) =
        generateSequence(bingo, Bingo::callNextNumber)
            .takeWhile(this::thereAreNoWinningBoards)
            .last()

    private fun thereAreNoWinningBoards(it: Bingo) = it.winningBoards().isEmpty()

    fun findLastWinningBingoBoard(bingo: Bingo): WinningBingoBoard {
        val justBeforeDesiredBingo = callNumbersUntilLastWinningBoard(bingo)
        val desiredBingo = justBeforeDesiredBingo.callNextNumber()
        val lastWinningBoard = findLastWinningBoard(desiredBingo, justBeforeDesiredBingo)

        return WinningBingoBoard(desiredBingo.lastCalledNumber, lastWinningBoard)
    }

    private fun callNumbersUntilLastWinningBoard(bingo: Bingo) =
        generateSequence(bingo, Bingo::callNextNumber)
            .takeWhile(this::notAllBoardsAreWinningYet)
            .last()

    private fun findLastWinningBoard(
        desiredBingo: Bingo,
        justBeforeDesiredBingo: Bingo
    ) = desiredBingo.winningBoards()
        .firstBoardThatBecameWinningAfterLastCalledNumber(justBeforeDesiredBingo.winningBoards())

    private fun notAllBoardsAreWinningYet(it: Bingo) = !it.allBoardsAreWinning()

    private fun List<Board>.firstBoardThatBecameWinningAfterLastCalledNumber(winningBoardsBefore: List<Board>) =
        this.first { winningBoardsBefore.doesNotContain(it) }

    private fun List<Board>.doesNotContain(board: Board) = !this.contains(board)
}

data class Bingo(val drawnNumbers: DrawnNumbers, val boards: List<Board>) {
    val lastCalledNumber: Int by lazy {
        drawnNumbers.lastCalledNumber()
    }

    fun winningBoards() = boards.filter(Board::isWinning)

    fun callNextNumber(): Bingo {
        return copy(
            drawnNumbers = drawnNumbers.withNextNumberCalled(),
            boards = boards.map { it.withFieldMarked(drawnNumbers.withNextNumberCalled().lastCalledNumber()) }
        )
    }

    fun allBoardsAreWinning() = boards.all(Board::isWinning)
}

data class WinningBingoBoard(val lastNumberCalled: Int, val board: Board) {
    fun sumOfUnmarkedFields() = board.unmarkedFields().sumOf(Field::value)
}

data class Board(private val rows: List<Row>, val id: UUID) {
    fun unmarkedFields() = rows.flatMap(Row::unmarkedFields)

    fun isWinning() = rows.any(this::isWinningRow) || ColumnsFactory.from(rows).any(this::isWinningColumn)

    private fun isWinningRow(it: Row) = it.unmarkedFields().isEmpty()

    private fun isWinningColumn(it: Column) = it.unmarkedFields().isEmpty()

    fun withFieldMarked(number: Int) = copy(rows = rows.map { it.withFieldMarked(number) })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class ColumnsFactory(val raw: List<Column>) {
    companion object {
        fun from(rows: List<Row>): List<Column> {
            val size = rows.first().size()
            return (0 until size)
                .map { index -> rows.map { it.at(index) } }
                .map(::Column)
        }
    }
}

data class Column(private val raw: List<Field>) {
    fun unmarkedFields() = raw.unmarkedFields()
}

data class Row(private val raw: List<Field>) {

    fun unmarkedFields() = raw.unmarkedFields()

    fun withFieldMarked(number: Int): Row {
        return copy(
            raw = raw.map {
                if (it.value == number) it.copy(isMarked = true)
                else it
            }
        )
    }

    fun size() = raw.size
    fun at(index: Int) = raw[index]
}

data class Field(val value: Int, val isMarked: Boolean = false) {
    fun isUnmarked(): Boolean {
        return !isMarked
    }
}

data class DrawnNumbers(private val raw: List<Int>, private val calledNumbers: Set<Int> = linkedSetOf()) {
    fun lastCalledNumber(): Int {
        return calledNumbers.last()
    }

    fun withNextNumberCalled(): DrawnNumbers {
        return copy(calledNumbers = calledNumbers.plus(raw.first { !calledNumbers.contains(it) }))
    }
}

object BingoFactory {
    fun createBingo(input: List<String>): Bingo {
        val drawnNumbers = DrawnNumbers(input.first().split(",").map(String::toInt))
        val boards = BoardsFactory.from(input.drop(1))
        return Bingo(drawnNumbers, boards)
    }
}

object BoardsFactory {
    fun from(input: List<String>): List<Board> {
        return input.fold(listOf<BoardBuilder>()) { acc, line -> processLine(line, acc) }
            .map(BoardBuilder::build)
    }

    private fun processLine(line: String, acc: List<BoardBuilder>) = if (line.isBlank()) {
        acc.plus(BoardBuilder())
    } else {
        acc.last().addRow(line)
        acc
    }
}

class BoardBuilder {
    private val rows: MutableList<Row> = LinkedList()
    fun addRow(line: String) {
        val row = Row(
            line.split("\\s+".toRegex())
                .filter(String::isNotBlank)
                .map(String::toInt)
                .map(::Field)
        )
        rows.add(row)
    }

    fun build(): Board {
        return Board(rows, UUID.randomUUID())
    }
}

fun List<Field>.unmarkedFields(): List<Field> {
    return this.filter(Field::isUnmarked)
}
