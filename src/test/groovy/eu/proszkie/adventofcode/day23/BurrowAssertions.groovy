package eu.proszkie.adventofcode.day23

class BurrowAssertions {
    Burrow target

    private BurrowAssertions() {

    }

    static BurrowAssertions assertThat(Burrow burrow) {
        return new BurrowAssertions(target: burrow)
    }
}
