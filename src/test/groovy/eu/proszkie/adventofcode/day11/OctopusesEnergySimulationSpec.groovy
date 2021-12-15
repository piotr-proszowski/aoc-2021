package eu.proszkie.adventofcode.day11

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class OctopusesEnergySimulationSpec extends Specification implements WithResourceReadingAbility {

    OctopusEnergySimulator simulation = new OctopusEnergySimulator()

    def "should properly count flashes"() {
        given:
        def input = getResource(path).readLines()
        def grid = OctopusGridFactory.INSTANCE.create(input)

        when:
        def actual = simulation.countFlashes(grid, steps)

        then:
        actual == expected


        where:
        path                           | steps || expected
        '/advent-of-code/day11/input1' | 2     || 35
        '/advent-of-code/day11/input1' | 3     || 80
        '/advent-of-code/day11/input1' | 4     || 96
        '/advent-of-code/day11/input1' | 100   || 1656
    }


    def "should properly simulate octopuses energy flow"() {
        given:
        def input = getResource(path).readLines()
        def grid = OctopusGridFactory.INSTANCE.create(input)

        when:
        def actual = simulation.simulate(grid, steps).grid
        def expected = OctopusGridFactory.INSTANCE.create(getResource(expectedPath).readLines())

        then:
        gridsShouldBeTheSame(actual, expected)

        where:
        path                           | steps || expectedPath
        '/advent-of-code/day11/input0' | 1     || '/advent-of-code/day11/expected/input0-after-1-steps'
        '/advent-of-code/day11/input0' | 2     || '/advent-of-code/day11/expected/input0-after-2-steps'
        '/advent-of-code/day11/input1' | 4     || '/advent-of-code/day11/expected/input1-after-4-steps'
    }

    def gridsShouldBeTheSame(OctopusGrid first, OctopusGrid second) {
        verifyAll {
            def octopusThatDiffer = first.grid.keySet().collect { Tuple2.of(first.grid[it], second.grid[it]) }
                    .findAll { it[0].level != it[1].level }

            octopusThatDiffer.forEach { println("${it[0]} differs from ${it[1]}") }
            octopusThatDiffer.every { it[0] == it[1] }
        }
        return true
    }

    def "should find first time when all octopuses flash"() {
        given:
        def input = getResource(path).readLines()
        def grid = OctopusGridFactory.INSTANCE.create(input)

        when:
        def actual = simulation.findFirstStepWhenAllOctopusFlashSimultaneously(grid)

        then:
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day11/input1' || 195
//        '/advent-of-code/day11/input2' || 195

    }
}
