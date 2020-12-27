class Day19: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Int {
        val initialMem = convert(input)
        val computer = Intcode(initialMem)
        val grid = List(50) { MutableList(50) { ' ' } }
        for (x in 0..49) {
            for (y in 0..49) {
                computer.reset(initialMem)
                val res = computer.execute(listOf(x.toLong(), y.toLong()))
                grid[y][x] = if (res.first() == 1L) '#' else '.'

            }
        }
//        grid.forEach() { println(it.joinToString("")) }

        return grid.sumBy { it.count { it == '#' } }
    }

    override fun solvePartTwo(input: List<String>): Long {
        val initialMem = convert(input)
        val computer = Intcode(initialMem)

        var y = 100L
        var x = 0L
        while (true) {
            val bottomLeft = computer.run {
                reset(initialMem)
                execute(listOf(x, y))
            }.first()
            if (bottomLeft != 1L) {
                x++
                continue
            }
            val bottomRight = computer.run {
                reset(initialMem)
                execute(listOf(x + 99, y))
            }.first()
            val topLeft = computer.run {
                reset(initialMem)
                execute(listOf(x, y - 99))
            }.first()
            val topRight = computer.run {
                reset(initialMem)
                execute(listOf(x + 99, y - 99))
            }.first()
            if (topLeft != 1L || topRight != 1L || bottomRight != 1L) {
                y++
                continue
            }
            return x * 10000 + y - 99
        }
    }
}