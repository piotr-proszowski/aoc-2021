package eu.proszkie.adventofcode.day01

class SonarSweep {
    fun howMuchIncreases(input: List<String>): Int {
        val measurements = input.map { it.toInt() }
        return howMuchIncreasesInternal(measurements)
    }

    private fun howMuchIncreasesInternal(input: List<Int>): Int {
        return input.foldIndexed(0) { index, acc, element ->
            if(index > 0 && element > input[index - 1]) {
                acc + 1
            } else {
                acc
            }
        }
    }

    fun howMuchIncreasesWindowed(input: List<String>): Int {
        val measurements = input.map { it.toInt() }
        val slices = measurements.mapIndexed { index, i -> index.rangeTo(index + 2)}.dropLast(2)
        val windowed = slices.map { measurements.slice(it).sum() }

        return howMuchIncreasesInternal(windowed)
    }
}