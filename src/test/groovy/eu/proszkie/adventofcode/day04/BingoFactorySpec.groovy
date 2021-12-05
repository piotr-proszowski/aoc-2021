package eu.proszkie.adventofcode.day04

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class BingoFactorySpec extends Specification implements WithResourceReadingAbility {

    def "should properly construct bingo"() {
        given:
        List<String> input = getResource('/advent-of-code/day04/input1').readLines()

        when:
        Bingo bingo = BingoFactory.INSTANCE.createBingo(input)

        then:
        bingo.drawnNumbers == new DrawnNumbers([7, 4, 9, 5, 11, 17, 23, 2, 0, 14, 21, 24, 10, 16, 13, 6, 15, 25, 12, 22, 18, 20, 8, 19, 3, 26, 1], [] as Set)
        bingo.boards.size() == 3
    }
}
