package eu.proszkie.adventofcode.day23

import eu.proszkie.adventofcode.WithResourceReadingAbility

class BurrowBuilder implements WithResourceReadingAbility {

    static Burrow sampleNotFinalBurrow() {
        return aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###B#C#B#D###|
        |  #A#D#C#A#  |
        |  #########  |
       """))
    }

    static Burrow sampleFinalBurrow() {
        return aBurrow(withoutMargins("""
        |#############|
        |#...........#|
        |###A#B#C#D###|
        |  #A#B#C#D#  |
        |  #########  |
       """))
    }

    static Burrow aBurrowFromFile(String path) {
        return aBurrow(new BurrowBuilder().readLines("/advent-of-code/day23/$path")
                .collect { withoutMargins(it) }
                .join('\n'))
    }

    static String withoutMargins(String burrow) {
        return burrow.stripMargin("|").replaceAll("\\|", "")
    }

    static Burrow aBurrow(String burrow) {
        return BurrowFactory.INSTANCE.fromString(burrow)
    }

    static BurrowState aBurrowState(Burrow burrow, Map props = [:]) {
        return new BurrowState(burrow, props.energyUsed ?: 0, [props.previousChange] ?: [])
    }

}
