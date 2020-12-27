class Day5: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): List<Long> {
        return Intcode(convert(input)).execute(listOf(1))
    }

    override fun solvePartTwo(input: List<String>): Long {
        val res = Intcode(convert(input)).execute(listOf(5))
        return res.firstOrNull() ?: -1
    }
}