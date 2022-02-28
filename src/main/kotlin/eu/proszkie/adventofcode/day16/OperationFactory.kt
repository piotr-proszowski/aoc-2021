package eu.proszkie.adventofcode.day16

object OperationFactory {
    fun fromTypeId(typeId: PacketTypeId): Operation =
        when (typeId.raw) {
            0 -> Sum
            1 -> Product
            2 -> Minimum
            3 -> Maximum
            5 -> FirstIsGreaterThanRest
            6 -> FirstIsLessThanRest
            7 -> Equal
            else -> throw IllegalStateException("Unknown operation. Packet type id: $typeId")
        }
}

sealed class Operation {
    abstract fun calculate(packets: List<Packet>): Long
}

object Sum : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        packets.sumOf(Packet::getValue)
}

object Product : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        packets.fold(1) { prev, next -> prev * next.getValue() }
}

object Minimum : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        packets.map(Packet::getValue).minOf { it }
}

object Maximum : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        packets.map(Packet::getValue).maxOf { it }
}

object FirstIsGreaterThanRest : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        if (packets.first().getValue() > packets.drop(1).first().getValue()) 1 else 0
}

object FirstIsLessThanRest : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        if (packets.first().getValue() < packets.drop(1).first().getValue()) 1 else 0
}

object Equal : Operation() {
    override fun calculate(packets: List<Packet>): Long =
        if (packets.first().getValue() == packets.drop(1).first().getValue()) 1 else 0
}
