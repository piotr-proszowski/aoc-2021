package eu.proszkie.adventofcode.day18

import eu.proszkie.adventofcode.WithResourceReadingAbility
import kotlin.Pair
import spock.lang.Specification

import static eu.proszkie.adventofcode.day18.SnailfishNumberBuilder.snailfish

class SnailFishNumberSpec extends Specification implements WithResourceReadingAbility {

    BestCombinationCalculator calculator = BestCombinationCalculator.INSTANCE

    def 'should build plain number'() {
        expect:
        snailfish(1) == new PlainNumber(1)
    }

    def 'should build two plain numbers'() {
        expect:
        snailfish([1, 1]) == new TwoSnailfishNumbers(new PlainNumber(1), new PlainNumber(1))
    }

    def 'should build nested snailfishes'() {
        expect:
        snailfish([[2, 2], 1]) == new TwoSnailfishNumbers(new TwoSnailfishNumbers(new PlainNumber(2), new PlainNumber(2)), new PlainNumber(1))
    }

    def 'should not reduce snailfish number if it is not at fourth level'() {
        expect:
        snailfish([3, 4]).reduce() == new Unchanged(snailfish([3, 4]))
    }

    def 'should reduce snailfish number if it is at fourth level'() {
        expect:
        snailfish([1, 1]).explode(4) == new Explosion(snailfish(0), 1, 1)
    }

    def 'should reduce snailfish number if it explodes on left'() {
        expect:
        snailfish([[1, 1], 1]).explode(3) == new Explosion(snailfish([0, 2]), 1, null)
    }

    def 'should reduce snailfish number if it explodes on left and right is not a plain number'() {
        expect:
        snailfish([[1, 1], [1, 1]]).explode(3) == new Explosion(snailfish([0, [2, 1]]), 1, null)
    }

    def 'should reduce snailfish number if it explodes on right'() {
        expect:
        snailfish([1, [1, 1]]).explode(3) == new Explosion(snailfish([2, 0]), null, 1)
    }

    def 'should reduce snailfish number if both carries have to be carried'() {
        expect:
        snailfish([[1, [1, 1]], 1]).explode(2) == new Explosion(snailfish([[2, 0], 2]), null, null)
    }

    def 'should build snailfish from string'() {
        expect:
        snailfish('[1, [1, 1]]') == snailfish([1, [1, 1]])
    }

    def 'should add two snailfishes'() {
        expect:
        snailfish([1, 1]) + snailfish([1, 1]) == snailfish([[1, 1], [1, 1]])
    }

    def 'should add snailfishes properly'() {
        given:
        def snailfishes = readLines(inputPath).collect { snailfish(it) }
        def expected = readLines(expectedPath).collect { snailfish(it) }.first()

        when:
        def actual = snailfishes.inject { prev, next -> prev + next }

        then:
        actual == expected

        where:
        inputPath                      || expectedPath
        '/advent-of-code/day18/input1' || '/advent-of-code/day18/input1-expected'
        '/advent-of-code/day18/input3' || '/advent-of-code/day18/input3-expected'
        '/advent-of-code/day18/input2' || '/advent-of-code/day18/input2-expected'
        '/advent-of-code/day18/input4' || '/advent-of-code/day18/input4-expected'
    }

    def 'should split snailfish'() {
        expect:
        snailfish(raw).split().result == snailfish(expected)

        where:
        raw || expected
        10  || [5, 5]
        11  || [5, 6]
        13  || [6, 7]
        9   || 9
    }

    def 'should split nested snailfish'() {
        expect:
        snailfish([1, [10, 1]]).split().result == snailfish([1, [[5, 5], 1]])
    }

    def 'should first reduce and then split'() {
        expect:
        snailfish([[1, [1, [1, [15, 1]]]], 1]).explode(0).result == snailfish([[1, [1, [16, 0]]], 2])
        snailfish([[1, [1, [16, 0]]], 2]).split().result == snailfish([[1, [1, [[8, 8], 0]]], 2])
        snailfish([[1, [1, [[8, 8], 0]]], 2]).explode(0).result == snailfish([[1, [9, [0, 8]]], 2])

        snailfish([[1, [1, [1, [15, 1]]]], 1]).reduce().result == snailfish([[1, [9, [0, 8]]], 2])
    }

