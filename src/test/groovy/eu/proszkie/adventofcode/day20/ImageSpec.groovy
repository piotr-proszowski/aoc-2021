package eu.proszkie.adventofcode.day20

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class ImageSpec extends Specification implements WithResourceReadingAbility {
    def "should enhance the map"() {
        given:
        List<String> lines = readLines('/advent-of-code/day20/input2')
        Image image = ImageFactory.INSTANCE.createFromStrings(lines.drop(2), 51)
        ImageEnhancementAlgorithm algorithm = ImageEnhancementAlgorithmFactory.INSTANCE.createFromString(lines.first())

        and:
        List<String> expectedImagePath = readLines('/advent-of-code/day20/input1-expected-image')
        Image expected = ImageFactory.INSTANCE.createFromStrings(expectedImagePath, 1)

        when:
        Image actual = algorithm.enhance(image, 50)

        then:
        def actualAsString = actual.reduced().toString()
        def expectedAsString = expected.reduced().toString()
        println('Amount of light pixels: ' + actual.amountOfLightPixels())
        actualAsString == expectedAsString

    }
}
