import kotlin.math.min

class Day20: Day {
    // This represents a leg of a path
    data class Leg(val to: String, val steps: Int, val level: Int)

    private val dirs = listOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0))

    fun convert(input: List<String>) : Map<String, List<Leg>> {
        fun findPortals(input: List<String>): List<Pair<String, Position>> {
            val portals = mutableListOf<Pair<String, Position>>()
            for (y in input.indices) {
                for (x in input[y].indices) {
                    if (!input[y][x].isUpperCase() ||
                        (x > 0 && input[y][x-1].isUpperCase()) ||
                        (y > 0 && x < input[y-1].length && input[y-1][x].isUpperCase()))
                        continue

                    val posX =
                        if (x + 1 < input[y].length && input[y][x + 1].isUpperCase()) {
                            if (x > 0 && input[y][x - 1] == '.') x - 1
                            else x + 2
                        } else x
                    val posY =
                        if (y + 1 < input.size && x < input[y+1].length && input[y+1][x].isUpperCase()) {
                            if (y > 0 && x < input[y-1].length && input[y - 1][x] == '.') y - 1
                            else y + 2
                        } else y

                    val id = when {
                        y == posY -> String(charArrayOf(input[y][x], input[y][x+1]))
                        x == posX -> String(charArrayOf(input[y][x], input[y+1][x]))
                        else -> "ERROR"
                    }
                    portals.add(Pair(id, Position(posX, posY)))

                }
            }
            return portals
        }

        fun findLegs(maze: List<String>, from: String, fromPos: Position, portals: List<Pair<String, Position>>) : MutableList<Leg> {
            val legs = mutableListOf<Leg>()
            val frontier = mutableListOf(Pair(fromPos, 0)) // List of <position, distance>
            val visited = mutableListOf(fromPos)

            while (frontier.isNotEmpty()) {
                val (pos, dist) = frontier.removeAt(0)
                for (newPos in dirs.map { pos + it }) {
                    if (newPos.y < 0 || newPos.y >= maze.size || newPos.x < 0 || newPos.x >= maze[newPos.y].length ) continue
                    val type = maze[newPos.y][newPos.x]
                    if (newPos in visited || type == '#' || type == ' ') continue
                    visited.add(newPos)

                    if (type.isUpperCase()) {
                        val portal = portals.find { (_, portalPos) -> portalPos == pos }!!.first
                        val level = if (pos.y == 2 || pos.y == maze.size - 3 || pos.x == 2 || pos.x == maze[pos.y].length - 3) -1 else 1
                        val suffix = if (level == -1) 'o' else 'i'
                        if (portal != from) legs.add(Leg(portal + suffix, dist + 1, level))
                    } else {
                        frontier.add(Pair(newPos, dist + 1))
                    }
                }
            }
            return legs
        }

        val mazeMap = mutableMapOf<String, MutableList<Leg>>()

        with(findPortals(input)) {
            forEach { (id, pos) ->
                val legs = findLegs(input, id, pos, this)
                val suffix = if (pos.y == 2 || pos.y == input.size - 3 || pos.x == 2 || pos.x == input[pos.y].length - 3) 'o' else 'i'
                mazeMap[id + suffix] = legs
            }
        }

        return mazeMap
    }

    override fun solvePartOne(input: List<String>): Int {
        val mazeMap = convert(input)
        val frontier = mutableListOf(listOf(Leg("AAi", 0, 0)))
        var bestDist = Int.MAX_VALUE

        while (frontier.size > 0) {
            val path = frontier.minBy { it.sumBy { it.steps } }!!
            frontier.remove(path)
            if (path.last().to == "ZZo") {
                if (path.sumBy { it.steps } < bestDist) {
                    bestDist = path.sumBy { it.steps }
                }
                continue
            }
            val entry = path.last().to.substring(0..1) + (if (path.last().to[2] == 'i') 'o' else 'i')
            mazeMap[entry]?.forEach { leg ->
                if (path.all { it.to != leg.to } && (path.sumBy { it.steps } + leg.steps < bestDist))
                    frontier.add(path + leg)
            }
        }
        return bestDist - 1
    }

    override fun solvePartTwo(input: List<String>): Int {
        val mazeMap = convert(input)
        val frontier = mutableListOf(listOf(Leg("AAi", 0, 0)))
        var bestDist = Int.MAX_VALUE

        while (frontier.size > 0) {
            val path = frontier.minBy { it.last().steps }!!
            frontier.remove(path)
            if (path.last().to == "ZZo") {
                bestDist = min(bestDist, path.last().steps)
                continue
            }
            val entry = path.last().to.substring(0..1) + (if (path.last().to[2] == 'i') 'o' else 'i')
            mazeMap[entry]?.forEach { leg ->
                if ((leg.to == "ZZo" || leg.to == "AAo") && path.last().level != 0) return@forEach
                val level = path.last().level + leg.level
                val steps = path.last().steps + leg.steps

                if ((leg.to != "ZZo" && leg.to != "AAo" && level < 0) ||
                    (path.any { it.to == leg.to && it.level == level }) ||
                    (steps > bestDist))
                    return@forEach

                frontier.add(path + Leg(leg.to, steps, level))
            }
        }
        return bestDist - 1
    }
}