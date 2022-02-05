package eu.proszkie.adventofcode.day23

import spock.lang.Specification

import static eu.proszkie.adventofcode.day23.AmphipodFixture.EXTENDED_AMPHIPODS
import static eu.proszkie.adventofcode.day23.AmphipodFixture.LAST_AMBER_GOING_TO_THE_DESIRED_ROOM
import static eu.proszkie.adventofcode.day23.AmphipodFixture.LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM
import static eu.proszkie.adventofcode.day23.AmphipodFixture.LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM_AND_AMBER_BLOCKING_THE_PLACE
import static eu.proszkie.adventofcode.day23.AmphipodFixture.SAMPLE_2_AMPHIPODS
import static eu.proszkie.adventofcode.day23.AmphipodFixture.SAMPLE_AMPHIPODS
import static eu.proszkie.adventofcode.day23.AmphipodFixture.TWO_DESERT_NEAR_TO_THEIR_ROOM
import static eu.proszkie.adventofcode.day23.AmphipodType.AMBER
import static eu.proszkie.adventofcode.day23.AmphipodType.BRONZE
import static eu.proszkie.adventofcode.day23.AmphipodType.COPPER
import static eu.proszkie.adventofcode.day23.AmphipodType.DESERT
import static eu.proszkie.adventofcode.day23.BurrowBuilder.aBurrow
import static eu.proszkie.adventofcode.day23.BurrowStateBuilder.aBurrowState
import static eu.proszkie.adventofcode.day23.BurrowStateFixture.EXTENDED_BURROW
import static eu.proszkie.adventofcode.day23.BurrowStateFixture.SAMPLE_BURROW

class BurrowSpecification extends Specification {
    def 'should build burrow'() {
        expect:
            aBurrow {
                hallway {
                    x 1..11
                    y 1
                }
                withRoom {
                    x 3
                    y 2..3
                    type AMBER
                }
                withRoom {
                    x 5
                    y 2..3
                    type BRONZE
                }
                withRoom {
                    x 7
                    y 2..3
                    type COPPER
                }
                withRoom {
                    x 9
                    y 2..3
                    type DESERT
                }
            }
    }

    def 'should find next valid states'() {
        given:
            BurrowState burrowState = aBurrowState {
                burrow SAMPLE_BURROW
                amphipods sampleAmphipods
            }
        when:
            List<BurrowState> actual = burrowState.findNextValidStates()
        then:
            actual.any {
                it.minimalCostToReachFinalState == expectedMinimalCost
                it.energyUsed == expectedEnergyUsed
            }
            actual.size() == expectedAmountOfValidStates
        where:
            sampleAmphipods                       || expectedEnergyUsed | expectedMinimalCost | expectedAmountOfValidStates
            SAMPLE_AMPHIPODS                      || 500                | 9910                | 28
            LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM || 30                 | 0                   | 1
            LAST_AMBER_GOING_TO_THE_DESIRED_ROOM  || 10                 | 0                   | 1
    }

    def 'should find the cheapest way'() {
        given:
            BurrowState burrowState = aBurrowState {
                burrow sampleBurrow
                amphipods sampleAmphipods
            }
        when:
            BurrowState actual = burrowState.findCheapestFinalState()
        then:
            actual.energyUsed == expectedEnergyUsed
        where:
            sampleAmphipods                                                    | sampleBurrow    || expectedEnergyUsed
            LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM                              | SAMPLE_BURROW   || 30
            LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM_AND_AMBER_BLOCKING_THE_PLACE | SAMPLE_BURROW   || 38
            TWO_DESERT_NEAR_TO_THEIR_ROOM                                      | SAMPLE_BURROW   || 5000
            SAMPLE_AMPHIPODS                                                   | SAMPLE_BURROW   || 12521
            SAMPLE_2_AMPHIPODS                                                 | SAMPLE_BURROW   || 15160
            EXTENDED_AMPHIPODS                                                 | EXTENDED_BURROW || 46772
    }
}
