package eu.proszkie.adventofcode.day11

import eu.proszkie.adventofcode.WithResourceReadingAbility
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class OctopusesBuilder implements WithResourceReadingAbility {

    static Octopuses octopuses(List<Integer>... octopusesRows) {
        return OctopusesFactory.INSTANCE.fromRowsOfEnergyLevels(octopusesRows.toList())
    }

    static Octopuses fromFile(String filePath) {
        def rows = OctopusesBuilder.class.getResource("/advent-of-code/day11/$filePath").readLines().collect {
            it.chars().collect { it as char }.collect { Character.getNumericValue(it) }
        }
        return OctopusesFactory.INSTANCE.fromRowsOfEnergyLevels(rows)
    }
}
