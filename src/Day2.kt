class Day2: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Long {
        val computer = Intcode(convert(input))

        computer.mem[1] = 12
        computer.mem[2] = 2

        computer.execute()
        return computer.mem[0]
    }

    override fun solvePartTwo(input: List<String>): Long {
        for (noun in 0..99L) {
            for (verb in 0..99L) {
                val computer = Intcode(convert(input))
                computer.mem[1] = noun
                computer.mem[2] = verb
                computer.execute()
                if (computer.mem[0] == 19690720L)
                    return 100 * noun + verb
            }
        }
        return -1
    }
}