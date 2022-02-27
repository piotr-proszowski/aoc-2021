package eu.proszkie.adventofcode.day16

import spock.lang.Specification

class HexToBinaryMapperSpec extends Specification {
    def 'should convert hex to binary'() {
        expect:
            HexToBinaryMapper.INSTANCE.map(hex) == binary
        where:
            hex              || binary
            '38006F45291200' || '00111000000000000110111101000101001010010001001000000000'
            'EE00D40C823060' || '11101110000000001101010000001100100000100011000001100000'
    }
}
