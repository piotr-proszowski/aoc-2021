package eu.proszkie.adventofcode.day23

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

import static eu.proszkie.adventofcode.day23.BurrowBuilder.*
import static eu.proszkie.adventofcode.day23.BurrowStateChangeBuilder.*

class BurrowSpecification extends Specification implements WithResourceReadingAbility {

    def 'should build burrow'() {
        when:
            Burrow burrow = aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###B#C#B#D###|
        |  #A#D#C#A#  |
        |  #########  |
       """))

        then:
            burrow.getRooms().size() == 4
            burrow.getRooms().every { it.allCoords.size() == 2 }
    }

    def 'should be able to say if burrow is final'() {
        when:
            def actual = burrow.isFinal()

        then:
            actual == expected

        where:
            burrow                 || expected
            sampleNotFinalBurrow() || false
            sampleFinalBurrow()    || true
    }

    def 'should find all next valid states'() {
        given:
            def burrow = startingBurrow
            def burrowState = aBurrowState(burrow, [previousChange: previousChange])
            def expected = expectedValidStates.collect { aBurrowFromFile(it).toString() }

        when:
            List<String> actual = burrowState.findNextValidStates().collect { it.burrow.toString() }

        then:
            assert actual.containsAll(expected) && expected.containsAll(actual), printErrorMessage(actual, expected)

        where:
            startingBurrow              | previousChange                                          || expectedValidStates
            aBurrowFromFile("burrow_1") | null                                                    || ["burrow_1_1", "burrow_1_2", "burrow_1_3", "burrow_1_4"]
            aBurrowFromFile("burrow_2") | change { from { x 3 y 2 } to { x 3 y 1 } element('B') } || ["burrow_2_1", "burrow_2_2"]
    }

    def 'should fail to apply change that breaks any of specified rules'() {
        given:
            Burrow burrow = aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###B#C#B#D###|
        |  #A#D#C#A#  |
        |  #########  |
       """))
            def burrowState = aBurrowState(burrow)

        when:
            def actual = burrowState.applyChanges(changesToApply) as BurrowStateChangeFailed

        then:
            actual.burrowChangeFailed == expectedFailure

        where:
            changesToApply                                                 | expectedFailure
            triesToStopOnSpaceImmediatelyOutsideAnyRoom()                  | TriedToStopInFrontOfTheRoom.INSTANCE
            triesToMoveIntoRoomInWhichThereIsOtherTypeOfAmphipod()         | TriedToStepIntoRoomWhichIsNotDestinationRoom.INSTANCE
            triesToComebackToTheSpaceInWhichAlreadyItWasDuringSingleWalk() | TriedToComebackToTheSpaceInWhichAlreadyItWasDuringSingleWalk.INSTANCE
            triesToStopAndThenGoNotDirectlyToTheRoom()                     | TriedToStopAndThenGoNotDirectlyToTheRoom.INSTANCE
            triesToStopInsideTheRoom()                                     | TriedToStopInsideTheRoom.INSTANCE
    }

    def 'should not be able to stop inside the room'() {

        given:
            Burrow burrow = aBurrow(withoutMargins("""
        |#############|
        |#D....D...CA#|
        |###.#B#.#.###|
        |  #A#B#C#.#  |
        |  #########  |
        """))
            def burrowState = aBurrowState(burrow)
            def changes = [
                    change { from { x 3 y 3 } to { x 3 y 2 } element('A') },
                    change { from { x 7 y 3 } to { x 7 y 2 } element('C') },
            ]

        when:
            def actual = burrowState.applyChanges(changes) as BurrowStateChangeFailed

        then:
            actual.burrowChangeFailed == TriedToStopInsideTheRoom.INSTANCE
    }

    def 'should be able to reach final state'() {
        given:
            Burrow burrow = aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###B#C#B#D###|
        |  #A#D#C#A#  |
        |  #########  |
       """))
            def burrowState = aBurrowState(burrow)
            def changes = [
                    change { from { x 3 y 2 } to { x 3 y 1 } element('B') },
                    change { from { x 3 y 1 } to { x 2 y 1 } element('B') },
                    change { from { x 9 y 2 } to { x 9 y 1 } element('D') },
                    change { from { x 9 y 1 } to { x 10 y 1 } element('D') },
                    change { from { x 9 y 3 } to { x 9 y 2 } element('A') },
                    change { from { x 9 y 2 } to { x 9 y 1 } element('A') },
                    change { from { x 9 y 1 } to { x 8 y 1 } element('A') },
                    change { from { x 8 y 1 } to { x 7 y 1 } element('A') },
                    change { from { x 7 y 1 } to { x 6 y 1 } element('A') },
                    change { from { x 6 y 1 } to { x 5 y 1 } element('A') },
                    change { from { x 5 y 1 } to { x 4 y 1 } element('A') },
                    change { from { x 4 y 1 } to { x 3 y 1 } element('A') },
                    change { from { x 3 y 1 } to { x 3 y 2 } element('A') },
                    change { from { x 10 y 1 } to { x 9 y 1 } element('D') },
                    change { from { x 9 y 1 } to { x 9 y 2 } element('D') },
                    change { from { x 9 y 2 } to { x 9 y 3 } element('D') },
                    change { from { x 7 y 2 } to { x 7 y 1 } element('B') },
                    change { from { x 7 y 1 } to { x 8 y 1 } element('B') },
                    change { from { x 8 y 1 } to { x 9 y 1 } element('B') },
                    change { from { x 9 y 1 } to { x 10 y 1 } element('B') },
                    change { from { x 5 y 2 } to { x 5 y 1 } element('C') },
                    change { from { x 5 y 1 } to { x 6 y 1 } element('C') },
                    change { from { x 6 y 1 } to { x 7 y 1 } element('C') },
                    change { from { x 7 y 1 } to { x 7 y 2 } element('C') },
                    change { from { x 5 y 3 } to { x 5 y 2 } element('D') },
                    change { from { x 5 y 2 } to { x 5 y 1 } element('D') },
                    change { from { x 5 y 1 } to { x 6 y 1 } element('D') },
                    change { from { x 6 y 1 } to { x 7 y 1 } element('D') },
                    change { from { x 7 y 1 } to { x 8 y 1 } element('D') },
                    change { from { x 8 y 1 } to { x 9 y 1 } element('D') },
                    change { from { x 9 y 1 } to { x 9 y 2 } element('D') },
                    change { from { x 10 y 1 } to { x 9 y 1 } element('B') },
                    change { from { x 9 y 1 } to { x 8 y 1 } element('B') },
                    change { from { x 8 y 1 } to { x 7 y 1 } element('B') },
                    change { from { x 7 y 1 } to { x 6 y 1 } element('B') },
                    change { from { x 6 y 1 } to { x 5 y 1 } element('B') },
                    change { from { x 5 y 1 } to { x 5 y 2 } element('B') },
                    change { from { x 5 y 2 } to { x 5 y 3 } element('B') },
                    change { from { x 2 y 1 } to { x 3 y 1 } element('B') },
                    change { from { x 3 y 1 } to { x 4 y 1 } element('B') },
                    change { from { x 4 y 1 } to { x 5 y 1 } element('B') },
                    change { from { x 5 y 1 } to { x 5 y 2 } element('B') },
            ]

        when:
            def actual = burrowState.applyChanges(changes) as BurrowStateChangeSucceeded
        then:
            actual.burrowState.isFinal()
    }

    def 'should calculate minimal steps to get into final rooms'() {
        given:
            Burrow burrow = aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###B#C#B#D###|
        |  #A#D#C#A#  |
        |  #########  |
       """))
            def burrowState = aBurrowState(burrow)
        when:
            def actual = burrowState.minimalCostToFinalState
        then:
            actual == (0 * 1 + 6 * 1) + (2 * 10 + 2 * 10) + (2 * 100 + 0 * 100) + (4 * 1000 + 0 * 1000)
    }

    def 'should find cheapest cost'() {
        given:
            def burrow = aBurrowFromFile(burrowFile)
            def burrowState = aBurrowState(burrow)
        when:
            def actual = CheapestSolutionFinder.INSTANCE.findCheapestSolutionCost(burrowState)
        then:
            actual == expected
        where:
            burrowFile || expected
            'burrow_1' || 12521
            'burrow_3' || 15160
//            'burrow_4' || 15160
    }

    private static GString printErrorMessage(List<String> actual, List<String> expected) {
        return """
                Not expected burrows: [\n${(actual - expected).collect { it }.join("\n\n")}\n]
                There are no burrows that were expected to be: [\n${(expected - actual).collect { it }.join("\n\n")}\n]
        """
    }
}
