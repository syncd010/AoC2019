import kotlin.math.abs

class Day12: Day {
    private fun convert(input: List<String>) : List<Position> {
        val regexp = """<x=([+-]?\d+), y=([+-]?\d+), z=([+-]?\d+)>""".toRegex()

        return input.map {
            val m = regexp.find(it)!!
            return@map Position(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt())
        }
    }

    private fun Position.energy() : Int = abs(x) + abs(y) + abs(z)

    override fun solvePartOne(input: List<String>): Int {
        val positions = convert(input).toMutableList()
        val numBodies = positions.size
        val velocities = MutableList(numBodies) { Position(0, 0, 0) }
        val comb = (0 until numBodies).toSet().combinations(2)

        for (step in 1..1000) {
            for (c in comb) {
                val idx1 = c.elementAt(0)
                val idx2 = c.elementAt(1)

                val diff = positions[idx1] - positions[idx2]
                velocities[idx1] -= diff.sign()
                velocities[idx2] += diff.sign()
            }

            for (i in 0 until positions.size)
                positions[i] += velocities[i]
        }

        return positions.zip(velocities).sumBy { it.first.energy() * it.second.energy() }
    }

    private fun getKeys(positions: List<Position>, velocities: List<Velocity>): Triple<List<Int>, List<Int>, List<Int>> {
        val keyX = mutableListOf<Int>()
        val keyY = mutableListOf<Int>()
        val keyZ = mutableListOf<Int>()
        for (i in positions.indices) {
            keyX.add (positions[i].x)
            keyX.add (velocities[i].x)
            keyY.add (positions[i].y)
            keyY.add (velocities[i].y)
            keyZ.add (positions[i].z)
            keyZ.add (velocities[i].z)
        }
        return Triple(keyX, keyY, keyZ)
    }

    override fun solvePartTwo(input: List<String>): Long {
        val positions = convert(input).toMutableList()
        val numBodies = positions.size
        val velocities = MutableList(numBodies) { Position(0, 0, 0) }
        val comb = (0 until numBodies).toSet().combinations(2)

        val periods = LongArray(3) { -1 }
        val seenX = emptyMap<List<Int>, Long>().toMutableMap()
        val seenY = emptyMap<List<Int>, Long>().toMutableMap()
        val seenZ = emptyMap<List<Int>, Long>().toMutableMap()
        var step = 0L

        val (keyX, keyY, keyZ) = getKeys(positions, velocities)
        seenX[keyX] = step
        seenY[keyY] = step
        seenZ[keyZ] = step

        while (periods.any { it == -1L }) {
            step++

            for (c in comb) {
                val idx1 = c.elementAt(0)
                val idx2 = c.elementAt(1)
                val diff = positions[idx1] - positions[idx2]
                velocities[idx1] -= diff.sign()
                velocities[idx2] += diff.sign()
            }

            for (i in 0 until positions.size)
                positions[i] += velocities[i]

            val (keyX, keyY, keyZ) = getKeys(positions, velocities)

            if (periods[0] == -1L)
                if (keyX in seenX) {
                    periods[0] = step
                } else {
                    seenX[keyX] = step
                }

            if (periods[1] == -1L)
                if (keyY in seenY) {
                    periods[1] = step
                } else {
                    seenY[keyY] = step
                }

            if (periods[2] == -1L)
                if (keyZ in seenZ) {
                    periods[2] = step
                } else {
                    seenZ[keyZ] = step
                }
        }

        return lcm(lcm(periods[2], periods[1]), periods[0])
    }
}