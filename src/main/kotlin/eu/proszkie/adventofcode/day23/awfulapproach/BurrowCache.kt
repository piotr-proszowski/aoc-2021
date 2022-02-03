package eu.proszkie.adventofcode.day23.awfulapproach

import java.util.UUID

object BurrowCache {

    private val idToElements: MutableMap<UUID, Map<Coords, Element>> = HashMap()
    private val elementsToId: MutableMap<Map<Coords, Element>, UUID> = HashMap()

    fun getIdFor(elements: Map<Coords, Element>): UUID {
        val onlyMeaningfulOnes = elements.filter { it.value.isMovable }
        return elementsToId[onlyMeaningfulOnes] ?: save(elements, onlyMeaningfulOnes)
    }

    private fun save(elements: Map<Coords, Element>, onlyMeaningfulOnes: Map<Coords, Element>): UUID {
        val id = UUID.randomUUID()
        elementsToId[onlyMeaningfulOnes] = id
        idToElements[id] = elements
        return id
    }

    fun findById(id: UUID): Map<Coords, Element> {
        return idToElements[id]!!
    }
}