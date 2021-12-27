package eu.proszkie.adventofcode.day18

import kotlin.math.ceil
import kotlin.math.floor

sealed class SnailfishNumber {
    fun reduce(): ReductionResult {
        return generateSequence(explode(0).orIfUnchanged(this::split)) {
            it.result.explode(0)
                .orIfUnchanged { it.result.split() }
        }
            .dropWhile { it !is Unchanged }
            .first()
    }

    private fun ReductionResult.orIfUnchanged(reductionResult: () -> ReductionResult): ReductionResult {
        return if(this is Unchanged) reductionResult() else this
    }

    abstract fun magnitude(): Int
    abstract fun explode(level: Int): ReductionResult
    abstract fun theMostLeftIncreasedBy(carry: PlainNumber?): SnailfishNumber
    abstract fun theMostRightIncreasedBy(carry: PlainNumber?): SnailfishNumber
    abstract fun split(): ReductionResult
    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        return TwoSnailfishNumbers(this, other).reduce().result
    }
}

data class PlainNumber(val raw: Int) : SnailfishNumber() {

    override fun magnitude(): Int {
        return raw
    }

    override fun explode(level: Int): ReductionResult {
        return Unchanged(this)
    }

    override fun theMostLeftIncreasedBy(carry: PlainNumber?): SnailfishNumber {
        return this + carry
    }

    override fun theMostRightIncreasedBy(carry: PlainNumber?): SnailfishNumber {
        return this + carry
    }

    override fun split(): ReductionResult {
        if (raw > 9) {
            return Splited(
                TwoSnailfishNumbers(
                    PlainNumber(floor(raw.toDouble().div(2)).toInt()),
                    PlainNumber(ceil(raw.toDouble().div(2)).toInt())
                )
            )
        }

        return Unchanged(this)
    }

    operator fun plus(other: PlainNumber?): PlainNumber {
        return other?.let { PlainNumber(raw + it.raw) } ?: this
    }

    override fun toString(): String {
        return "$raw"
    }
}

data class TwoSnailfishNumbers(val left: SnailfishNumber, val right: SnailfishNumber) : SnailfishNumber() {

    override fun magnitude(): Int {
        if(left is PlainNumber && right is PlainNumber) {
            return left.raw * 3 + right.raw * 2
        }
        return left.magnitude() * 3 + right.magnitude() * 2
    }

    override fun explode(level: Int): ReductionResult {
        val leftReduced = left.explode(level + 1)
        if (leftReduced is Explosion) {
            return Explosion(
                TwoSnailfishNumbers(
                    leftReduced.result,
                    right.theMostLeftIncreasedBy(leftReduced.carryRight?.let(::PlainNumber))
                ), leftReduced.carryLeft, null
            )
        }

        val rightReduced = right.explode(level + 1)
        if (rightReduced is Explosion) {
            return Explosion(
                TwoSnailfishNumbers(
                    left.theMostRightIncreasedBy(rightReduced.carryLeft?.let(::PlainNumber)),
                    rightReduced.result
                ), null, rightReduced.carryRight
            )
        }

        return if (level == 4) {
            require(left is PlainNumber)
            require(right is PlainNumber)
            Explosion(PlainNumber(0), left.raw, right.raw)
        } else {
            Unchanged(this)
        }
    }

    override fun theMostLeftIncreasedBy(carry: PlainNumber?): SnailfishNumber {
        return TwoSnailfishNumbers(left.theMostLeftIncreasedBy(carry), right)
    }

    override fun theMostRightIncreasedBy(carry: PlainNumber?): SnailfishNumber {
        return TwoSnailfishNumbers(left, right.theMostRightIncreasedBy(carry))
    }

    override fun split(): ReductionResult {
        val leftSplited = left.split()
        if (leftSplited is Splited) {
            return Splited(TwoSnailfishNumbers(leftSplited.result, right))
        }
        val rightSplited = right.split()
        if (rightSplited is Splited) {
            return Splited(TwoSnailfishNumbers(left, rightSplited.result))
        }
        return Unchanged(this)
    }

    override fun toString(): String {
        return "[$left, $right]"
    }
}

sealed class ReductionResult(open val result: SnailfishNumber)
data class Explosion(override val result: SnailfishNumber, val carryLeft: Int?, val carryRight: Int?) :
    ReductionResult(result)

data class Splited(override val result: SnailfishNumber) : ReductionResult(result)
data class Unchanged(override val result: SnailfishNumber) : ReductionResult(result)
