package eu.proszkie.adventofcode.day24

import spock.lang.Specification

class ModelNumberSpec extends Specification {
    def 'model number is created when it has exactly 14 digits'() {
        expect:
            ModelNumber modelNumber = new ModelNumber('12345678912345')
    }

    def 'model number consists only from digits'() {
        when:
            new ModelNumber('123456789023BA')
        then:
            thrown(IllegalArgumentException)
    }

    def 'model number cannot have zeroes inside'() {
        when:
            new ModelNumber('12345678902345')
        then:
            thrown(IllegalArgumentException)
    }

    def 'model number cannot have less or more digits'() {
        when:
            new ModelNumber(digits)
        then:
            thrown(IllegalArgumentException)
        where:
            digits << ['123', '123456789123456']
    }
}
