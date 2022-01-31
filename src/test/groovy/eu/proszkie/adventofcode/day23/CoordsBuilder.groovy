package eu.proszkie.adventofcode.day23

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class CoordsBuilder {
    int x
    int y

    static Coords coords(@DelegatesTo(CoordsBuilder) Closure definition) {
        CoordsBuilder builder = new CoordsBuilder()
        builder.with(definition)
        return builder.build()
    }

    Coords build() {
        return new Coords(x, y)
    }
}