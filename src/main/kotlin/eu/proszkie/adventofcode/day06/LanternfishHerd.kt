package eu.proszkie.adventofcode.day06

data class LanternfishHerd(private val raw: Map<Lanternfish, Long>) {

    fun afterDays(days: Int) =
        generateSequence(this, LanternfishHerd::afterOneDay)
            .drop(days)
            .first()

    private fun afterOneDay(): LanternfishHerd {
        val herds = raw.map { it.key.nextDay().withValuesMultipliedBy(it.value) }
        return LanternfishHerd(herds.fold(mapOf()) { acc, current -> merge(acc, current) })
    }

    private fun merge(acc: Map<Lanternfish, Long>, current: Map<Lanternfish, Long>) =
        acc.keys.plus(current.keys).associateWith { (acc[it] ?: 0L) + (current[it] ?: 0L) }

    private fun Map<Lanternfish, Long>.withValuesMultipliedBy(multiplier: Long) =
        this.entries.associate { it.key to it.value * multiplier }

    fun size() = raw.values.sum()
}

data class Lanternfish(val cycle: Cycle) {

    companion object {
        fun newlyCreated() = Lanternfish(Cycle(8))
    }

    fun nextDay(): Map<Lanternfish, Long> {
        val cycle = cycle.next()
        val lanternfishNextDay = this.copy(cycle = cycle)
        return if (shouldCreateNewLanternfish()) {
            mapOf(lanternfishNextDay to 1L).plus(newlyCreated() to 1L)
        } else {
            mapOf(lanternfishNextDay to 1L)
        }
    }

    private fun shouldCreateNewLanternfish(): Boolean {
        return cycle.shouldCreateNewLanternfish
    }

}

data class Cycle(private val raw: Int) {

    val shouldCreateNewLanternfish: Boolean = raw == 0

    fun next() = copy(raw = if (raw == 0) 6 else raw - 1)
}

object LanternfishHerdFactory {
    fun fromString(raw: String): LanternfishHerd {
        return raw.split(",")
            .map(String::toInt)
            .map { Cycle(it) }
            .map(::Lanternfish)
            .groupBy { it }
            .mapValues { it.value.size.toLong() }
            .let(::LanternfishHerd)
    }
}