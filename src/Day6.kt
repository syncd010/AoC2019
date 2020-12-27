class Day6: Day {
    private fun convert(input: List<String>) : Map<String, String> {
        return input.map { it.split(")") }.associate { Pair(it[1], it[0]) }
    }

    /**
     * Assuming [map] is a path map, returns the path from [from] to [to]
     */
    private fun getMapPath(map: Map<String, String>, from: String, to: String): List<String> {
        var tmp = from
        val path = mutableListOf<String>()
        do {
            path.add(tmp)
            tmp = map[tmp] ?: error("Incorrect map")
        } while (tmp != to)
        return path
    }

    override fun solvePartOne(input: List<String>): Int {
        val orbits = convert(input)
        return orbits.keys.map { getMapPath(orbits, it, "COM").size }.sum()
    }

    override fun solvePartTwo(input: List<String>): Int {
        val orbits = convert(input)
        val youPath = getMapPath(orbits, "YOU", "COM").toSet()
        val santaPath = getMapPath(orbits, "SAN", "COM").toSet()
        val common = (youPath union santaPath) subtract (youPath intersect santaPath)
        return common.size - 2
    }
}