package eu.proszkie.adventofcode.day19

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static eu.proszkie.adventofcode.day19.PointBuilder.point

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class CubeBuilder {

    Set<Point> points

    static List<Cube> cubesFromString(String input) {
        input.split("---.*---")
                .findAll { !it.isEmpty() }
                .collect { it.split("\n") }
                .collect { it.findAll { !it.isEmpty() } }
                .collect { it.collect { point(it) }.toSet() }
                .collect { new Cube(it, [] as Set) }
    }

    static Cube cube(List<List<Integer>> points) {
        CubeBuilder builder = new CubeBuilder()
        builder.with {
            it.points points.collect { new Point(it[0], it[1], it[2]) }.toSet()
        }
        return builder.build()
    }

    Cube build() {
        return new Cube(points, [] as Set)
    }

}
