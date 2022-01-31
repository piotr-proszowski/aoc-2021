package eu.proszkie.adventofcode.day23

sealed class Movable(val energyNeededToMove: Int, val token: Char)
object Amber : Movable(1, 'A')
object Bronze : Movable(10, 'B')
object Copper : Movable(100, 'C')
object Desert : Movable(1000, 'D')
object MovableFactory {
    fun fromElement(element: Element): Movable {
        return when (element) {
            Element.Amber -> Amber
            Element.Bronze -> Bronze
            Element.Copper -> Copper
            Element.Desert -> Desert
            else -> throw IllegalArgumentException("Element $element is not movable")
        }
    }
}