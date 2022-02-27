package eu.proszkie.adventofcode.day16

import java.util.LinkedList

object PacketTransmissionFactory {
    fun fromHex(hex: String): Packet {
        val input = HexToBinaryMapper.map(hex).iterator()
        return fromBinaryIterator(input)
    }

    private fun fromBinaryIterator(input: CharIterator): Packet {
        val version = loadPacketVersion(input)
        return when (val packetType = loadPacketType(input)) {
            is LiteralValuePacketType -> loadLiteralValuePacket(version, input)
            is OperatorPacketType -> loadOperatorPacket(version, packetType, input)
        }
    }

    private fun loadPacketVersion(input: CharIterator): Version =
        nextNBitsAsInt(3, input).let(::Version)

    private fun loadPacketType(input: CharIterator): PacketType =
        nextNBitsAsInt(3, input)
            .let(::PacketTypeId)
            .toPacketType()

    private fun nextNBitsAsInt(n: Int, input: CharIterator) = input.asSequence()
        .take(n)
        .joinToString(separator = "")
        .toInt(2)

    private fun loadLiteralValuePacket(version: Version, input: CharIterator): LiteralValuePacket {
        val literalValues = readLiteralValues(input)
        return LiteralValuePacket(version, LiteralValuePacketType.typeId, literalValues)
    }

    private fun readLiteralValues(input: CharIterator): List<LiteralValue> {
        val literalValues: MutableList<LiteralValue> = LinkedList()
        while (literalValues.isEmpty() || !literalValues.lastOrNull()!!.isLast) {
            literalValues.add(readLiteralValue(input))
        }
        return literalValues
    }

    private fun readLiteralValue(input: CharIterator): LiteralValue {
        val isLast = readIfItIsLastLiteralValue(input)
        val binaryValue = readBinaryValue(input)
        return LiteralValue(isLast, binaryValue)
    }

    private fun readIfItIsLastLiteralValue(input: CharIterator): Boolean {
        return nextNBitsAsInt(1, input) == 0
    }

    private fun readBinaryValue(input: CharIterator): String =
        input.asSequence().take(4).joinToString(separator = "")

    private fun loadOperatorPacket(
        version: Version,
        packetType: OperatorPacketType,
        input: CharIterator
    ): OperatorPacket =
        when (loadLengthType(input)) {
            SubPacketsAmount -> loadOperatorWithSubPacketsAmount(version, packetType, input)
            TotalLengthOfSubPackets -> loadOperatorWithTotalLengthOfSubPackets(version, packetType, input)
        }

    private fun loadOperatorWithSubPacketsAmount(
        version: Version,
        packetType: OperatorPacketType,
        input: CharIterator
    ): OperatorPacket {
        val amountOfSubPackets = loadAmountOfSubPackets(input)
        val subPackets = readSubPacketsKnowingAmount(input, amountOfSubPackets)
        return OperatorPacket(
            version = version,
            typeId = packetType.typeId,
            lengthTypeId = SubPacketsAmount.lengthTypeId,
            subPackets = subPackets
        )
    }

    private fun loadAmountOfSubPackets(input: CharIterator): Int =
        nextNBitsAsInt(11, input)

    private fun readSubPacketsKnowingAmount(input: CharIterator, amountOfSubPackets: Int): List<Packet> {
        val subPackets: MutableList<Packet> = LinkedList()
        while (subPackets.size < amountOfSubPackets) {
            val subPacket = fromBinaryIterator(input)
            subPackets.add(subPacket)
        }
        return subPackets
    }

    private fun loadOperatorWithTotalLengthOfSubPackets(
        version: Version,
        packetType: OperatorPacketType,
        input: CharIterator
    ): OperatorPacket {
        val totalLengthOfSubPackets = loadTotalLengthOfSubPackets(input)
        val subPackets = readSubPacketsKnowingTotalLength(input, totalLengthOfSubPackets)
        return OperatorPacket(
            version = version,
            typeId = packetType.typeId,
            lengthTypeId = TotalLengthOfSubPackets.lengthTypeId,
            subPackets = subPackets
        )
    }

    private fun loadTotalLengthOfSubPackets(input: CharIterator): Int =
        nextNBitsAsInt(15, input)

    private fun readSubPacketsKnowingTotalLength(input: CharIterator, totalLengthOfSubPackets: Int): List<Packet> {
        val charIterator = ReadCountAwareCharIterator(input)
        return readSubPacketsInternal(charIterator, totalLengthOfSubPackets)
    }

    private fun readSubPacketsInternal(
        charIterator: ReadCountAwareCharIterator,
        totalLengthOfSubPackets: Int
    ): List<Packet> {
        val subPackets: MutableList<Packet> = LinkedList()
        while (charIterator.readCount < totalLengthOfSubPackets) {
            val subPacket = fromBinaryIterator(charIterator)
            if (charIterator.readCount <= totalLengthOfSubPackets) {
                subPackets.add(subPacket)
            }
        }
        return subPackets
    }

    private fun loadLengthType(input: CharIterator): LengthType =
        nextNBitsAsInt(1, input)
            .let(::LengthTypeId)
            .toLengthType()
}

class ReadCountAwareCharIterator(private val raw: CharIterator) : CharIterator() {
    var readCount = 0
        private set

    override fun hasNext() = raw.hasNext()

    override fun nextChar(): Char {
        readCount++
        return raw.next()
    }
}