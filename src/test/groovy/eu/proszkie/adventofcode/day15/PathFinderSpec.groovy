package eu.proszkie.adventofcode.day15

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class PathFinderSpec extends Specification implements WithResourceReadingAbility {
    def "should properly find path with lowest risk level"() {
        given:
        def input = readLines(pathToInput)
        def riskLevelMap = RiskLevelMapFactory.INSTANCE.createFromStrings(input)
        def pathFinder = new PathFinder(riskLevelMap)

        when:
        Path path = pathFinder.findPathWithLowestRisk()

        then:
        path.totalRisk() == expected

        where:
        pathToInput                    || expected
        '/advent-of-code/day15/input1' || 40
        '/advent-of-code/day15/input2' || 40
    }
}
