package eu.proszkie.adventofcode.day04

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class WinningBingoFinderSpec extends Specification implements WithResourceReadingAbility {

    BingoSolver winningBingoFinder = new BingoSolver()

    def "should properly find first solved bingo"() {

        given:
        List<String> input = getResource(path).readLines()
        Bingo bingo = BingoFactory.INSTANCE.createBingo(input)

        when:
        WinningBingoBoard solvedBingo = winningBingoFinder.findFirstWinningBingoBoard(bingo)

        then:
        solvedBingo.sumOfUnmarkedFields() * solvedBingo.lastNumberCalled == expected

        where:
        path                           || expected
        '/advent-of-code/day04/input1' || 4512
        '/advent-of-code/day04/input2' || 55770
    }

    def "should properly find last solved bingo"() {

        given:
        List<String> input = getResource(path).readLines()
        Bingo bingo = BingoFactory.INSTANCE.createBingo(input)

        when:
        WinningBingoBoard solvedBingo = winningBingoFinder.findLastWinningBingoBoard(bingo)

        then:
        solvedBingo.sumOfUnmarkedFields() * solvedBingo.lastNumberCalled == expected

        where:
        path                           || expected
        '/advent-of-code/day04/input1' || 1924
        '/advent-of-code/day04/input2' || 2980
    }
}
