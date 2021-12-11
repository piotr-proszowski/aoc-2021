package eu.proszkie.adventofcode.day11

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class OctopusesEnergySimulationSpec extends Specification implements WithResourceReadingAbility {

    OctopusEnergySimulation simulation = new OctopusEnergySimulation()

    def "should properly simulate octopuses energy flow"() {
        given:
        def input = getResource(path).readLines()
        def grid = OctopusGridFactory.INSTANCE.create(input)

        when:
        def actual = simulation.countFlashes(grid, steps)

        then:
        actual == expected


        where:
        path                           | steps || expected
        '/advent-of-code/day11/input1' | 1     || 0
        '/advent-of-code/day11/input1' | 2     || 35
        '/advent-of-code/day11/input1' | 100   || 1656
    }
}
