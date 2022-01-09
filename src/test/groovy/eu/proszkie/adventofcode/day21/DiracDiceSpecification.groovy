package eu.proszkie.adventofcode.day21

import spock.lang.Specification

import static eu.proszkie.adventofcode.day21.GameSnapshotBuilder.gameSnapshot

class DiracDiceSpecification extends Specification {
    def 'dice should be deterministic'() {
        given:
        DeterministicDiceState dice = new DeterministicDiceState(1000, 0)

        when:
        DeterministicDiceState next = (1..numOfThrows).inject(dice) { acc, _ -> acc.next() }

        then:
        next.currentValue == expectedValue
        next.countOfThrows == expectedNumOfThrows

        where:
        numOfThrows || expectedValue | expectedNumOfThrows
        1           || 1             | 1
        5           || 5             | 5
        1000        || 1000          | 1000
        1001        || 1             | 1001
    }

    def 'should build snapshot of game'() {
        expect:
        GameSnapshot gameSnapshot = gameSnapshot {
            playersPositions([1: 1, 2: 3])
        }
        gameSnapshot.worstPlayerScore() == 0
        gameSnapshot.amountOfDiceThrows() == 0
    }

    def 'should move from one game snapshot to next one'() {
        given:
        GameSnapshot gameSnapshot = gameSnapshot {
            playersPositions([1: 1, 2: 3])
        }

        when:
        GameSnapshot nextSnapshot = gameSnapshot.next().next()

        then:
        nextSnapshot.worstPlayerScore() == 7
        nextSnapshot.amountOfDiceThrows() == 6
    }

    def 'should simulate game until first player wins'() {
        given:
        GameSnapshot gameSnapshot = gameSnapshot {
            it.playersPositions(playersPositions)
        }

        when:
        GameSnapshot endOfGame = gameSnapshot.rewindToMomentWhenAnyPlayerReachGivenAmountOfPoints(1000)

        then:
        endOfGame.amountOfDiceThrows() == expectedAmountOfDiceThrows
        endOfGame.worstPlayerScore() == expectedWorstPlayerScore
        endOfGame.amountOfDiceThrows() * endOfGame.worstPlayerScore() == expectedProduct

        where:
        playersPositions || expectedAmountOfDiceThrows | expectedWorstPlayerScore | expectedProduct
        [1: 4, 2: 8]     || 993                        | 745                      | 739785
        [1: 8, 2: 6]     || 747                        | 674                      | 503478

    }
}
