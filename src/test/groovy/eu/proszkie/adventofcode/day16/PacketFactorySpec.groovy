package eu.proszkie.adventofcode.day16

import spock.lang.Specification

import static eu.proszkie.adventofcode.day16.PacketAssertions.assertThat

class PacketFactorySpec extends Specification {
    def 'should read packet transmission with outer packet operator of length type with id 0'() {
        given:
            String hex = '38006F45291200'
        when:
            Packet packetTransmission = PacketTransmissionFactory.INSTANCE.fromHex(hex)
        then:
            //@formatter:off
            assertThat(packetTransmission)
                    .hasVersionEqualTo(1)
                    .hasLengthTypeEqualTo(0)
                    .hasPacketTypeIdEqualTo(6)
                    .hasSubPacketThat()
                        .hasVersionEqualTo(6)
                        .hasPacketTypeIdEqualTo(4)
                        .and()
                    .hasSubPacketThat()
                        .hasVersionEqualTo(2)
                        .hasPacketTypeIdEqualTo(4)
                        .and()
                    .noMoreSubPackets()
            //@formatter:on
    }

    def 'should read packet transmission with outer packet operator of length type with id 1'() {
        given:
            String hex = 'EE00D40C823060'
        when:
            Packet packetTransmission = PacketTransmissionFactory.INSTANCE.fromHex(hex)
        then:
            //@formatter:off
            assertThat(packetTransmission)
                    .hasVersionEqualTo(7)
                    .hasLengthTypeEqualTo(1)
                    .hasPacketTypeIdEqualTo(3)
                    .hasSubPacketThat()
                        .hasVersionEqualTo(2)
                        .hasPacketTypeIdEqualTo(4)
                        .and()
                    .hasSubPacketThat()
                        .hasVersionEqualTo(4)
                        .hasPacketTypeIdEqualTo(4)
                        .and()
                    .hasSubPacketThat()
                        .hasVersionEqualTo(1)
                        .hasPacketTypeIdEqualTo(4)
                        .and()
                    .noMoreSubPackets()
            //@formatter:on
    }
}
