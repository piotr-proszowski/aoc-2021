package eu.proszkie.adventofcode.day23

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BurrowBuilder {
    Hallway hallway
    List<Room> rooms = []

    static Burrow aBurrow(@DelegatesTo(BurrowBuilder) Closure definition) {
        BurrowBuilder builder = new BurrowBuilder()
        builder.with(definition)
        return builder.build()
    }

    void hallway(@DelegatesTo(HallwayBuilder) Closure definition) {
        HallwayBuilder builder = new HallwayBuilder()
        builder.with(definition)
        this.hallway = builder.build()
    }

    void withRoom(@DelegatesTo(RoomBuilder) Closure definition) {
        RoomBuilder builder = new RoomBuilder()
        builder.with(definition)
        rooms += builder.build()
    }

    Burrow build() {
        return new Burrow(hallway, rooms)
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class HallwayBuilder {

    int y
    IntRange x

    Hallway build() {
        List<Coords> coords = x.collect { new Coords(it, y) }
        return new Hallway(coords)
    }

}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class RoomBuilder {
    int x
    IntRange y
    AmphipodType type

    Room build() {
        Set<Coords> coords = y.collect { new Coords(x, it) }.toSet()
        return new Room(coords, type)
    }
}
