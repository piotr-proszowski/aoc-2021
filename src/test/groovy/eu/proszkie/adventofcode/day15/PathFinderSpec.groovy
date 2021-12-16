package eu.proszkie.adventofcode.day15

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class PathFinderSpec extends Specification implements WithResourceReadingAbility {

    def pathFinder = new PathFinder()

    def "should properly find path with lowest risk level"() {
        given:
        def input = readLines(pathToInput)
        def riskLevelMap = RiskLevelMapFactory.INSTANCE.createFromStrings(input)

        when:
        int actual = pathFinder.findTotalRiskOfPathWithLowestRisk(riskLevelMap)

        then:
        actual == expected

        where:
        pathToInput                    || expected
        '/advent-of-code/day15/input0' || 20
        '/advent-of-code/day15/input1' || 40
        '/advent-of-code/day15/input2' || 537
    }

    def "should properly find path with lowest risk level for tiles"() {
        given:
        def input = readLines(pathToInput)
        def riskLevelMap = RiskLevelMapFactory.INSTANCE.createTilesFromStrings(input)

        when:
        int actual = pathFinder.findTotalRiskOfPathWithLowestRisk(riskLevelMap)

        then:
        actual == expected

        where:
        pathToInput                    || expected
        '/advent-of-code/day15/input0' || 97
        '/advent-of-code/day15/input1' || 315
        '/advent-of-code/day15/input2' || 40
    }
}
