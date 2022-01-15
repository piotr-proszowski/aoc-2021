package eu.proszkie.adventofcode.day22

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

import static eu.proszkie.adventofcode.day22.CuboidBuilder.cuboid
import static eu.proszkie.adventofcode.day22.CuboidWithActionBuilder.*

class CuboidSpecification extends Specification implements WithResourceReadingAbility {

    def 'cuboid should return proper amount of cubes'() {
        expect:
        cuboid { x 1..2 y 1..2 z 1..2 }.amountOfCubes() == 8
        cuboid { x 0..0 y 0..0 z 0..0 }.amountOfCubes() == 1
    }

    def 'cuboid should be able to determine if intersects with another one'() {
        expect:
        cuboid { x 1..2 y 1..2 z 1..2 }.intersection(cuboid { x 3..3 y 5..9 z 14..21 }) == null
        cuboid { x 5..8 y 5..8 z 5..8 }.intersection(cuboid { x 7..9 y 7..9 z 7..9 }) == cuboid { x 7..8 y 7..8 z 7..8 }
    }

    def 'should build cuboid from string'() {
        when:
        def actual = cuboidWithAction(command)

        then:
        actual == expected

        where:
        command                                                || expected
        'on x=10..12,y=10..12,z=10..12'                        || firstCase()
        'off x=-54112..-39298,y=-85059..-49293,z=-27449..7877' || secondCase()
    }

    def 'should subtract two cuboids'() {
        expect:
        (cuboid { x 5..8 y 5..8 z 5..8 } - cuboid { x 7..8 y 7..8 z 7..8 }).amountOfCubes() == 56
        (cuboid { x 5..8 y 5..8 z 5..8 } - cuboid { x 1..2 y 3..4 z 9..10 }).amountOfCubes() == 64
        cuboid { x 5..8 y 5..8 z 5..8 } - cuboid { x 5..8 y 5..8 z 5..8 } == null
        (cuboid { x 10..12 y 10..12 z 10..12 } - cuboid { x 11..13 y 11..13 z 11..13 }).amountOfCubes() == 19

    }

    def 'should properly calculate amount of enabled cubes after performed actions in initialization phase'() {
        given:
        List<CuboidWithAction> cuboidsWithActions = readLines(input).collect { cuboidWithAction(it) }
        Reactor reactor = new Reactor()

        when:
        Reactor modifiedReactor = reactor.withActionsPerformed(cuboidsWithActions)

        then:
        Long actual = modifiedReactor.amountOfEnabledCubesDuringInitialization()
        actual == expectedAmountOfEnabledCubes

        where:
        input                          || expectedAmountOfEnabledCubes
        '/advent-of-code/day22/input1' || 39
        '/advent-of-code/day22/input2' || 590784
        '/advent-of-code/day22/input3' || 533863
        '/advent-of-code/day22/input4' || 474140
    }

    def 'should properly calculate amount of enabled cubes after performed actions'() {
        given:
        Reactor reactor = new Reactor()
        List<CuboidWithAction> cuboidsWithActions = readLines(input).collect { cuboidWithAction(it) }

        when:
        Reactor modifiedReactor = reactor.withActionsPerformed(cuboidsWithActions)

        then:
        Long actual = modifiedReactor.amountOfEnabledCubes()
        actual == expectedAmountOfEnabledCubes

        where:
        input                          || expectedAmountOfEnabledCubes
        '/advent-of-code/day22/input4' || 2758514936282235
        '/advent-of-code/day22/input3' || 1261885414840992
    }

    private static CuboidWithAction firstCase() {
        cuboidWithAction {
            it.action ENABLE
            it.cuboid cuboid {
                x 10..12
                y 10..12
                z 10..12
            }
        }
    }

    private static CuboidWithAction secondCase() {
        cuboidWithAction {
            it.action DISABLE
            it.cuboid cuboid {
                x(-54112..-39298)
                y(-85059..-49293)
                z(-27449..7877)
            }
        }
    }
}
