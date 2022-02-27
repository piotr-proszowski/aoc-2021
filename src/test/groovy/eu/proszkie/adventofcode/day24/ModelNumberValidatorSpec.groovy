package eu.proszkie.adventofcode.day24

import spock.lang.Specification

import static eu.proszkie.adventofcode.day24.MonadDefinitionFixture.loadMonadFromFile

class ModelNumberValidatorSpec extends Specification {
    def 'should validate single model number'() {
        given:
            ModelNumber modelNumber = new ModelNumber('79997391969649')
            MonadDefinition monadDefinition = loadMonadFromFile('monad.txt')
            ModelNumberValidator validator = new ModelNumberValidator(monadDefinition)
        expect:
            validator.validate(modelNumber).get('z' as char) == 0
    }
}
