package eu.proszkie.adventofcode.day03

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class DiagnosticReportResolverSpec extends Specification implements WithResourceReadingAbility {

    def "should properly resolve diagnostic report"() {
        given:
        List<String> rawDiagnosticReport = getResource(path).readLines()

        when:
        DiagnosticReport diagnosticReport = DiagnosticReport.@Companion.of(rawDiagnosticReport)
        def actual = diagnosticReport.gamma * diagnosticReport.epsilon

        then:
        actual == expected

        where:
        path                           | expected
        '/advent-of-code/day03/input1' | 198
        '/advent-of-code/day03/input2' | 3309596

    }

    def "should properly calculate life supporting criteria"() {
        given:
        List<String> rawDiagnosticReport = getResource(path).readLines()

        when:
        DiagnosticReport diagnosticReport = DiagnosticReport.@Companion.of(rawDiagnosticReport)

        then:
        diagnosticReport.lifeSupportingCriteria.oxygenGeneratorRating == 23
        diagnosticReport.lifeSupportingCriteria.co2ScrubberRating == 10

        where:
        path                           | _
        '/advent-of-code/day03/input1' | _
    }
}
