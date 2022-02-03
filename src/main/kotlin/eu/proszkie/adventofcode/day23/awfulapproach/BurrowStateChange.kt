package eu.proszkie.adventofcode.day23.awfulapproach

data class BurrowStateChange(val movable: Movable, val startingCoords: Coords, val destination: Coords)
sealed class BurrowStateChangeResult
data class BurrowStateChangeSucceeded(val burrowState: BurrowState) : BurrowStateChangeResult()
data class BurrowStateChangeFailed(val burrowChangeFailed: BurrowChangeFailed) : BurrowStateChangeResult()