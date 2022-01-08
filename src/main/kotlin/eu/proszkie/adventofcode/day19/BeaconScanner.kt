package eu.proszkie.adventofcode.day19

import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs

object BeaconScanner {
    fun scan(input: List<Cube>): Cube {
        val mergingQueue: Queue<Cube> = LinkedList()
        mergingQueue.addAll(input)

        var current = mergingQueue.poll()
        while (!mergingQueue.isEmpty()) {
            val next = mergingQueue.poll()
            if (current.findCommonPointsWith(next).size < 12) {
                mergingQueue.add(next)
            } else {
                current = current.merge(next)
            }
            println("${mergingQueue.size} elements left in queue")
        }

        return current
    }
}