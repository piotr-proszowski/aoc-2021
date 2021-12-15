package eu.proszkie.adventofcode.day14

data class PolymerTemplate(
    val activePolymers: List<Polymer>,
    val rules: PolymerRules,
    val usedPolymersGroupedByCount: Map<Char, Long> = emptyMap()
) {

    fun size(): Int = activePolymers.size

    fun mostCommonQuantityMinusLeastCommonQuantityAfterNSteps(numOfSteps: Int): Long {
        val nextAfterSteps = (1..numOfSteps).fold(this) { acc, _ -> acc.nextStep() }
        return nextAfterSteps.polymersGroupedByCount()
            .maxOf(Map.Entry<Char, Long>::value) - nextAfterSteps.polymersGroupedByCount()
            .minOf(Map.Entry<Char, Long>::value)
    }

    private fun polymersGroupedByCount() = usedPolymersGroupedByCount.mergedWith(activePolymers.countByGroup())

    private fun <T> Map<T, Long>.mergedWith(other: Map<T, Long>): Map<T, Long> {
        return (this.keys + other.keys).associateWith {
            (this[it] ?: 0) + (other[it] ?: 0)
        }
    }

    private fun List<Polymer>.countByGroup() =
        groupBy(Polymer::raw).mapValues { it.value.sumOf { if (it is MultiplePolimers) it.instancesCount else 1L } }

    fun nextStep(): PolymerTemplate {
        val duplicatedPolymers = activePolymers.groupBy { it }.mapValues { it.value.size }
            .filter { it.value > 2 }

        val multipliedPolymers = duplicatedPolymers.map { MultiplePolimers.from(it.key, it.value.toLong()) }

        val polymersAfterStep = activePolymers.filter { !it.deactivated }
            .filter { !duplicatedPolymers.keys.contains(it) }
            .plus(multipliedPolymers)
            .flatMap { it.buildNext(rules) }

        return copy(
            activePolymers = polymersAfterStep,
            usedPolymersGroupedByCount = usedPolymersGroupedByCount.mergedWith(
                activePolymers.map(Polymer::deactivate).countByGroup()
            )
        )
    }
}

sealed class Polymer(open val raw: Char, open val reactsWith: List<Char>, open val deactivated: Boolean = false) {
    abstract fun deactivate(): Polymer

    open fun buildNext(rulesLookup: PolymerRules): List<Polymer> {
        if (reactsWith.isEmpty()) return listOf()

        val firstParent = reactsWith.get(0)
        val firstKey = "$raw$firstParent"
        val firstValue = rulesLookup.valueFor(firstKey)

        if (reactsWith.size == 1) {
            return listOf(SinglePolymer(firstValue, listOf(firstParent, raw)))
        }

        val secondParent = reactsWith.get(1)
        val secondKey = "$secondParent$raw"
        val secondValue = rulesLookup.valueFor(secondKey)
        return listOf(
            SinglePolymer(firstValue, listOf(firstParent, raw)),
            SinglePolymer(secondValue, listOf(raw, secondParent))
        )
    }
}

data class SinglePolymer(
    override val raw: Char,
    override val reactsWith: List<Char>,
    override val deactivated: Boolean = false
) : Polymer(raw, reactsWith, deactivated) {

    override fun deactivate(): SinglePolymer {
        return copy(deactivated = true)
    }
}

data class MultiplePolimers(
    val instancesCount: Long,
    val singlePolymer: SinglePolymer
) : Polymer(singlePolymer.raw, singlePolymer.reactsWith, singlePolymer.deactivated) {
    companion object {
        fun from(polymer: Polymer, instancesCount: Long): MultiplePolimers {
            return when (polymer) {
                is SinglePolymer -> MultiplePolimers(instancesCount, polymer)
                is MultiplePolimers -> MultiplePolimers(
                    polymer.instancesCount * instancesCount, polymer.singlePolymer
                )
            }
        }
    }

    override fun deactivate(): Polymer {
        return copy(singlePolymer = singlePolymer.deactivate())
    }

    override fun buildNext(rulesLookup: PolymerRules): List<Polymer> {
        return super.buildNext(rulesLookup).map { from(it, instancesCount) }
    }

}


data class PolymerRules(private val raw: List<PolymerRule>) {
    private val groupedByKey = raw.associateBy(PolymerRule::key)

    fun size(): Int = raw.size
    fun valueFor(key: String): Char = groupedByKey.get(key)!!.value
}

data class PolymerRule(val key: String, val value: Char)

object PolymerFactory {
    fun readTemplateFromStrings(input: List<String>, rules: PolymerRules): PolymerTemplate {
        val polymers = input.first().windowed(2).map {
            SinglePolymer(it[0], listOf(it[1]))
        }
        return PolymerTemplate(polymers + SinglePolymer(input.first().last(), listOf()), rules)
    }

    fun readRulesFromStrings(input: List<String>): PolymerRules {
        return input.drop(2).map { it.split(" -> ") }
            .map { PolymerRule(it[0], it[1].first()) }
            .let(::PolymerRules)
    }
}