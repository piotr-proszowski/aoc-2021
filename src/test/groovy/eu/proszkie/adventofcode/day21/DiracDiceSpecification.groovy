package eu.proszkie.adventofcode.day21

import spock.lang.Specification

import static eu.proszkie.adventofcode.day21.GameSnapshotBuilder.gameSnapshot

class DiracDiceSpecification extends Specification {
    def 'dice should be deterministic'() {
        given:
        DiceState dice = new DeterministicDiceState(1000, 0)

        when:
        DiceState next = (1..numOfThrows).inject(dice) { acc, _ -> acc.next().first() }

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
        GameSnapshot nextSnapshot = gameSnapshot.next().first().next().first()

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
        GameSnapshot endOfGame = gameSnapshot.rewindToMomentWhenAnyPlayerReachGivenAmountOfPoints(1000).entrySet().collect { it.key }.first()

        then:
        endOfGame.amountOfDiceThrows() == expectedAmountOfDiceThrows
        endOfGame.worstPlayerScore() == expectedWorstPlayerScore
        endOfGame.amountOfDiceThrows() * endOfGame.worstPlayerScore() == expectedProduct

        where:
        playersPositions || expectedAmountOfDiceThrows | expectedWorstPlayerScore | expectedProduct
        [1: 4, 2: 8]     || 993                        | 745                      | 739785
        [1: 8, 2: 6]     || 747                        | 674                      | 503478
    }

    def 'dirac dice should produce three results'() {
        given:
        DiceState dice = new DiracDice(1, 0)

        when:
        List<DiceState> results = dice.next()

        then:
        results.contains(new DiracDice(2, 1))
        results.contains(new DiracDice(3, 1))
        results.contains(new DiracDice(1, 1))
        results.size() == 3
    }

    def 'should move from one game snapshot to multiple possibilities when playing with dirac dice'() {
        given:
        GameSnapshot gameSnapshot = gameSnapshot {
            playersPositions([1: 1, 2: 3])
            dice(new DiracDice(1, 0))
        }

        when:
        List<GameSnapshot> nextSnapshots = gameSnapshot.next()

        then:
        nextSnapshots.size() == 27
    }

    def 'should simulate game with dirac dice until first player wins'() {
        given:
        GameSnapshot gameSnapshot = gameSnapshot {
            it.playersPositions(playersPositions)
            dice(new DiracDice(1, 0))
        }

        when:
        Map<GameSnapshot, Long> snapshots = gameSnapshot.rewindToMomentWhenAnyPlayerReachGivenAmountOfPoints(21)

        then:
        def results = snapshots.collect {
            new Tuple2<>(it.key.winnerForThreshold(21), it.value)
        }.inject([:] as Map<PlayerId, Long>) { acc, next ->
            acc + [(next[0]): next[1] + (acc[next[0]] ?: 0L)]
        }
        results[winner] == expectedAmountOfWinnedGamesByWinner

        where:
        playersPositions || winner          | expectedAmountOfWinnedGamesByWinner
        [1: 4, 2: 8]     || new PlayerId(1) | 444356092776315
        [1: 8, 2: 6]     || new PlayerId(1) | 716241959649754
    }
}