class Day17: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Int {
        val view = Intcode(convert(input))
                .execute()
                .map { it.toChar() }
                .joinToString("") { it.toString() }
                .trim()
                .split("\n")

        return view.mapIndexed { y, row ->
            if (y == 0 || y == view.size - 1) 0
            else {
                row.mapIndexed { x, c ->
                    if (x == 0 || x == row.length - 1 || c != '#' ||
                            view[y - 1][x] != '#' ||
                            view[y + 1][x] != '#' ||
                            view[y][x - 1] != '#' ||
                            view[y][x + 1] != '#') 0
                    else x * y
                }.sum()
            }
        }.sum()
    }

    override fun solvePartTwo(input: List<String>): Int {
        val computer = Intcode(convert(input))

        computer.mem[0] = 2
        val commands = ("A , A , B , C , B , C , B , C , C , A \n " +
                "L , 1 0 , R , 8 , R , 8 \n " +
                "L , 1 0 , L , 1 2 , R , 8 , R , 1 0 \n " +
                "R , 1 0 , L , 1 2 , R , 1 0 \n " +
                "n \n")
                .split(" ")
                .map { it[0].toLong() }

        val out = computer.execute(commands)
        return out.last().toInt()
    }
}