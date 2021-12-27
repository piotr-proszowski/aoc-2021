package eu.proszkie.adventofcode.day20

import kotlin.math.max
import kotlin.math.min

object ImageFactory {
    fun createFromStrings(input: List<String>, padding: Int): Image {
        return input.mapIndexed { index, line -> index with line.map(Pixel.Companion::create) }
            .flatMap {
                val y = it.index
                it.pixels.mapIndexed { x, pixel ->
                    Coords(x, y) to pixel
                }
            }
            .associate { it.first to it.second }
            .let(::Image)
            .withPadding(padding)
    }
}

data class IndexWithPixels(val index: Int, val pixels: List<Pixel>)

private infix fun Int.with(pixels: List<Pixel>) = IndexWithPixels(this, pixels)

sealed class Pixel {
    companion object {
        fun create(character: Char): Pixel {
            return when (character) {
                '.' -> DarkPixel
                '#' -> LightPixel
                else -> throw IllegalStateException()
            }
        }
    }
}

object DarkPixel : Pixel() {
    override fun toString(): String {
        return "."
    }
}

object LightPixel : Pixel() {
    override fun toString(): String {
        return "#"
    }
}

object ImageEnhancementAlgorithmFactory {
    fun createFromString(input: String): ImageEnhancementAlgorithm {
        return ImageEnhancementAlgorithm(input)
    }
}

data class Image(private val pixels: Map<Coords, Pixel>) {
    fun reduced(): Image {
        val coords = pixels.keys
        val borders = getBordersFor(coords)
        val bordersWithoutLightPixels = getBordersWithoutLightPixels(borders, pixels)
        return if (bordersWithoutLightPixels.isEmpty()) {
            this
        } else {
            copy(pixels = pixels.without(bordersWithoutLightPixels.flatten().toSet()))
                .reduced()
        }
    }

    fun amountOfLightPixels() = pixels.values.filterIsInstance<LightPixel>().count()

    private fun getBordersWithoutLightPixels(
        borders: List<List<Coords>>,
        input: Map<Coords, Pixel>
    ) = borders.filter { it.map(input::get).filterIsInstance<LightPixel>().count() == 0 }

    private fun getBordersFor(coords: Set<Coords>): List<List<Coords>> {
        val topLeftCorner = coords.minOrNull()!!
        val downRightCorner = coords.maxOrNull()!!
        val topRightCorner = Coords(downRightCorner.x, topLeftCorner.y)
        val downLeftCorner = Coords(topLeftCorner.x, downRightCorner.y)
        return listOf(
            (topLeftCorner to topRightCorner),
            (topRightCorner to downRightCorner),
            (downRightCorner to downLeftCorner),
            (downLeftCorner to topLeftCorner)
        )
    }

    private fun Map<Coords, Pixel>.without(coords: Set<Coords>): Map<Coords, Pixel> {
        return this.entries
            .filter { !coords.contains(it.key) }
            .associate { it.key to it.value }
    }


    fun withPadding(paddingLayers: Int = 1): Image {
        if(paddingLayers == 0) {
            return this
        }
        val topLeftCorner = pixels.keys.minOrNull()!!.upLeft()
        val downRightCorner = pixels.keys.maxOrNull()!!.downRight()
        val topRightCorner = Coords(downRightCorner.x, topLeftCorner.y)
        val downLeftCorner = Coords(topLeftCorner.x, downRightCorner.y)
        val padding = getBordersFor(setOf(topLeftCorner, downRightCorner, topRightCorner, downLeftCorner))
            .flatten()
            .associateWith { DarkPixel }
        return Image(pixels + padding)
            .withPadding(paddingLayers - 1)
    }

    override fun toString(): String {
        val coords = this.pixels.keys
        val topLeftCorner = coords.minOrNull()!!
        val downRightCorner = coords.maxOrNull()!!
        val minX = topLeftCorner.x
        val minY = topLeftCorner.y
        val maxX = downRightCorner.x
        val maxY = downRightCorner.y
        return (minY..maxY).map { y ->
            (minX..maxX).map { x ->
                pixels.get(Coords(x, y)) ?: ""
            }.joinToString(separator = "")
        }.joinToString(separator = "\n")
            .trim()
    }

    fun averagedPixels(): Map<Coords, Int> {
        return pixels.entries.map {
            val current = it.value
            it.key to it.key.grid().map(pixels::get)
                .map { it ?: current }
                .map { if (it is DarkPixel) 0 else 1 }
                .joinToString(separator = "")
                .let { Integer.parseInt(it, 2) }
        }.associate { it.first to it.second }
    }

    fun withEnhancedPixels(enhancedPixels: Map<Coords, Pixel>): Image {
        return copy(pixels = pixels + enhancedPixels)
    }
}

class ImageEnhancementAlgorithm(private val raw: String) {
    fun enhance(image: Image, times: Int = 1): Image {
        if(times == 0) {
            return image
        }
        val enhancedPixels = image.averagedPixels()
            .map { it.key to raw[it.value] }
            .map { it.first to Pixel.create(it.second) }
            .associate { it.first to it.second }
        return enhance(image.withEnhancedPixels(enhancedPixels), times - 1)
    }
}

data class Coords(val x: Int, val y: Int) : Comparable<Coords> {
    override fun compareTo(other: Coords): Int {
        return if (x == other.x) {
            y.compareTo(other.y)
        } else if (y == other.y) {
            x.compareTo(other.x)
        } else {
            x.compareTo(other.x)
        }
    }

    fun grid() = listOf(upLeft(), up(), upRight(), left(), this, right(), downLeft(), down(), downRight())
    private fun up() = Coords(x, y - 1)
    private fun down() = Coords(x, y + 1)
    private fun right() = Coords(x + 1, y)
    private fun left() = Coords(x - 1, y)
    fun upRight() = Coords(x + 1, y - 1)
    fun upLeft() = Coords(x - 1, y - 1)
    fun downRight() = Coords(x + 1, y + 1)
    fun downLeft() = Coords(x - 1, y + 1)
}

private infix fun Coords.to(other: Coords): List<Coords> {
    if (this.x == other.x) {
        val minY = min(this.y, other.y)
        val maxY = max(this.y, other.y)
        return (minY..maxY).map { Coords(x, it) }
    } else if (this.y == other.y) {
        val minX = min(this.x, other.x)
        val maxX = max(this.x, other.x)
        return (minX..maxX).map { Coords(it, y) }
    }

    throw IllegalStateException("Given coords do not lay on straight line")
}