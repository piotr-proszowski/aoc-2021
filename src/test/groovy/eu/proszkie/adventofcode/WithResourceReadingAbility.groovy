package eu.proszkie.adventofcode

trait WithResourceReadingAbility {
    URL getResource(String path) {
        getClass().getResource(path)
    }

    List<String> readLines(String path) {
        return getResource(path).readLines()
    }
}
