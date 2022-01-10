package eu.proszkie.adventofcode.day22

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

import static eu.proszkie.adventofcode.day22.CuboidBuilder.*

class ReactorSpecification extends Specification implements WithResourceReadingAbility {

    def 'should have all cube set to off'() {
        when:
        Reactor reactor = new Reactor()

        then:
        reactor.amountOfTurnedOnCubes() == 0
    }

    def 'should build cube'() {
        when:
        Cuboid cuboid = enabledCuboid(0..3, 0..3, 0..3)

        then:
        cuboid.cubes.size() == 64
    }

    def 'should enable cubes with cuboid'() {
        given:
        Reactor reactor = new Reactor()
        Cuboid cuboid = enabledCuboid(0..3, 0..3, 0..3)

        when:
        Reactor modified = reactor.withCuboid(cuboid)

        then:
        modified.amountOfTurnedOnCubes() == 64
    }

    def 'should disable cubes with cuboid'() {
        given:
        Reactor reactor = new Reactor()
        Cuboid enabledCuboid = enabledCuboid(0..3, 0..3, 0..3)
        Cuboid disabledCuboid = disabledCuboid(2..3, 2..3, 2..3)

        when:
        Reactor modified = reactor.withCuboid(enabledCuboid).withCuboid(disabledCuboid)

        then:
        modified.amountOfTurnedOnCubes() == 56
    }

    def 'should build cuboid based on command'() {
        when:
        Cuboid actual = cuboid(command)

        then:
        actual == expected

        where:
        command                           || expected
        'on x=-20..26,y=-36..17,z=-47..7' || enabledCuboid(-20..26, -36..17, -47..7)
        'off x=9..11,y=9..11,z=9..11'     || disabledCuboid(9..11, 9..11, 9..11)
    }

    def 'should ignore cubes outside allowed area'() {
        expect:
        enabledCuboid(-51..-49, -51..-49, -51..-49).cubes.size() == 8
        enabledCuboid(49..51, 49..51, 49..51).cubes.size() == 8
    }

    def 'should be able to process multiple commands'() {
        given:
        List<Cuboid> cuboidsToApply = readLines(path).collect { cuboid(it) }
        Reactor reactor = new Reactor()

        when:
        Reactor modified = cuboidsToApply.inject(reactor) {
            acc, next -> {
                println "processing $next"
                return acc.withCuboid(next)
            }
        }

        then:
        def actual = modified.amountOfTurnedOnCubes()
        actual == expected

        where:
        path                           || expected
        '/advent-of-code/day22/input1' || 39
        '/advent-of-code/day22/input2' || 590784
        '/advent-of-code/day22/input3' || 533863

    }
}
