package eu.proszkie.adventofcode.day09

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class HeightMapSpec extends Specification implements WithResourceReadingAbility {

    def "should properly load height map"() {
        given:
        List<String> input = getResource(path).readLines()

        when:
        HeightMap heightMap = HeightMapFactory.@Companion.load(input)

        then:
        heightMap.riskLevels().sum() == expected

        where:
        path                           || expected
        '/advent-of-code/day09/input1' || 15
        '/advent-of-code/day09/input2' || 11
    }

    def "should find proper basins"() {
        given:
        List<String> input = getResource(path).readLines()

        when:
        HeightMap heightMap = HeightMapFactory.@Companion.load(input)

        then:
        def solution = heightMap.findProductOfSizesOfThreeBiggestBasins()
        solution == expected

        where:
        path                           || expected
        '/advent-of-code/day09/input1' || 1134
        '/advent-of-code/day09/input2' || 11
    }
}
