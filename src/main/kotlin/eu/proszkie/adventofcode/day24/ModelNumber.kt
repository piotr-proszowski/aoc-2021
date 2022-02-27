package eu.proszkie.adventofcode.day24

data class ModelNumber(private val raw: String) {
    init {
        require(raw.all(Char::isDigit)) { "Model number can consists only of digits" }
        require(
            raw.map(Char::digitToInt).all { it in 1..9 }) {
            "Model number can consists only of digits in 1..9 range"
        }
        require(raw.length == 14) { "Model number need to have exactly 14 digits" }
    }

    fun digits() =
        raw.map(Char::digitToInt)
}