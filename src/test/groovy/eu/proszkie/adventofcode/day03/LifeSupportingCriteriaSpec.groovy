package eu.proszkie.adventofcode.day03

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class LifeSupportingCriteriaSpec extends Specification implements WithResourceReadingAbility {
    def "should properly calculate oxygen generator rating"() {
        given:
        List<String> input = getResource(path).readLines()
        Binaries asBinaries = new Binaries(input.collect { new Binary(it) })

        when:
        LifeSupportingCriteria lsc = LifeSupportingCriteria.@Companion.of(asBinaries)

        then:
        lsc.oxygenGeneratorRating * lsc.co2ScrubberRating == expected

        where:
        path                           | expected
        '/advent-of-code/day03/input1' | 230
        '/advent-of-code/day03/input2' | 2981085

    }
}
