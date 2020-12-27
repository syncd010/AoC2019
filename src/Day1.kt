import kotlin.math.max

class Day1: Day {
    private fun convert(input: List<String>) : List<Int> {
        return input.map { it.toInt() }
    }

    override fun solvePartOne(input: List<String>): Int {
        return convert(input).map { max(it / 3 - 2, 0)  }.sum()
    }

    override fun solvePartTwo(input: List<String>): Int {
        var fuel = convert(input)
        var res = 0

        do {
            fuel = fuel.mapNotNull { if (it / 3 - 2 > 0) it / 3 - 2 else null }
            res += fuel.sum()
        } while (fuel.isNotEmpty())

        return res
    }
}