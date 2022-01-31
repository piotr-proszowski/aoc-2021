package eu.proszkie.adventofcode.day23

data class Coords(val x: Int, val y: Int) : Comparable<Coords> {
    fun adjacent() = setOf(left(), right(), up(), down())
    fun up() = copy(y = y - 1)
    fun down() = copy(y = y + 1)
    fun right() = copy(x = x + 1)
    fun left() = copy(x = x - 1)

    override fun compareTo(other: Coords): Int {
        val xComparison = x.compareTo(other.x)
        val yComparison = y.compareTo(other.y)
        return if (yComparison == 0) {
            xComparison
        } else {
            yComparison
        }
    }
}