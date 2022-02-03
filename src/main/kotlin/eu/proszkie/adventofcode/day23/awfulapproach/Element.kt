package eu.proszkie.adventofcode.day23.awfulapproach

import eu.proszkie.adventofcode.day23.awfulapproach.Element.values

enum class Element(val token: Char, val isMovable: Boolean) {
    Wall('#', false),
    FreeSpace('.', false),
    OutOfBurrow(' ', false),
    Amber('A', true),
    Bronze('B', true),
    Copper('C', true),
    Desert('D', true);

    companion object {
        fun getByToken(token: Char): Element? {
            return values().find { it.token == token }
        }
    }

    override fun toString(): String {
        return "$token"
    }

}

object ElementFactory {
    fun fromToken(token: Char): Element? {
        return values().find { it.token == token }
    }
}