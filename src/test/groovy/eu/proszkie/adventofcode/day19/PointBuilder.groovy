package eu.proszkie.adventofcode.day19

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static java.lang.Integer.parseInt

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class PointBuilder {
    int x
    int y
    int z

    static Point point(String input) {
        def xyz = input.trim().split(",").collect { parseInt(it) }
        return point(xyz[0], xyz[1], xyz[2])
    }

    static Point point(int x, int y, int z) {
        return new PointBuilder().with {
            it.x x
            it.y y
            it.z z
        }.build()
    }

    Point build() {
        return new Point(x, y, z)
    }
}
