package eu.proszkie.adventofcode.day06

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class LanternfishHerdSpec extends Specification implements WithResourceReadingAbility {
    def "should simulate lanternfish herd growth properly"() {
        given:
        String input = getResource('/advent-of-code/day06/input2').text
        LanternfishHerd lanternfishHerd = LanternfishHerdFactory.INSTANCE.fromString(input)

        when:
        def actual = lanternfishHerd.afterDays(256)

        then:
        def size = actual.size()
        size == 1732731810807
    }
}
