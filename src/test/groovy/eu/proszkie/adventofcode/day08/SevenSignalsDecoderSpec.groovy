package eu.proszkie.adventofcode.day08

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class SevenSignalsDecoderSpec extends Specification implements WithResourceReadingAbility {

    SevenSignalsDecoder sevenSignalsDecoder = new SevenSignalsDecoder()

    def "should properly decode signals"() {
        given:
        def lines = getResource(path).readLines()
        List<SignalsWithResults> signalsWithResults = lines.collect { SignalsAndResultsFactory.INSTANCE.fromString(it) }

        when:
        DecodedSignals result = sevenSignalsDecoder.decodeAll(signalsWithResults)

        then:
        result.output == expected

        where:
        path                           | expected
        '/advent-of-code/day08/input1' | 61229
        '/advent-of-code/day08/input2' | 1083859
    }
}
