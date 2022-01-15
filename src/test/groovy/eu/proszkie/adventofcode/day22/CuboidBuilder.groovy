package eu.proszkie.adventofcode.day22

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static eu.proszkie.adventofcode.day22.CuboidBuilder.cuboid

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class CuboidBuilder {

    IntRange x = 1..2
    IntRange y = 1..2
    IntRange z = 1..2

    static Cuboid cuboid(@DelegatesTo(CuboidBuilder) Closure definition = {}) {
        CuboidBuilder builder = new CuboidBuilder()
        builder.with(definition)
        return builder.build()
    }

    Cuboid build() {
        return new Cuboid(x.toSet(), y.toSet(), z.toSet(), [])
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class CuboidWithActionBuilder {

    static Action DISABLE = Disable.INSTANCE
    static Action ENABLE = Enable.INSTANCE

    Cuboid cuboid = cuboid { x 1..2 y 1..2 z 1..2 }
    Action action = DISABLE

    static CuboidWithAction cuboidWithAction(@DelegatesTo(CuboidWithActionBuilder) Closure definition = {}) {
        CuboidWithActionBuilder builder = new CuboidWithActionBuilder()
        builder.with(definition)
        return builder.build()
    }

    static CuboidWithAction cuboidWithAction(String command) {
        def (String actionDefinition, String cuboidDefinition) = command.split(' ')
        Action action = actionDefinition == 'on' ? ENABLE : DISABLE
        def (IntRange x, IntRange y, IntRange z) = cuboidDefinition.split(',')
                .collect { it.drop(2) }
                .collect { Eval.me(it) }
        return cuboidWithAction {
            it.action action
            it.cuboid cuboid {
                it.x x
                it.y y
                it.z z
            }
        }
    }

    CuboidWithAction build() {
        return new CuboidWithAction(cuboid, action)
    }
}
