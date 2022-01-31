package eu.proszkie.adventofcode.day23

import java.util.UUID

object BurrowsCache {
    private val burrowToId: MutableMap<Map<Coords, Element>, UUID> = HashMap()
    private val idToBurrow: MutableMap<UUID, Map<Coords, Element>> = HashMap()

    fun findById(burrowId: UUID): Map<Coords, Element>? = idToBurrow[burrowId]

    fun saveOrFindExisting(elements: Map<Coords, Element>): UUID {
        return burrowToId[elements]
            ?: save(elements)
    }

    private fun save(elements: Map<Coords, Element>): UUID {
        val id = UUID.randomUUID()
        burrowToId[elements] = id
        idToBurrow[id] = elements
        return id
    }

}