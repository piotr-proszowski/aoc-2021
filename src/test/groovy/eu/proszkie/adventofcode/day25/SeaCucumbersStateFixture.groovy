package eu.proszkie.adventofcode.day25

import eu.proszkie.adventofcode.WithResourceReadingAbility

import static eu.proszkie.adventofcode.day25.builders.SeaCucumbersBuilder.seaCucumbersState

class SeaCucumbersStateFixture implements WithResourceReadingAbility {

    static def firstInitialState() {
        return seaCucumbersState([
                '..v>.',
                '.....'
        ])
    }

    static def firstExpectedState() {
        return seaCucumbersState([
                '....>',
                '..v..'
        ])
    }

    static def secondInitialState() {
        return seaCucumbersState([
                '.>>>.',
                'v....',
                'v....'
        ])
    }

    static def secondExpectedState() {
        return seaCucumbersState([
                'v>>.>',
                'v....',
                '.....'
        ])
    }

    static def thirdInitialState() {
        return seaCucumbersState([
                'v...>>.vv>',
                '.vv>>.vv..',
                '>>.>v>...v',
                '>>v>>.>.v.',
                'v>v.vv.v..',
                '>.>>..v...',
                '.vv..>.>v.',
                'v.v..>>v.v',
                '....v..v.>'
        ])
    }

    static def thirdExpectedState() {
        return seaCucumbersState([
                '....>.>v.>',
                'v.v>.>v.v.',
                '>v>>..>v..',
                '>>v>v>.>.v',
                '.>v.v...v.',
                'v>>.>vvv..',
                '..v...>>..',
                'vv...>>vv.',
                '>.v.v..v.v'
        ])
    }

    static SeaCucumbersState cucumbersFromFile(String path) {
        return seaCucumbersState(
                new SeaCucumbersStateFixture().readLines("/advent-of-code/day25/$path")
        )
    }
}
