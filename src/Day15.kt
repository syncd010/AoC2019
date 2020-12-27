class Day15: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    data class Direction(val dir: Long, val opposite: Long, val posChange: Position)
    val dirs = listOf(
            Direction(1, 2, Position(0, -1)),
            Direction(2, 1, Position(0, 1)),
            Direction(3, 4, Position(-1, 0)),
            Direction(4, 3, Position(1, 0)))

    private fun mapMaze(computer: Intcode, initialPos: Position): Pair<List<Position>, Position> {
        fun dfsMap(computer: Intcode,
                   currPos: Position,
                   visited: MutableList<Position>): Position? {
            var goalPos: Position? = null
            for (dir in dirs) {
                val newPos = currPos + dir.posChange
                if (newPos in visited) continue

                val status = computer.execute(listOf(dir.dir))
                if (status.first() == 0L) continue // Wall

                visited.add(newPos)

                if (status.first() == 2L) goalPos = newPos
                val foundPos = dfsMap(computer, newPos, visited)
                if (goalPos == null) goalPos = foundPos
                computer.execute((listOf(dir.opposite)))
            }
            return goalPos
        }

        val visited = mutableListOf(initialPos)
        val goalPos = dfsMap(computer, initialPos, visited)!!
        return Pair(visited, goalPos)
    }

    private fun findPath(maze: List<Position>,
                         initialPos: Position,
                         finalPos: Position): List<Position> {
        fun dfs(maze: List<Position>,
                currPos: Position,
                finalPos: Position,
                visited: MutableList<Position>): MutableList<Position>? {
            var bestPath: MutableList<Position>? = null

            for (dir in dirs) {
                val newPos = currPos + dir.posChange
                if (newPos in visited || newPos !in maze) continue
                if (newPos == finalPos) return mutableListOf(newPos)

                visited.add(newPos)
                val path = dfs(maze, newPos, finalPos, visited)?.apply { add(newPos) }
                if (path != null && (bestPath == null || path.size < bestPath.size)) {
                    bestPath = path
                }
            }
            // return the best path found
            return bestPath
        }

        val visited = mutableListOf(initialPos)
        return dfs(maze, initialPos, finalPos, visited)!!
    }

    private fun floodMaze(maze: List<Position>, initialPos: Position): Int {
        var frontier = mutableListOf(initialPos)
        var nextFrontier = mutableListOf<Position>()
        val visited = mutableListOf(initialPos)
        var count = 0

        do {
            while (frontier.isNotEmpty()) {
                val currPos = frontier.removeAt(0)
                visited.add(currPos)
                for (dir in dirs) {
                    val nextPos = currPos + dir.posChange
                    if (nextPos !in visited && nextPos in maze)
                        nextFrontier.add(nextPos)
                }
            }
            count++
            frontier = nextFrontier.also { nextFrontier = frontier }
        } while (frontier.isNotEmpty())

        return count - 1
    }

    override fun solvePartOne(input: List<String>): Int {
        val initialPos = Position(0, 0)
        val (maze, finalPos) = mapMaze(Intcode(convert(input)), initialPos)
        return findPath(maze, initialPos, finalPos).size
    }

    override fun solvePartTwo(input: List<String>): Int {
        val initialPos = Position(0, 0)
        val (maze, finalPos) = mapMaze(Intcode(convert(input)), initialPos)
        return floodMaze(maze, finalPos)
    }
}