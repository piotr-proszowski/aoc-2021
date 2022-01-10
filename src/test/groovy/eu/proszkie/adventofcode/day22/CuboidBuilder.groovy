package eu.proszkie.adventofcode.day22

import kotlin.ranges.LongRange

class CuboidBuilder {

    static Cuboid enabledCuboid(IntRange xRange, IntRange yRange, IntRange zRange) {
        return new EnabledCuboid(
                new LongRange(xRange.getFrom(), xRange.getTo()),
                new LongRange(yRange.getFrom(), yRange.getTo()),
                new LongRange(zRange.getFrom(), zRange.getTo())
        )
    }

    static Cuboid disabledCuboid(IntRange xRange, IntRange yRange, IntRange zRange) {
        return new DisabledCuboid(
                new LongRange(xRange.getFrom(), xRange.getTo()),
                new LongRange(yRange.getFrom(), yRange.getTo()),
                new LongRange(zRange.getFrom(), zRange.getTo())
        )
    }

    static Cuboid cuboid(String command) {
        def (String mode, String ranges) = command.split(" ")
        def (String xRange, String yRange, String zRange) = ranges.split(",")
        if(mode == 'on') {
            return enabledCuboid(Eval.me(xRange.drop(2)), Eval.me(yRange.drop(2)), Eval.me(zRange.drop(2)))
        } else if (mode == 'off') {
            return disabledCuboid(Eval.me(xRange.drop(2)), Eval.me(yRange.drop(2)), Eval.me(zRange.drop(2)))
        }

        throw new IllegalStateException('Illegal mode. Must be on or off')
    }
}
