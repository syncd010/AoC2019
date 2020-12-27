import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.hypot
import kotlin.math.PI

class Day10: Day {
    private fun Position.direction(): Position? {
        val g = abs(gcd(x, y))
        return if (g == 0) null else Position(x / g, y / g)
    }

    fun Position.angleFromVertical(): Double {
        val h = hypot(x.toDouble(), y.toDouble())
        return when {
            (x >= 0) && (y < 0) -> asin(x / h)
            (x > 0) && (y >= 0) -> PI / 2 + asin(y / h)
            (x <= 0) && (y > 0) -> PI + asin(-x / h)
            (x < 0) && (y <= 0) -> 3 * PI / 2 + asin(-y / h)
            else -> -1.0
        }
    }

    fun Position.hypot(): Double = hypot(x.toDouble(), y.toDouble())

    private fun convert(input: List<String>) : List<Position> {
        return input.mapIndexed { idxRow, row ->
            row.mapIndexedNotNull { idxCol, col ->
                if (col == '#') Position(idxCol, idxRow) else null
            }
        }.flatten()
    }

    override fun solvePartOne(input: List<String>): Int {
        val asteroids = convert(input)
        return asteroids
            .map { orig ->
                asteroids
                    .filter { it != orig }
                    .map { dest -> dest - orig }
                    .groupBy { it.direction() }
                    .size
            }.max() ?: -1
    }

    override fun solvePartTwo(input: List<String>): Int {
        val asteroids = convert(input)

        val grouped = asteroids
            .map { orig ->
                asteroids
                    .filter { it != orig }
                    .map { dest -> dest - orig }
                    .groupBy { it.direction() }
            }

        val idxMax = grouped.withIndex()
            .maxBy { it.value.size }!!
            .index

        val sortedAsts = grouped[idxMax].values
            .sortedBy { it[0].angleFromVertical() }
            .map { it.sortedBy { it.hypot() } }

        val res = sortedAsts[199][0] + asteroids[idxMax]
        return res.x * 100 + res.y
    }

}