    def 'should calculate magnitude'() {
        expect:
        snailfish([2, 9]).magnitude() == 24
        snailfish([[9, 1], [1, 9]]).magnitude() == 129
        snailfish([[1, 2], [[3, 4], 5]]).magnitude() == 143
        snailfish([[[[8, 7], [7, 7]], [[8, 6], [7, 7]]], [[[0, 7], [6, 6]], [8, 7]]]).magnitude() == 3488
    }

    def 'should permutate list of snailfishes'() {
        expect:
        calculator.permutate(input).containsAll(output)

        where:
        input              || output
        twoSnailfishes()   || twoPairsOfSnailfishes()
        threeSnailfishes() || sixSnailfishPairs()
    }

    def 'should find all combinations of added snailfishes'() {
        when:
        List<SnailfishNumber> addedSnailfishes = calculator.findAllCombinationsOfAddedSnailfishes(input)

        then:
        addedSnailfishes.containsAll(output)

        where:
        input            || output
        twoSnailfishes() || [snailfish([1, 1]) + snailfish([2, 2]), snailfish([2, 2]) + snailfish([1, 1])]
    }

    def 'should find highest possible magnitude by adding two best fitting snailfishnumbers'() {
        expect:
        calculator.findHighestPossibleMagnitudeByAddingOnlyTwoSnailfishes(input) == output

        where:
        input                     || output
        twoSnailfishes()          || 40
        exampleSnailfishNumbers() || 3993
        homeworkAssignement()     || 4616
    }

    private List<SnailfishNumber> homeworkAssignement() {
        return readLines('/advent-of-code/day18/homework').collect { snailfish(it) }
    }

    private static List<SnailfishNumber> exampleSnailfishNumbers() {
        return [
                snailfish([[[0, [5, 8]], [[1, 7], [9, 6]]], [[4, [1, 2]], [[1, 4], 2]]]),
                snailfish([[[5, [2, 8]], 4], [5, [[9, 9], 0]]]),
                snailfish([6, [[[6, 2], [5, 6]], [[7, 6], [4, 7]]]]),
                snailfish([[[6, [0, 7]], [0, 9]], [4, [9, [9, 0]]]]),
                snailfish([[[7, [6, 4]], [3, [1, 3]]], [[[5, 5], 1], 9]]),
                snailfish([[6, [[7, 3], [3, 2]]], [[[3, 8], [5, 7]], 4]]),
                snailfish([[[[5, 4], [7, 7]], 8], [[8, 3], 8]]),
                snailfish([[9, 3], [[9, 9], [6, [4, 9]]]]),
                snailfish([[2, [[7, 7], 7]], [[5, 8], [[9, 3], [0, 2]]]]),
                snailfish([[[[5, 2], 5], [8, [3, 7]]], [[5, [7, 5]], [4, 4]]]),
        ]
    }

    private static List<Pair> twoPairsOfSnailfishes() {
        return [new Pair(snailfish([1, 1]), snailfish([2, 2])), new Pair(snailfish([2, 2]), snailfish([1, 1]))]
    }

    private static List<SnailfishNumber> twoSnailfishes() {
        return [snailfish([1, 1]), snailfish([2, 2])]
    }

    private static List<SnailfishNumber> threeSnailfishes() {
        return [snailfish([1, 1]), snailfish([2, 2]), snailfish([3, 3])]
    }

    private static List<Pair> sixSnailfishPairs() {
        return [
                new Pair(snailfish([1, 1]), snailfish([2, 2])),
                new Pair(snailfish([1, 1]), snailfish([3, 3])),
                new Pair(snailfish([2, 2]), snailfish([1, 1])),
                new Pair(snailfish([3, 3]), snailfish([1, 1])),
                new Pair(snailfish([2, 2]), snailfish([3, 3])),
                new Pair(snailfish([3, 3]), snailfish([2, 2])),
        ]
    }
}
