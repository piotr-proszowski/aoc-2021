package eu.proszkie.adventofcode.day23

class BurrowAssertions {
    ChangeAttemptResult target

    static BurrowAssertions assertThat(ChangeAttemptResult changeResult) {
        BurrowAssertions assertions = new BurrowAssertions()
        assertions.target = changeResult
        return assertions
    }

    void hasSameBurrowIgnoringVisitedPlaces(BurrowState burrowState) {
        def changedBurrow = (target as Success).changedBurrow
        def actualCoordsToClass = changedBurrow.burrow.raw.collect { [it.key, it.value.class] }
        def expectedCoordsToClass = burrowState.burrow.raw.collect { [it.key, it.value.class] }

        assert actualCoordsToClass == expectedCoordsToClass
    }
}
