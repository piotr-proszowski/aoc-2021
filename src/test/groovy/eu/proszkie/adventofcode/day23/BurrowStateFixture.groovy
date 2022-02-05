package eu.proszkie.adventofcode.day23

import static eu.proszkie.adventofcode.day23.AmphipodType.AMBER
import static eu.proszkie.adventofcode.day23.AmphipodType.BRONZE
import static eu.proszkie.adventofcode.day23.AmphipodType.COPPER
import static eu.proszkie.adventofcode.day23.AmphipodType.DESERT
import static eu.proszkie.adventofcode.day23.BurrowBuilder.aBurrow

class BurrowStateFixture {

    /*
        #############
        #...........#
        ###.#.#.#.###
          #.#.#.#.#
          #########
    */

    static Burrow SAMPLE_BURROW = aBurrow {
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

    /*
        #############
        #...........#
        ###.#.#.#.###
          #.#.#.#.#
          #.#.#.#.#
          #.#.#.#.#
          #########
    */

    static Burrow EXTENDED_BURROW = aBurrow {
        hallway {
            x 1..11
            y 1
        }
        withRoom {
            x 3
            y 2..5
            type AMBER
        }
        withRoom {
            x 5
            y 2..5
            type BRONZE
        }
        withRoom {
            x 7
            y 2..5
            type COPPER
        }
        withRoom {
            x 9
            y 2..5
            type DESERT
        }
    }
}
