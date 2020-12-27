class Day21: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }


    private fun Intcode.springdroid(commands: String) : Long {
        val out = execute(commands
                .split("")
                .drop(1).dropLast(1)
                .map { it[0].toLong() })
        if (out.last() < 256L) {
            val view = out.map { it.toChar() }
                .joinToString("") { it.toString() }
                .trim()
            println(view)
            return -1
        }
        return out.last()
    }

    override fun solvePartOne(input: List<String>): Long {
        // !A || (!B && D) || (!C && D)
        val commands = ("NOT A J\n" +
                "NOT B T\n" +
                "AND D T\n" +
                "OR T J\n" +
                "NOT C T\n" +
                "AND D T\n" +
                "OR T J\n" +
                "WALK\n")
        return  Intcode(convert(input)).springdroid(commands)
    }

    override fun solvePartTwo(input: List<String>): Long {
        // !A || (!B && D) || (!C && D)
        val commands = ("NOT A J\n" +
                "NOT B T\n" +
                "AND D T\n" +
                "OR T J\n" +
                "NOT C T\n" +
                "AND D T\n" +
                "AND E T\n" +
                "OR T J\n" +
                "NOT C T\n" +
                "AND D T\n" +
                "AND H T\n" +
                "OR T J\n" +
                "RUN\n")
        return  Intcode(convert(input)).springdroid(commands)
    }
}