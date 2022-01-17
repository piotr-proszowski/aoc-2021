package eu.proszkie.adventofcode.day23

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

class BurrowBuilder {
    static BurrowState aBurrow() {
        return burrow("""
        |#############
        |#...........#
        |###B#C#B#D###
        |  #A#D#C#A#
        |  #########
        """.stripMargin('|'))
    }

    static BurrowState burrow(String burrowAsString) {
        def burrowAsMap = burrowAsString.split("\n")
                .findAll { !it.isBlank() }
                .indexed()
                .collect { y, line ->
                    line.collect().indexed().collectEntries { x, token ->
                        [new Coords(x, y), ElementInBurrow.@Companion.fromToken(token as Character)]
                    }
                }.inject { prev, next -> prev + next }
                .findAll { it.value != null }
        return new BurrowState(new Burrow(burrowAsMap as Map<Coords, ElementInBurrow>, null), null, 0L)
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class CoordsBuilder {

    int x = 0
    int y = 0

    static Coords coords(@DelegatesTo(CoordsBuilder) Closure definition) {
        CoordsBuilder builder = new CoordsBuilder()
        builder.with(definition)
        return builder.build()
    }

    Coords build() {
        return new Coords(x, y)
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BurrowChangeBuilder {
    Coords from
    Coords to

    static BurrowChange change(@DelegatesTo(BurrowChangeBuilder) Closure definition) {
        BurrowChangeBuilder builder = new BurrowChangeBuilder()
        builder.with(definition)
        return builder.build()
    }

    BurrowChange build() {
        return new BurrowChange(from, to)
    }
}