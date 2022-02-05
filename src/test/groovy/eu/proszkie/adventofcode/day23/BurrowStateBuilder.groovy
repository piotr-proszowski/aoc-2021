package eu.proszkie.adventofcode.day23

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import kotlin.Pair

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BurrowStateBuilder {

    Burrow burrow
    List<Tuple2<Coords, AmphipodType>> amphipods

    static BurrowState aBurrowState(@DelegatesTo(BurrowStateBuilder) Closure definition) {
        BurrowStateBuilder builder = new BurrowStateBuilder()
        builder.with(definition)
        return builder.build()
    }

    BurrowState build() {
        return BurrowStateFactory.INSTANCE.from(burrow, amphipods.collect { new Pair(it.v1, it.v2) })
    }
}
