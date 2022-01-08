package eu.proszkie.adventofcode.day19

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

import static eu.proszkie.adventofcode.day19.CubeBuilder.cube
import static eu.proszkie.adventofcode.day19.CubeBuilder.cubesFromString
import static eu.proszkie.adventofcode.day19.PointBuilder.point

class BeaconScannerSpec extends Specification implements WithResourceReadingAbility {

    ManhattanDistanceCalculator calculator = ManhattanDistanceCalculator.INSTANCE

    def 'should build cube'() {
        expect:
        cube([[1, 1, 0]]) == new Cube([new Point(1, 1, 0)] as Set, [] as Set)
    }

    def 'should calculate manhattan distance'() {
        when:
        def actual = calculator.calculate(first, second)

        then:
        actual == expected

        where:
        first           | second         || expected
        point(0, 0, 0)  | point(0, 0, 0) || 0
        point(1, 0, 0)  | point(0, 0, 0) || 1
        point(1, 12, 3) | point(0, 0, 0) || 16
    }

    def 'should read cubes from lines'() {
        expect:
        cubesFromString(readLines('/advent-of-code/day19/input1').join("\n")).size() == 5
    }

    def 'should find common points between read cubes'() {
        given:
        List<Cube> cubes = cubesFromString(readLines('/advent-of-code/day19/input1').join("\n"))

        when:
        def actual = cubes[0].findCommonPointsWith(cubes[1])

        then:
        actual.size() == 12

        where:
        firstCube | secondCube
        0         | 1
        1         | 4
    }

    def 'should merge cubes'() {
        given:
        List<Cube> cubes = cubesFromString(readLines('/advent-of-code/day19/input1').join("\n"))

        when:
        def actual = cubes[0].merge(cubes[1])

        then:
        actual.beacons.size() == 38
    }

    def 'should move point by vector'() {
        expect:
        point(0, 0, 0).moveBy(point(1, 1, 1)) == point(1, 1, 1)
    }

    def 'should rotate alongside X'() {
        expect:
        cube([[1, 2, 3]]).rotateAlongsideX() == cube([[1, 3, -2]])
        cube([[1, 3, -2]]).rotateAlongsideX() == cube([[1, -2, -3]])
        cube([[1, -2, -3]]).rotateAlongsideX() == cube([[1, -3, 2]])
        cube([[1, -3, 2]]).rotateAlongsideX() == cube([[1, 2, 3]])
    }

    def 'should rotate alongside Z'() {
        expect:
        cube([[1, 2, 3]]).rotateAlongsideZ() == cube([[2, -1, 3]])
        cube([[2, -1, 3]]).rotateAlongsideZ() == cube([[-1, -2, 3]])
        cube([[-1, -2, 3]]).rotateAlongsideZ() == cube([[-2, 1, 3]])
        cube([[-2, 1, 3]]).rotateAlongsideZ() == cube([[1, 2, 3]])
    }

    def 'should rotate alongside Y'() {
        expect:
        cube([[1, 2, 3]]).rotateAlongsideY() == cube([[-3, 2, 1]])
        cube([[-3, 2, 1]]).rotateAlongsideY() == cube([[-1, 2, -3]])
        cube([[-1, 2, -3]]).rotateAlongsideY() == cube([[3, 2, -1]])
        cube([[3, 2, -1]]).rotateAlongsideY() == cube([[1, 2, 3]])
    }

    def 'should find all rotation positions'() {
        expect:
        def rotations = cube([[686, 422, 578]]).allRotations()
        rotations.size() == 24
    }

    def 'should permutate all signs'() {
        expect:
        cube([[686, 422, 578]]).permutateSigns().size() == 8
    }

    def 'should find unique beacons'() {
        given:
        List<Cube> cubes = cubesFromString(readLines(input).join("\n"))

        when:
        def actual = BeaconScanner.INSTANCE.scan(cubes)

        then:
        actual.beacons.size() == expected

        where:
        input                          || expected
        '/advent-of-code/day19/input1' || 79
        '/advent-of-code/day19/input2' || 350
    }

    def 'should find all scanners positions'() {
        given:
        List<Cube> cubes = cubesFromString(readLines(input).join("\n"))

        when:
        def actual = BeaconScanner.INSTANCE.scan(cubes)

        then:
        actual.distanceBetweenTwoFarthestScanners() == expected

        where:
        input                          || expected
        '/advent-of-code/day19/input1' || 3621
        '/advent-of-code/day19/input2' || 10895
    }
}
