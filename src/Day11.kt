class Day11: Day {
    data class Panel(val pos: Position, var value: Long = 0L)

    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    fun nextDir(currDir: Char, turn: Long): Char {
        val dirs = "ULDR"
        val moves = mapOf(0L to 1, 1L to dirs.length - 1)
        return dirs[(dirs.indexOf(currDir) + moves[turn]!!) % dirs.length]
    }

    val moves = mapOf(
        'U' to Position(0, -1),
        'L' to Position(-1, 0),
        'D' to Position(0, 1),
        'R' to Position(1, 0))

    fun paint(inst: List<Long>, startValue: Int): List<Panel> {
        val computer = Intcode(inst)

        val panels = mutableListOf(Panel(Position(0, 0), value = startValue.toLong()))
        var currPanel: Panel? = panels[0]
        var currDir = 'U'

        while (!computer.halted) {
            val out = computer.execute(listOf(currPanel!!.value))
            currPanel.value = out[0]
            currDir = nextDir(currDir, out[1])
            val newPos = currPanel.pos + moves[currDir]!!
            currPanel = panels.find { it.pos == newPos }
            if (currPanel == null) {
                currPanel = Panel(newPos, 0)
                panels.add(currPanel)
            }
        }

        return panels
    }

    override fun solvePartOne(input: List<String>): Int {
        return paint(convert(input), 0).size
    }

    fun List<Panel>.repr(): String {
        val minX = this.minBy { it.pos.x }!!.pos.x
        val minY = this.minBy { it.pos.y }!!.pos.y
        val width = this.maxBy { it.pos.x }!!.pos.x - minX
        val height = this.maxBy { it.pos.y }!!.pos.y - minY

        val repr = List(height + 1) { MutableList(width + 1) { ' ' } }

        this.forEach {p ->
            repr[p.pos.y - minY][p.pos.x - minX] =
                when (p.value) {
                    0L -> ' '
                    1L -> '#'
                    else -> ' '
                }
        }
        return repr.joinToString("\n") { it.joinToString(separator = "") }
    }

    override fun solvePartTwo(input: List<String>): String {
        val panels = paint(convert(input), 1)
        return "\n" + panels.repr() + "\n"
    }
}