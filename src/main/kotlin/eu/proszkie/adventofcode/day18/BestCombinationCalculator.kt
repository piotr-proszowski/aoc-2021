package eu.proszkie.adventofcode.day18

object BestCombinationCalculator {
    fun permutate(snailfishes: List<SnailfishNumber>): List<Pair<SnailfishNumber, SnailfishNumber>> {
        return snailfishes.flatMap { snailfish ->
            snailfishes.filter { it != snailfish }
                .map { snailfish to it }
        }
    }

    fun findAllCombinationsOfAddedSnailfishes(snailfishes: List<SnailfishNumber>): List<SnailfishNumber> {
        return permutate(snailfishes).map { it.first + it.second }
    }

    fun findHighestPossibleMagnitudeByAddingOnlyTwoSnailfishes(snailfishNumbers: List<SnailfishNumber>): Int {
        return findAllCombinationsOfAddedSnailfishes(snailfishNumbers).maxByOrNull(SnailfishNumber::magnitude)!!
            .magnitude()
    }
}