package eu.proszkie.adventofcode.day12

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class TaskSpecification extends Specification implements WithResourceReadingAbility {
    def 'should properly create caves with outgoing paths'() {
        given:
        List<String> input = readLines(path)

        when:
        Collection<Cave> actual = CavesFactory.INSTANCE.fromString(input)

        then:
        actual.size() == expected

        where:
        path                           || expected
        '/advent-of-code/day12/input1' || 6
    }

    def "should properly count distinct paths"() {
        given:
        List<String> input = readLines(path)
        Collection<Cave> caves = CavesFactory.INSTANCE.fromString(input)
        CavesDistinctPathsFinder finder = new CavesDistinctPathsFinder(caves)

        when:
        def actual = finder.findDistinctPaths().size()

        then:
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day12/input1' || 10
        '/advent-of-code/day12/input2' || 4792
    }

    def "should properly count distinct paths part 2"() {
        given:
        List<String> input = readLines(path)
        Collection<Cave> caves = CavesFactory.INSTANCE.fromString(input)
        CavesDistinctPathsFinder finder = new CavesDistinctPathsFinder(caves)

        when:
        def actual = finder.findDistinctPathsPart2().size()

        then:
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day12/input1' || 36
        '/advent-of-code/day12/input2' || 4792
    }
}
