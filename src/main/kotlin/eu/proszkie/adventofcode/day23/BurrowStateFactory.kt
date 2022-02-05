package eu.proszkie.adventofcode.day23

object BurrowStateFactory {
    fun from(burrow: Burrow, amphipods: List<Pair<Coords, AmphipodType>>): BurrowState {
        return amphipods.map { it.toAmphipod(burrow) }
            .let { BurrowState(burrow, groupedByCoords(it)) }
    }

    private fun groupedByCoords(it: List<Amphipod>) = it.groupBy(Amphipod::coords).mapValues { it.value.first() }

    private fun Pair<Coords, AmphipodType>.toAmphipod(burrow: Burrow): Amphipod {
        val (coords, amphipodType) = this
        burrow.findRoom(coords)?.let { room ->
            if (room.isDesiredFor(amphipodType) && room.isDeepest(coords)) {
                return NotMovableAmphipod(coords, amphipodType)
            }
        }

        return MovableAmphipod(coords, amphipodType)
    }
}