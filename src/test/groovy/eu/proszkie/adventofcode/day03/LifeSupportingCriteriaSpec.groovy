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
//        lsc.oxygenGeneratorRating == 23
//        lsc.co2ScrubberRating == 10
        lsc.oxygenGeneratorRating * lsc.co2ScrubberRating == 230

        where:
        path                           | _
        '/advent-of-code/third/input1' | _
        '/advent-of-code/third/input2' | _

    }
}
