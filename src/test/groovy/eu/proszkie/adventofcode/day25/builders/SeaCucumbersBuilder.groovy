package eu.proszkie.adventofcode.day25.builders

import eu.proszkie.adventofcode.day25.SeaCucumbersState
import eu.proszkie.adventofcode.day25.SeaCucumbersStateFactory

class SeaCucumbersBuilder {
    static SeaCucumbersState seaCucumbersState(List<String> lines) {
        return SeaCucumbersStateFactory.INSTANCE.fromStringLines(lines)
    }
}
