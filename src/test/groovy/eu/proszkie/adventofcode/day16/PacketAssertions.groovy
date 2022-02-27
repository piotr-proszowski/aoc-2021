package eu.proszkie.adventofcode.day16

class PacketAssertions {

    Packet target
    PacketAssertions parent
    int nextSubPacketToAssert = 0

    private PacketAssertions() {}

    static PacketAssertions assertThat(Packet packet) {
        return new PacketAssertions(target: packet)
    }

    PacketAssertions hasVersionEqualTo(int version) {
        assert target.version.raw == version
        return this
    }

    PacketAssertions hasLengthTypeEqualTo(int lengthTypeId) {
        assert target as OperatorPacket
        OperatorPacket operatorPacket = target as OperatorPacket
        assert operatorPacket.lengthTypeId.raw == lengthTypeId
        return this
    }

    PacketAssertions hasPacketTypeIdEqualTo(int packetTypeId) {
        assert target.typeId.raw == packetTypeId
        return this
    }

    PacketAssertions hasSubPacketThat() {
        assert target as OperatorPacket
        OperatorPacket operatorPacket = target as OperatorPacket
        return new PacketAssertions(target: operatorPacket.subPackets[nextSubPacketToAssert++], parent: this)
    }

    PacketAssertions and() {
        return parent
    }

    void noMoreSubPackets() {
        assert target as OperatorPacket
        OperatorPacket operatorPacket = target as OperatorPacket
        assert nextSubPacketToAssert == operatorPacket.subPackets.size()
    }
}
