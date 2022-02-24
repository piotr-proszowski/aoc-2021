package eu.proszkie.adventofcode.day24

import spock.lang.Specification

import static eu.proszkie.adventofcode.day24.MonadDefinitionFixture.loadFromFile

class MonadNumberValidatorSpec extends Specification {
    def 'should find highest valid number'() {
        given:
            MonadDefinition monadDefinition = loadFromFile('monad.txt')
            ModelNumberFinder finder = new ModelNumberFinder(monadDefinition)
        expect:
            def highest = finder.findHighestValid()
            println(highest)
    }
}
