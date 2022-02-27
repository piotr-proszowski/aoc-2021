package eu.proszkie.adventofcode.day16

object HexToBinaryMapper {
    fun map(hex: String): String =
        hex.map(::Hex).map(Hex::toBinary)
            .joinToString(separator = "")
}

data class Hex(val raw: Char) {
    fun toBinary(): String =
        Integer.toBinaryString(raw.digitToInt(16)).withZeroPadding()
}

private fun String.withZeroPadding(): String =
    "0".repeat(4 - this.length) + this
