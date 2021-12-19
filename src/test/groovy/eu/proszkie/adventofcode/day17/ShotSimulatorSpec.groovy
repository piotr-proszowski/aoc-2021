package eu.proszkie.adventofcode.day17

import kotlin.ranges.IntRange
import spock.lang.Specification

class ShotSimulatorSpec extends Specification {

    ShotSimulator shotSimulator = new ShotSimulator()
    VelocityFinder velocityFinder = new VelocityFinder(shotSimulator)

    def "should simulate shot"() {
        when:
        Trajectory actual = shotSimulator.shot(new Coords(0, 0), new Velocities(5, 8))

        then:
        actual.currentCoords == new Coords(0, 0)
        actual.next().currentCoords == new Coords(5, 8)
        actual.afterNumOfSteps(2).currentCoords == new Coords(9, 15)
        actual.afterNumOfSteps(3).currentCoords == new Coords(12, 21)
        actual.afterNumOfSteps(4).currentCoords == new Coords(14, 26)
        actual.afterNumOfSteps(5).currentCoords == new Coords(15, 30)
        actual.afterNumOfSteps(6).currentCoords == new Coords(15, 33)
        actual.afterNumOfSteps(7).currentCoords == new Coords(15, 35)
        actual.afterNumOfSteps(8).currentCoords == new Coords(15, 36)
        actual.afterNumOfSteps(9).currentCoords == new Coords(15, 36)
        actual.afterNumOfSteps(10).currentCoords == new Coords(15, 35)
    }

    def "should find x velocity when y velocity is known"() {
        when:
        Velocities actual = velocityFinder.deduceVelocity(knownY, target).velocities

        then:
        actual.x == expectedX
        actual.y == expectedY

        where:
        knownY | target             || expectedX | expectedY
        8      | new Coords(15, 36) || 5         | 8
        13     | new Coords(15, 36) || 6         | 13
        36     | new Coords(15, 36) || 15        | 36

    }

    def "should find velocities with highest y reached"() {
        when:
        Velocities actual = velocityFinder.deduceVelocity(target).velocities

        then:
        actual.x == expectedX
        actual.y == expectedY

        where:
        target             || expectedX | expectedY
        new Coords(15, 36) || 5         | 36
    }

    def "should find velocities with highest y reached for multiple coords as target"() {
        when:
        VelocitiesReachingTheTarget actual = velocityFinder.deduceVelocity(target)

        then:
        actual.velocities.x == expectedX
        actual.velocities.y == expectedY
        actual.highestYReached == expectedHighestYReached

        where:
        target                                                      || expectedX | expectedY | expectedHighestYReached
        new Target(new IntRange(156, 202), new IntRange(-110, -69)) || 18        | 109       | 0
    }

    def "should find distinct initial velocities"() {
        when:
        Set<Velocities> actual = velocityFinder.findAllPossibleVelocities(target)

        then:
        actual.size() == expectedSize

        where:
        target                                                      || expectedSize
        new Target(new IntRange(20, 30), new IntRange(-10, -5))     || 112
        new Target(new IntRange(156, 202), new IntRange(-110, -69)) || 0
    }
}
