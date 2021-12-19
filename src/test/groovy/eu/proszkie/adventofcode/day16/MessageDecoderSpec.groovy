package eu.proszkie.adventofcode.day16

import eu.proszkie.adventofcode.WithResourceReadingAbility
import spock.lang.Specification

class MessageDecoderSpec extends Specification implements WithResourceReadingAbility {

    def "should decode a hex to binary"() {
        given:
        def input = 'EE00D40C823060'
        def expected = '11101110000000001101010000001100100000100011000001100000'

        when:
        def actual = HexToByteDecoder.INSTANCE.hexToByte(input)

        then:
        actual == expected
    }

    def "should decode literal value"() {
        given:
        def input = '101111111000101000'

        when:
        LiteralValuePacket actual = LiteralValueDecoder.INSTANCE.decode(input, 6)

        then:
        actual.version == 6
        actual.typeId == 4
        actual.value == 2021
    }

    def "should decode operator in type one"() {
        given:
        def input = '10000000001101010000001100100000100011000001100000'

        when:
        OperatorPacket actual = OperatorDecoder.INSTANCE.decode(input, 3, 1)

        then:
        actual.version == 3
        actual.typeId == 1
        actual.subpackets.size() == 3
    }

    def "should decode operator in type zero"() {
        given:
        def input = '00000000000110111101000101001010010001001000000000'

        when:
        OperatorPacket actual = OperatorDecoder.INSTANCE.decode(input, 6, 0)

        then:
        actual.version == 6
        actual.typeId == 0
        actual.subpackets.size() == 2
    }

    def "should properly decode a message"() {
        given:
        def input = readLines('/advent-of-code/day16/input1').first()

        when:
        def binary = HexToByteDecoder.INSTANCE.hexToByte(input)
        def decoded = PacketsDecoder.INSTANCE.decode(binary)
        def actual = decoded.sum { it.versionsSum() }

        then:
        actual == 12
    }
}
