package eu.proszkie.adventofcode.day16

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class OperatorPacketBuilder {
    Integer version
    Integer typeId
    Integer lengthTypeId
    List<Packet> subPackets

    static Packet operatorPacket(@DelegatesTo(OperatorPacketBuilder) Closure definition) {
        OperatorPacketBuilder builder = new OperatorPacketBuilder()
        builder.with(definition)
        return builder.build()
    }

    OperatorPacket build() {
        return new OperatorPacket(
                new Version(version),
                new PacketTypeId(typeId),
                new LengthTypeId(lengthTypeId),
                subPackets
        )
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class LiteralValuePacketBuilder {
    Integer version = 4
    List<LiteralValue> literalValues = []

    static LiteralValuePacket literalValuePacket(@DelegatesTo(LiteralValuePacketBuilder) Closure definition) {
        LiteralValuePacketBuilder builder = new LiteralValuePacketBuilder()
        builder.with(definition)
        builder.build()
    }

    private LiteralValuePacket build() {
        return new LiteralValuePacket(new Version(version), new PacketTypeId(4), literalValues)
    }

}

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class LiteralValueBuilder {
    boolean isLast = false
    String binary = '1011'

    static LiteralValue literalValue(@DelegatesTo(LiteralValueBuilder) Closure definition = {}) {
        LiteralValueBuilder builder = new LiteralValueBuilder()
        builder.with(definition)
        builder.build()
    }

    private LiteralValue build() {
        return new LiteralValue(isLast, binary)
    }
}
