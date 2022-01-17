package eu.proszkie.adventofcode.day23

import spock.lang.Specification

import static eu.proszkie.adventofcode.day23.BurrowAssertions.assertThat
import static eu.proszkie.adventofcode.day23.BurrowBuilder.aBurrow
import static eu.proszkie.adventofcode.day23.BurrowBuilder.burrow
import static eu.proszkie.adventofcode.day23.BurrowChangeBuilder.change
import static eu.proszkie.adventofcode.day23.CoordsBuilder.coords

class BurrowSpecification extends Specification {
    def 'should build burrow'() {
        expect:
        burrow("""
        |#############
        |#...........#
        |###B#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|')) == aBurrow()
    }

    def 'should find possible changes'() {
        given:
        def burrow = aBurrow()
        def expected = [
                change { from(coords { x 3 y 2 }) to(coords { x 3 y 1 }) },
                change { from(coords { x 5 y 2 }) to(coords { x 5 y 1 }) },
                change { from(coords { x 7 y 2 }) to(coords { x 7 y 1 }) },
                change { from(coords { x 9 y 2 }) to(coords { x 9 y 1 }) }
        ]

        when:
        def actual = burrow.findPossibleChanges()

        then:
        actual == expected
    }

    def 'should apply change successfully'() {
        given:
        def burrowChange = change { from(coords { x 3 y 2 }) to(coords { x 3 y 1 }) }
        def expected = burrow("""
        |#############
        |#..B........#
        |###.#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))
        def burrow = aBurrow()

        when:
        def actual = burrow.applyChange(burrowChange)

        then:
        assertThat(actual).hasSameBurrowIgnoringVisitedPlaces(expected)
    }

    def 'should fail when amphipod stops immediately outside any room and other amphipod is moved'() {
        given:
        def burrowChange = change { from(coords { x 5 y 2 }) to(coords { x 5 y 1 }) }
        def burrow = burrow("""
        |#############
        |#..B........#
        |###.#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = burrow.applyChange(burrowChange)

        then:
        assert actual instanceof AmphipodStopsImmediatelyOutsideTheRoom
    }

    def 'should fail when amphipod goes again to previously visited room'() {
        given:
        def firstChange = change { from(coords { x 3 y 2 }) to(coords { x 3 y 1 }) }
        def secondChange = change { from(coords { x 3 y 1 }) to(coords { x 3 y 2 }) }
        def burrow = burrow("""
        |#############
        |#...........#
        |###B#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = (burrow.applyChange(firstChange) as Success).changedBurrow.applyChange(secondChange)

        then:
        assert actual instanceof AmphipodAlreadyVisitedThisRoom
    }

    def 'should fail when amphipod tries to step in into room where other type of amphipod is'() {
        given:
        def change = change { from(coords { x 3 y 1 }) to(coords { x 3 y 2 }) }
        def burrow = burrow("""
        |#############
        |#..B........#
        |###.#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = burrow.applyChange(change)

        then:
        assert actual instanceof AmphipodTriesToEnterRoomWhereAmphipodOfOtherTypeIs
    }

    def 'should succeed when amphipod tries to step in into room where the same type of amphipod is'() {
        given:
        def change = change { from(coords { x 3 y 1 }) to(coords { x 3 y 2 }) }
        def burrow = burrow("""
        |#############
        |#..B........#
        |###.#C#B#D###
        |  #B#D#C#A#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = burrow.applyChange(change)

        then:
        assert actual instanceof Success
    }

    def 'should be able to determine final position'() {
        expect:
        burrow("""
        |#############
        |#...........#
        |###A#B#C#D###
        |  #A#B#C#D#
        |  #########
        """.stripMargin('|')).isFinal()

        and:
        !burrow("""
        |#############
        |#...........#
        |###B#A#D#C###
        |  #A#B#C#D#
        |  #########
        """.stripMargin('|')).isFinal()
    }

    def 'should calculate energy properly'() {
        given:
        def change = change { from(coords { x 3 y 1 }) to(coords { x 4 y 1 }) }
        def burrow = burrow("""
        |#############
        |#..B........#
        |###.#A#D#C###
        |  #A#B#C#D#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = burrow.applyChange(change) as Success

        then:
        actual.changedBurrow.energyUsed == 10L
    }

    def 'should fail when trying to move back to the same place as in previous change'() {
        given:
        def firstChange = change { from(coords { x 3 y 1 }) to(coords { x 4 y 1 }) }
        def secondChange = change { from(coords { x 4 y 1 }) to(coords { x 3 y 1 }) }
        def burrow = burrow("""
        |#############
        |#..B........#
        |###.#A#D#C###
        |  #A#B#C#D#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = burrow.applyChanges([firstChange, secondChange])

        then:
        actual instanceof GoesBackToTheSamePlaceAsInPreviousMove
    }

    def 'should transit into final state'() {
        given:
        def changesLeadingToFinalState = [
                change { from(coords { x 3 y 2 }) to(coords { x 3 y 1 }) },
                change { from(coords { x 3 y 1 }) to(coords { x 2 y 1 }) },
                change { from(coords { x 5 y 2 }) to(coords { x 5 y 1 }) },
                change { from(coords { x 5 y 1 }) to(coords { x 6 y 1 }) },
                change { from(coords { x 3 y 3 }) to(coords { x 3 y 2 }) },
                change { from(coords { x 3 y 2 }) to(coords { x 3 y 1 }) },
                change { from(coords { x 3 y 1 }) to(coords { x 4 y 1 }) },
                change { from(coords { x 4 y 1 }) to(coords { x 5 y 1 }) },
                change { from(coords { x 5 y 1 }) to(coords { x 5 y 2 }) },
                change { from(coords { x 6 y 1 }) to(coords { x 5 y 1 }) },
                change { from(coords { x 5 y 1 }) to(coords { x 4 y 1 }) },
                change { from(coords { x 4 y 1 }) to(coords { x 3 y 1 }) },
                change { from(coords { x 3 y 1 }) to(coords { x 3 y 2 }) },
                change { from(coords { x 3 y 2 }) to(coords { x 3 y 3 }) },
                change { from(coords { x 2 y 1 }) to(coords { x 3 y 1 }) },
                change { from(coords { x 3 y 1 }) to(coords { x 3 y 2 }) },
        ]
        def burrow = burrow("""
        |##########
        |#........#
        |###B#B####
        |  #A#A##
        |  ######
        """.stripMargin('|'))

        when:
        def finalBurrow = burrow.applyChanges(changesLeadingToFinalState) as Success

        then:
        finalBurrow.changedBurrow.isFinal()
    }

    def 'should find optimal cost of going to correct configuration'() {
        given:
        def burrow = burrow("""
        |##########
        |#........#
        |###B#B####
        |  #A#A##
        |  ######
        """.stripMargin('|'))

        when:
        def actual = BurrowOptimalWayFinder.INSTANCE.findTheLowestCostOfMovingToFinalConfiguration(burrow)

        then:
        actual == 97
    }

    def 'should find optimal cost of going to correct configuration for input'() {
        given:
        def burrow = burrow("""
        |#############
        |#...........#
        |###B#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))

        when:
        def actual = BurrowOptimalWayFinder.INSTANCE.findTheLowestCostOfMovingToFinalConfiguration(burrow)

        then:
        actual == 12521
    }
}
