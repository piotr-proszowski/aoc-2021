package eu.proszkie.adventofcode

trait WithResourceReadingAbility {
    URL getResource(String path) {
        getClass().getResource(path)
    }
}
