package eu.proszkie.adventofcode.day23

class AmphipodFixture {


    /**
     #############
     #...........#
     ###B#C#B#D###
     #A#D#C#A#
     #########
     */
    static List<Tuple2<Coords, AmphipodType>> SAMPLE_AMPHIPODS = [
            new Tuple2(new Coords(3, 2), AmphipodType.BRONZE),
            new Tuple2(new Coords(3, 3), AmphipodType.AMBER),
            new Tuple2(new Coords(5, 2), AmphipodType.COPPER),
            new Tuple2(new Coords(5, 3), AmphipodType.DESERT),
            new Tuple2(new Coords(7, 2), AmphipodType.BRONZE),
            new Tuple2(new Coords(7, 3), AmphipodType.COPPER),
            new Tuple2(new Coords(9, 2), AmphipodType.DESERT),
            new Tuple2(new Coords(9, 3), AmphipodType.AMBER),
    ]

    /*
        #############
        #...........#
        ###D#C#A#B###
          #B#C#D#A#
          #########
     */

    static List<Tuple2<Coords, AmphipodType>> SAMPLE_2_AMPHIPODS = [
            new Tuple2(new Coords(3, 2), AmphipodType.DESERT),
            new Tuple2(new Coords(3, 3), AmphipodType.BRONZE),
            new Tuple2(new Coords(5, 2), AmphipodType.COPPER),
            new Tuple2(new Coords(5, 3), AmphipodType.COPPER),
            new Tuple2(new Coords(7, 2), AmphipodType.AMBER),
            new Tuple2(new Coords(7, 3), AmphipodType.DESERT),
            new Tuple2(new Coords(9, 2), AmphipodType.BRONZE),
            new Tuple2(new Coords(9, 3), AmphipodType.AMBER),
    ]
    /*
        #############
        #...........#
        ###D#C#A#B###
          #D#C#B#A#
          #D#B#A#C#
          #B#C#D#A#
          #########
     */

    static List<Tuple2<Coords, AmphipodType>> EXTENDED_AMPHIPODS = [
            new Tuple2(new Coords(3, 2), AmphipodType.DESERT),
            new Tuple2(new Coords(3, 3), AmphipodType.DESERT),
            new Tuple2(new Coords(3, 4), AmphipodType.DESERT),
            new Tuple2(new Coords(3, 5), AmphipodType.BRONZE),
            new Tuple2(new Coords(5, 2), AmphipodType.COPPER),
            new Tuple2(new Coords(5, 3), AmphipodType.COPPER),
            new Tuple2(new Coords(5, 4), AmphipodType.BRONZE),
            new Tuple2(new Coords(5, 5), AmphipodType.COPPER),
            new Tuple2(new Coords(7, 2), AmphipodType.AMBER),
            new Tuple2(new Coords(7, 3), AmphipodType.BRONZE),
            new Tuple2(new Coords(7, 4), AmphipodType.AMBER),
            new Tuple2(new Coords(7, 5), AmphipodType.DESERT),
            new Tuple2(new Coords(9, 2), AmphipodType.BRONZE),
            new Tuple2(new Coords(9, 3), AmphipodType.AMBER),
            new Tuple2(new Coords(9, 4), AmphipodType.COPPER),
            new Tuple2(new Coords(9, 5), AmphipodType.AMBER),
    ]

    static List<Tuple2<Coords, AmphipodType>> LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM = [
            new Tuple2(new Coords(4, 1), AmphipodType.BRONZE),
    ]

    static List<Tuple2<Coords, AmphipodType>> LAST_BRONZE_GOING_TO_THE_DESIRED_ROOM_AND_AMBER_BLOCKING_THE_PLACE = [
            new Tuple2(new Coords(4, 1), AmphipodType.BRONZE),
            new Tuple2(new Coords(5, 3), AmphipodType.AMBER),
    ]

    static List<Tuple2<Coords, AmphipodType>> TWO_DESERT_NEAR_TO_THEIR_ROOM = [
            new Tuple2(new Coords(10, 1), AmphipodType.DESERT),
            new Tuple2(new Coords(8, 1), AmphipodType.DESERT),
    ]

    static List<Tuple2<Coords, AmphipodType>> LAST_AMBER_GOING_TO_THE_DESIRED_ROOM = [
            new Tuple2(new Coords(11, 1), AmphipodType.AMBER),
    ]
}
