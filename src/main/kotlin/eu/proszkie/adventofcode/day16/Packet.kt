package eu.proszkie.adventofcode.day16

sealed class Packet {
    abstract fun sumUpAllVersions(): Int
    abstract val version: Version
    abstract val typeId: PacketTypeId
    abstract fun getValue(): Long
}

data class OperatorPacket(
    override val version: Version,
    override val typeId: PacketTypeId,
    val lengthTypeId: LengthTypeId,
    val subPackets: List<Packet>
) : Packet() {
    override fun sumUpAllVersions(): Int =
        version.raw + subPackets.sumOf(Packet::sumUpAllVersions)

    override fun getValue(): Long =
        OperationFactory.fromTypeId(typeId).calculate(subPackets)
}

data class LiteralValuePacket(
    override val version: Version,
    override val typeId: PacketTypeId,
    val literalValues: List<LiteralValue>
) : Packet() {
    override fun sumUpAllVersions(): Int =
        version.raw

    override fun getValue(): Long =
        literalValues.map(LiteralValue::binary).joinToString(separator = "").toLong(2)
}

data class Version(val raw: Int)
data class PacketTypeId(val raw: Int) {
    fun toPacketType(): PacketType =
        when (raw) {
            4 -> LiteralValuePacketType
            else -> OperatorPacketType(this)
        }
}

sealed class PacketType {
    abstract val typeId: PacketTypeId
}

object LiteralValuePacketType : PacketType() {
    override val typeId = PacketTypeId(4)
}

data class OperatorPacketType(override val typeId: PacketTypeId) : PacketType()

data class LengthTypeId(val raw: Int) {
    fun toLengthType(): LengthType =
        when (raw) {
            0 -> TotalLengthOfSubPackets
            1 -> SubPacketsAmount
            else -> throw IllegalStateException("Unknown length type id: $raw")
        }
}

sealed class LengthType {
    abstract val lengthTypeId: LengthTypeId
}

object TotalLengthOfSubPackets : LengthType() {
    override val lengthTypeId = LengthTypeId(0)
}

object SubPacketsAmount : LengthType() {
    override val lengthTypeId = LengthTypeId(1)
}

data class LiteralValue(
    val isLast: Boolean,
    val binary: String
)