package eu.proszkie.adventofcode.day03

data class DiagnosticReport(
    val gamma: Int,
    val epsilon: Int,
    val lifeSupportingCriteria: LifeSupportingCriteria
) {
    companion object {
        fun of(report: List<String>): DiagnosticReport {
            val binaries = Binaries(report.map(::Binary))

            val gamma = (0..binaries.wordSize)
                .mapNotNull { index -> binaries.mostCommonBit(index) }

            val epsilon = gamma.map { it.opposite() }

            return DiagnosticReport(gamma.asInt(), epsilon.asInt(), LifeSupportingCriteria.of(binaries))
        }
    }
}

private fun List<Bit>.asInt() = Binary(this.map { it.raw }.joinToString(separator = "")).toInt()

data class LifeSupportingCriteria(
    val oxygenGeneratorRating: Int,
    val co2ScrubberRating: Int
) {
    companion object {
        fun of(report: Binaries): LifeSupportingCriteria {

            val oxygenGeneratorRating = calculateOxygenGeneratorRating(report)
            val co2ScrubberRating = calculateCo2ScrubberRating(report)
            return LifeSupportingCriteria(oxygenGeneratorRating, co2ScrubberRating)
        }

        private fun calculateOxygenGeneratorRating(report: Binaries): Int {
            return (0..report.wordSize).fold(report) { acc, index ->
                if (acc.hasOnlyOneElement()) {
                    acc
                } else {
                    val mostCommonBit = acc.mostCommonBit(index)
                    acc.allThatHaveGivenBitAtGivenIndex(mostCommonBit ?: One(), index)
                }
            }.getOnlyElement().toInt()
        }

        private fun calculateCo2ScrubberRating(report: Binaries): Int {
            return (0..report.wordSize).fold(report) { acc, index ->
                if (acc.hasOnlyOneElement()) {
                    acc
                } else {
                    val mostCommonBit = acc.mostCommonBit(index)
                    acc.allThatHaveGivenBitAtGivenIndex(mostCommonBit?.opposite() ?: Zero(), index)
                }
            }.getOnlyElement().toInt()
        }
    }
}

data class Binary(
    private val raw: String
) {
    fun size(): Int = raw.length
    fun at(index: Int): Bit {
        return Bit.of(raw[index])
    }

    fun toInt(): Int {
        return raw.toInt(2)
    }
}

data class Binaries(
    private val raw: List<Binary>
) {
    fun mostCommonBit(index: Int): Bit? {
        val summary = raw.fold(0 to 0) { acc, current ->
            when (current.at(index)) {
                is One -> acc.copy(first = acc.first + 1)
                is Zero -> acc.copy(second = acc.second + 1)
            }
        }

        return when {
            summary.first == summary.second -> null
            summary.first > summary.second -> One()
            summary.first < summary.second -> Zero()
            else -> throw IllegalStateException("Impossible.")
        }
    }

    fun allThatHaveGivenBitAtGivenIndex(bit: Bit, index: Int): Binaries {
        return Binaries(raw.filter { it.at(index) == bit })
    }

    fun hasOnlyOneElement() = raw.size == 1
    fun getOnlyElement(): Binary {
        if (hasOnlyOneElement()) {
            return raw.first()
        } else {
            throw IllegalStateException("There was expectation of single element, but was: ${raw.size} elements.")
        }
    }

    val wordSize: Int
        get() = raw.first().size() - 1
}

sealed class Bit {
    abstract val raw: Char

    companion object {
        fun of(raw: Char): Bit {
            return when (raw) {
                '1' -> One()
                '0' -> Zero()
                else -> throw IllegalStateException("Can be onle one or zero. Got: $raw")
            }
        }
    }

    fun opposite(): Bit {
        return when (this) {
            is One -> Zero()
            is Zero -> One()
        }
    }

    override fun toString(): String {
        return "$raw"
    }

}

data class One(override val raw: Char = '1') : Bit()
data class Zero(override val raw: Char = '0') : Bit()
