class Day9: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): List<Long> {
        return Intcode(convert(input))
            .execute(listOf(1))
    }

    override fun solvePartTwo(input: List<String>): List<Long> {
        return Intcode(convert(input))
            .execute(listOf(2))
    }
}