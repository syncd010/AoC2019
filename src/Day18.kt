
class Day18: Day {
    // This represents a key found on the maze
    data class MapKey(val id: Char, val steps:Int, val requires: List<Char>)

    private val dirs = listOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0))

    private fun List<String>.positionOf(c: Char): Position? {
        for (y in indices) {
            val x = get(y).indexOf(c)
            if (x != -1) return Position(x, y)
        }
        return null
    }

    fun convert(input: List<String>) : Map<Char, List<MapKey>> {
        // Finds the accessible keys starting in [from] with a BFS
        fun findKeys(maze: List<String>, from: Position): List<MapKey> {
            val keys = mutableListOf<MapKey>()
            val frontier = mutableListOf(Triple(from, 0, listOf<Char>())) // List of <key position, distance, required keys>
            val visited = mutableListOf(from)

            while (frontier.isNotEmpty()) {
                val (pos, dist, required) = frontier.removeAt(0)
                for (newPos in dirs.map { pos + it }) {
                    val type = maze[newPos.y][newPos.x]
                    if (newPos in visited || type == '#') continue
                    visited.add(newPos)
                    val newRequired = required.toMutableList()
                    if (type.isLowerCase() || type.isUpperCase()) {
                        if (type.isLowerCase())
                            keys.add(MapKey(type, dist + 1, required))
                        newRequired.add(type.toLowerCase())
                    }
                    frontier.add(Triple(newPos, dist + 1, newRequired))
                }
            }
            return keys
        }

        val mazeMap = mutableMapOf<Char, List<MapKey>>()
        input.joinToString("")
            .replace(Regex("([#.])"), "")
            .forEach { key -> mazeMap[key] = findKeys(input, input.positionOf(key)!!) }
        return mazeMap
    }

    // Represents a path taken. [frontier] is the last key(s) got, [seenKeys] is the path, steps the distance
    data class Path(val frontier: List<Char>, val seenKeys: List<Char>, val steps: Int)
    private fun Path.repr(): String = seenKeys.joinToString(" -> ") { "$it" }

    // Find the paths to get all the keys starting from [start] positions
    private fun findPaths(mazeMap: Map<Char, List<MapKey>>, start: List<Char>): List<Path> {
        val numKeys = mazeMap.keys.filter { it.isLowerCase() }.size
        val frontier = mutableListOf(Path(start, emptyList<Char>(), 0))
        val foundPaths = mutableListOf<Path>()

        // BFS
        while (frontier.size > 0) {
            val path = frontier.removeAt(0)
            if (path.seenKeys.size == numKeys) {
                foundPaths.add(path)
                continue
            }

            // For each possible independent move
            for (idx in path.frontier.indices) {
                // Get the possible keys it can move to:
                // keys that are on the map, that we haven't seen yet and that are "unlocked"
                for (newStep in mazeMap[path.frontier[idx]]!!.filter { it.id !in path.seenKeys && it.requires.all { it in path.seenKeys } }) {
                    val newPath = Path(
                        path.frontier.toMutableList().apply { this[idx] = newStep.id },
                        path.seenKeys + newStep.id,
                        path.steps + newStep.steps)
                    var addPath = true
                    // Check if the new path is just a permutation of an existing one and ignore it if so
                    for (existingPath in frontier) {
                        if ((existingPath.frontier == newPath.frontier) &&
                            (existingPath.seenKeys.size == newPath.seenKeys.size) &&
                            (existingPath.seenKeys.all { it in newPath.seenKeys })
                        ) {
                            if (existingPath.steps > newPath.steps)
                                frontier.remove(existingPath)  // Found a more efficient path, remove old add new
                            else
                                addPath = false // Old path is more efficient
                            break
                        }
                    }
                    if (addPath) frontier.add(newPath)
                }
            }
        }
        return foundPaths
    }

    override fun solvePartOne(input: List<String>): Int {
        val mazeMap = convert(input)
        val foundPaths = findPaths(mazeMap, listOf('@'))
//        foundPaths.forEach { println("${it.steps} -> ${it.repr()}") }
        return foundPaths.minBy { it.steps }!!.steps
    }

    override fun solvePartTwo(input: List<String>): Int {
        val startPos = input.positionOf('@')!!
        val inputStr = input.toMutableList()
        inputStr[startPos.y-1] = inputStr[startPos.y-1].substring(0, startPos.x - 1) + "1#2" + inputStr[startPos.y-1].substring(startPos.x + 2)
        inputStr[startPos.y] = inputStr[startPos.y].substring(0, startPos.x - 1) + "###" + inputStr[startPos.y].substring(startPos.x + 2)
        inputStr[startPos.y+1] = inputStr[startPos.y+1].substring(0, startPos.x - 1) + "3#4" + inputStr[startPos.y+1].substring(startPos.x + 2)

        val mazeMap = convert(inputStr)

        val foundPaths = findPaths(mazeMap, listOf('1', '2', '3', '4'))
//        foundPaths.forEach { println("${it.steps} -> ${it.repr()}") }
        return foundPaths.minBy { it.steps }!!.steps
    }

}