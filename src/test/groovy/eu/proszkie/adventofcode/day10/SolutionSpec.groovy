package eu.proszkie.adventofcode.day10

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class SolutionSpec extends Specification implements WithResourceReadingAbility {

    SyntaxScoring syntaxScoring = new SyntaxScoring()

    def "should properly score errors in lines"() {
        given:
            List<String> input = getResource(path).readLines()

        when:
            def actual = input.collect { syntaxScoring.scoreLineErrors(it) }.sum()

        then:
            actual == expected

        where:
            path                           || expected
            '/advent-of-code/day10/input1' || 26397
            '/advent-of-code/day10/input2' || 364389
    }

    def "should properly score autocomplete in lines"() {
        given:
            List<String> input = getResource(path).readLines()

        when:
            def scores = input.collect { syntaxScoring.scoreLineAutocomplete(it) }.findAll { it != null }.sort()
            def middleElement = scores[scores.size() / 2]

        then:
            middleElement == expected

        where:
            path                           || expected
            '/advent-of-code/day10/input1' || 288957
            '/advent-of-code/day10/input2' || 2870201088
    }
}
