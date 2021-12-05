package eu.proszkie.adventofcode.day05

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class HydrothermalVentsAnalyzerSpec extends Specification implements WithResourceReadingAbility {
    HydrothermalVentsAnalyzer analyzer = new HydrothermalVentsAnalyzer()

    def "should properly analyze hydrothermal vents"() {
        given:
        List<String> input = getResource(path).readLines()
        List<Line> lines = LinesFactory.INSTANCE.from(input)

        when:
        Analysis actual = analyzer.analyze(lines)

        then:
        def dangerousCoordinatesSize = actual.mostDangerousCoordinates.size()
        dangerousCoordinatesSize == expected

        where:
        path                           || expected
        '/advent-of-code/day05/input1' || 12
        '/advent-of-code/day05/input2' || 21104
    }
}
