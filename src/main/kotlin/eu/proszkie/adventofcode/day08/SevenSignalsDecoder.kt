package eu.proszkie.adventofcode.day08

class SevenSignalsDecoder {
    fun decodeAll(signalsWithResults: List<SignalsWithResults>): DecodedSignals {
        return signalsWithResults.map { decode(it.signals, it.results) }
            .fold(DecodedSignals.empty()) { acc, decodedSignals -> acc.merge(decodedSignals) }
    }

    private fun decode(signals: Signals, results: Results): DecodedSignals {
        return DecodedSignals(results.deducedOutputSignals(signals), results.calculateOutput(signals))
    }
}

sealed class Signal {
    abstract val value: Int
    abstract val raw: String

    companion object {
        fun of(raw: String): Signal =
            when (raw.length) {
                2 -> OneSignal(raw)
                3 -> SevenSignal(raw)
                4 -> FourSignal(raw)
                7 -> EightSignal(raw)
                else -> UnknownSignal(raw)
            }
    }
}

data class OneSignal(override val raw: String, override val value: Int = 1) : Signal()
data class SevenSignal(override val raw: String, override val value: Int = 7) : Signal()
data class FourSignal(override val raw: String, override val value: Int = 4) : Signal()
data class EightSignal(override val raw: String, override val value: Int = 8) : Signal()
data class KnownSignal(override val raw: String, override val value: Int) : Signal()

data class UnknownSignal(override val raw: String, override val value: Int = 0) : Signal()

data class Signals(private val raw: List<Signal>) {
    private val deducedMapping: SignalsMapping = SignalsMapping(raw)

    fun deduce(it: Signal): Signal {
        return when (it) {
            is UnknownSignal -> deducedMapping.map(it)
            else -> it
        }
    }
}

class SignalsMapping(raw: List<Signal>) {
    private val oneSegments: String? = raw.find { it is OneSignal }?.raw
    private val fourSegments: String? = raw.find { it is FourSignal }?.raw
    private val sevenSegments: String? = raw.find { it is SevenSignal }?.raw
    private val eightSegments: String? = raw.find { it is EightSignal }?.raw

    fun map(it: Signal): Signal {
        return when {
            isTwo(it) -> KnownSignal(it.raw, 2)
            isThree(it) -> KnownSignal(it.raw, 3)
            isFive(it) -> KnownSignal(it.raw, 5)
            isSix(it) -> KnownSignal(it.raw, 6)
            isNine(it) -> KnownSignal(it.raw, 9)
            else -> it
        }
    }

    private fun isTwo(it: Signal): Boolean {
        return hasValidSize(it, 5)
            .and(hasValidAmountOfCommonSegmentsWith(it, one = 1, four = 2, seven = 2, eight = 5))
    }

    private fun isThree(it: Signal): Boolean {
        return hasValidSize(it, 5)
            .and(hasValidAmountOfCommonSegmentsWith(it, one = 2, four = 3, seven = 3, eight = 5))
    }

    private fun isFive(it: Signal): Boolean {
        return hasValidSize(it, 5)
            .and(hasValidAmountOfCommonSegmentsWith(it, one = 1, four = 3, seven = 2, eight = 5))
    }

    private fun isSix(it: Signal): Boolean {
        return hasValidSize(it, 6)
            .and(hasValidAmountOfCommonSegmentsWith(it, one = 1, four = 3, seven = 2, eight = 6))
    }

    private fun isNine(it: Signal): Boolean {
        return hasValidSize(it, 6)
            .and(hasValidAmountOfCommonSegmentsWith(it, one = 2, four = 4, seven = 3, eight = 6))
    }


    private fun hasValidAmountOfCommonSegmentsWith(
        signal: Signal,
        one: Int,
        four: Int,
        seven: Int,
        eight: Int
    ): Boolean {
        return validAmountOfCommonSegments(signal, oneSegments, one)
            .and(validAmountOfCommonSegments(signal, fourSegments, four))
            .and(validAmountOfCommonSegments(signal, sevenSegments, seven))
            .and(validAmountOfCommonSegments(signal, eightSegments, eight))
    }

    private fun validAmountOfCommonSegments(it: Signal, one: String?, count: Int): Boolean {
        if (one != null) {
            if (it.raw.count { one.contains(it) } == count) {
                return true
            }
            return false
        } else {
            return true
        }
    }

    private fun hasValidSize(it: Signal, size: Int): Boolean {
        return it.raw.length == size
    }

}

data class Results(private val raw: List<Signal>) {
    fun deducedOutputSignals(signals: Signals) = raw.map(signals::deduce)

    fun calculateOutput(signals: Signals) =
        deducedOutputSignals(signals).map(Signal::value)
            .map(Int::toString)
            .joinToString(separator = "")
            .toInt()
}

data class DecodedSignals(
    val outputSignals: List<Signal>,
    val output: Int
) {
    fun merge(decodedSignals: DecodedSignals): DecodedSignals {
        return copy(
            outputSignals = outputSignals + decodedSignals.outputSignals,
            output = output + decodedSignals.output
        )
    }

    companion object {
        fun empty() = DecodedSignals(listOf(), 0)
    }
}

object SignalsAndResultsFactory {
    fun fromString(line: String): SignalsWithResults {
        val inputAndOutput = line.split("|")
        val signals =
            inputAndOutput[0].split("\\s+".toRegex()).filter(String::isNotBlank).map(Signal::of).let(::Signals)
        val results =
            inputAndOutput[1].split("\\s+".toRegex()).filter(String::isNotBlank).map(Signal::of).let(::Results)
        return SignalsWithResults(signals, results)
    }
}

data class SignalsWithResults(val signals: Signals, val results: Results)