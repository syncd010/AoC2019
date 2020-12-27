import kotlin.math.sign

class Day13: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Int {
        val output = Intcode(convert(input))
            .execute()
        return output.slice(2 until output.size step 3).count { it == 2L }
    }

    private fun display(screen: List<Long>) {
        val maxX = screen.slice(0 until screen.size step 3).max()!!.toInt()
        val maxY = screen.slice(1 until screen.size step 3).max()!!.toInt()
        val map = mapOf(0L to " ", 1L to "#", 2L to "-", 3L to "_", 4L to "o")

        val disp = List(maxY + 1) { MutableList(maxX + 1) { " " } }
        for (idx in screen.indices step 3) {
            if (screen[idx] == -1L) continue
            disp[screen[idx + 1].toInt()][screen[idx].toInt()] = map[screen[idx + 2]] ?: "?"
        }

        println(disp.joinToString("\n") { it.joinToString("") })
    }

    private fun findTile(screen: List<Long>, tile: Long): Position? {
        val idx = screen.slice(2 until screen.size step 3).indexOf(tile)
        return if (idx >= 0) Position(screen[idx * 3].toInt(), screen[idx * 3 + 1].toInt()) else null
    }

    override fun solvePartTwo(input: List<String>): Int {
        val arcade = Intcode(convert(input))
        arcade.mem[0] = 2
        var dir = 0L
        while (true) {
            val screen = arcade.execute(listOf(dir))
            if (arcade.halted) {
                return screen[screen.indexOf(-1L) + 2].toInt()
            }

            val ballPos = findTile(screen, 4)!!
            val paddlePos = findTile(screen, 3)!!
            dir = (ballPos.x - paddlePos.x).sign.toLong()
        }
    }
}