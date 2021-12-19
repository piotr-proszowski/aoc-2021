package eu.proszkie.adventofcode.day16

import java.lang.Integer.parseInt
import java.lang.Integer.toBinaryString
import java.lang.Long.parseLong

object HexToByteDecoder {
    fun hexToByte(input: String): String {
        return input.map { parseInt(it.toString(), 16).toBinary() }
            .joinToString(separator = "")
    }

    private fun Int.toBinary(): String {
        val binary = toBinaryString(this)
        return "0".repeat(4 - binary.length) + binary
    }
}

object LiteralValueDecoder {

    fun decode(input: String, version: Int): LiteralValuePacket {
        val typeId = 4
        val binaryValue = getBinaryValue(input)
        val value = toIntValue(binaryValue)
        val lengthOfVersion = 3
        val lengthOfType = 3

        val length = lengthOfVersion + lengthOfType + binaryValue.flatten().size

        return LiteralValuePacket(version, typeId, parseLong(value, 2), length)
    }

    private fun toIntValue(binaryValue: List<List<Char>>): String {
        return binaryValue
            .map { it.drop(1) }
            .flatten()
            .joinToString(separator = "")
    }

    private fun getBinaryValue(input: String): List<List<Char>> {
        return input.asSequence()
            .windowed(size = 5, step = 5)
            .takeWhileInclusive { it.first() == '1' }
            .toList()
    }
}

fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

object OperatorDecoder {
    fun decode(input: String, version: Int, typeId: Int): OperatorPacket {
        val lengthTypeId = parseInt(input.first().toString(), 2)
        val subpackets: List<Packet> = when (lengthTypeId) {
            0 -> decodeZeroLengthTypeOperator(input.drop(1))
            1 -> decodeOneLengthTypeOperator(input.drop(1))
            else -> throw IllegalStateException()
        }
        return OperatorPacket(
            version,
            typeId,
            subpackets,
            3 + 3 + 1 + subpackets.sumOf(Packet::length) + if (lengthTypeId == 0) 15 else 11
        )
    }

    private fun decodeZeroLengthTypeOperator(input: String): List<Packet> {
        val lengthOfSubpackets = parseInt(input.take(15), 2)
        return PacketsDecoder.decode(input.drop(15).take(lengthOfSubpackets))
    }

    private fun decodeOneLengthTypeOperator(input: String): List<Packet> {
        val amountOfSubpackets = parseInt(input.take(11), 2)
        return (1..amountOfSubpackets).map { n ->
            PacketsDecoder.decode(input.drop(11 * n)).first()
        }
    }
}

object PacketsDecoder {
    fun decode(input: String): List<Packet> {
        return generateSequence(listOf<Packet>()) { packets ->
            val nextPacketStart = input.drop(packets.sumOf(Packet::length))
            if (nextPacketStart.isEmpty() || nextPacketStart.all { it == '0' }) {
                return@generateSequence packets + PaddingPacket(nextPacketStart.length)
            }
            val version = parseInt(nextPacketStart.take(3), 2)
            val typeId = parseInt(nextPacketStart.drop(3).take(3), 2)
            packets + when (typeId) {
                4 -> LiteralValueDecoder.decode(nextPacketStart.drop(6), version)
                else -> OperatorDecoder.decode(nextPacketStart.drop(6), version, typeId)
            }
        }
            .takeWhileInclusive { it.sumOf(Packet::length) < input.length }
            .flatten()
            .toSet()
            .filterNot { it is PaddingPacket }
            .toList()
    }
}

sealed class Packet(open val version: Int, open val length: Int) {
    abstract fun versionsSum(): Int
}
data class LiteralValuePacket(override val version: Int, val typeId: Int, val value: Long, override val length: Int) :
    Packet(version, length) {

    override fun versionsSum(): Int {
        return version
    }
}

data class OperatorPacket(
    override val version: Int,
    val typeId: Int,
    val subpackets: List<Packet>,
    override val length: Int
) : Packet(version, length) {
    override fun versionsSum(): Int {
        return version + subpackets.sumOf(Packet::versionsSum)
    }
}

data class PaddingPacket(override val length: Int) : Packet(0, length) {
    override fun versionsSum(): Int {
        return 0
    }
}