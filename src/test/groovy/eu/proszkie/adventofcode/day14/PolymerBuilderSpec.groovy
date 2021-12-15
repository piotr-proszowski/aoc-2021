package eu.proszkie.adventofcode.day14

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class PolymerBuilderSpec extends Specification implements WithResourceReadingAbility {

    def "should properly read polymer template and rules"() {
        given:
        def input = readLines('/advent-of-code/day14/input1')

        when:
        PolymerRules rules = PolymerFactory.INSTANCE.readRulesFromStrings(input)
        PolymerTemplate template = PolymerFactory.INSTANCE.readTemplateFromStrings(input, rules)

        then:
        rules.size() == 16
        template.size() == 4
    }

    def "should properly build polymer"() {

        given:
        def input = readLines(path)
        PolymerRules rules = PolymerFactory.INSTANCE.readRulesFromStrings(input)
        PolymerTemplate template = PolymerFactory.INSTANCE.readTemplateFromStrings(input, rules)

        when:
        def actual = template.mostCommonQuantityMinusLeastCommonQuantityAfterNSteps(numOfSteps)

        then:
        actual == expected

        where:
        path                           | numOfSteps || expected
        '/advent-of-code/day14/input1' | 10         || 1588
        '/advent-of-code/day14/input1' | 40         || 2188189693529
        '/advent-of-code/day14/input2' | 40         || 3906445077999

    }
}
