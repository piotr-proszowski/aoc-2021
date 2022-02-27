package eu.proszkie.adventofcode.day24

import spock.lang.Specification

import static eu.proszkie.adventofcode.day24.MonadDefinitionFixture.loadMonadFromFile

class ModelNumberFinderSpec extends Specification {
    def 'should find highest valid number'() {
        given:
            MonadDefinition monadDefinition = loadMonadFromFile('monad.txt')
            ModelNumberFinder finder = new ModelNumberFinder(monadDefinition)
        expect:
            def highest = finder.findHighestValid()
            println(highest)
    }

    def 'should find lowest valid number'() {
        given:
            MonadDefinition monadDefinition = loadMonadFromFile('monad.txt')
            ModelNumberFinder finder = new ModelNumberFinder(monadDefinition)
        expect:
            def lowest = finder.findLowestValid()
            println(lowest)
    }
}
