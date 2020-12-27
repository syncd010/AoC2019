import kotlin.math.min
import kotlin.math.max
import kotlin.system.exitProcess

class Day3: Day {

    data class Line(val from: Position, val to: Position) {
        val isVertical: Boolean get() = from.x == to.x
        val isHorizontal: Boolean get() = from.y == to.y
        val isPosition: Boolean get() = isVertical && isHorizontal
        val isOrigin: Boolean get() = (from.x == 0 && from.y == 0) || (to.x == 0 && to.y == 0)

        val minX: Int get() = min(from.x, to.x)
        val minY: Int get() = min(from.y, to.y)
        val maxX: Int get() = max(from.x, to.x)
        val maxY: Int get() = max(from.y, to.y)

        infix fun intersect(other: Line): Line? {
            when {
                this.isHorizontal && other.isHorizontal && (this.from.y == other.from.y) -> {
                    val maxMinX = max(this.minX, other.minX)
                    val minMaxX = min(this.maxX, other.maxX)
                    val y = this.from.y

                    return if (minMaxX >= maxMinX) Line(Position(maxMinX, y), Position(minMaxX, y)) else null
                }
                this.isVertical && other.isVertical && (this.from.x == other.from.x) -> {
                    val maxMinY = max(this.minY, other.minY)
                    val minMaxY = min(this.maxY, other.maxY)
                    val x = this.from.x

                    return if (minMaxY >= maxMinY) Line(Position(maxMinY, x), Position(minMaxY, x)) else null
                }
                this.isHorizontal && other.isVertical &&
                        (this.minX <= other.minX) && (this.maxX >= other.maxX) &&
                        (other.minY <= this.minY) && (other.maxY >= this.maxY) -> {
                    val intersection = Position(other.minX, this.minY)
                    return Line(intersection, intersection)
                }
                this.isVertical && other.isHorizontal &&
                        (this.minY <= other.minY) && (this.maxY >= other.maxY) &&
                        (other.minX <= this.minX) && (other.maxX >= this.maxX) -> {
                    val intersection = Position(this.minX, other.minY)
                    return Line(intersection, intersection)
                }
            }
            return null
        }
    }

    private fun convert(input: List<String>) : Pair<List<Line>, List<Line>> {

        fun convertLine(directions: List<String>): List<Line> {
            val lines = mutableListOf<Line>()
            for ((index, dir) in directions.withIndex()) {
                val from: Position = if (index == 0) Position(0, 0) else lines[index - 1].to

                val steps = dir.substring(1).toInt()
                val to: Position = when (dir[0]) {
                    'R' -> Position(from.x + steps, from.y)
                    'L' -> Position(from.x - steps, from.y)
                    'U' -> Position(from.x, from.y + steps)
                    'D' -> Position(from.x, from.y - steps)
                    else -> {
                        println("Unknown direction: $dir[0]")
                        exitProcess(-1)
                    }
                }
                lines.add(Line(from, to))
            }
            return lines
        }

        return Pair(convertLine(input[0].split(",")), convertLine(input[1].split(",")))
    }

    override fun solvePartOne(input: List<String>): Int? {
        val wires = convert(input)
        val intersections = mutableListOf<Line>()
        for (firstLine in wires.first) {
            for (secondLine in wires.second) {
                if (firstLine.isOrigin && secondLine.isOrigin) continue
                val tmp = (firstLine.intersect(secondLine))
                if (tmp != null) { intersections.add(tmp) }
            }
        }

        val origin = Position(0, 0)
        val distances = intersections.map {
            if (it.isPosition) manhattanDist(origin, it.from)
            else min(manhattanDist(origin, it.from), manhattanDist(origin, it.to))
        }
        return distances.min()
    }

    override fun solvePartTwo(input: List<String>): Int? {
        val wires = convert(input)
        val intersectionSteps = mutableListOf<Int>()
        var firstWireSteps = 0
        for (firstLine in wires.first) {
            var secondWireSteps = 0
            for (secondLine in wires.second) {
                val inter = (firstLine.intersect(secondLine))
                if ((inter != null) && !(firstLine.isOrigin && secondLine.isOrigin)) {
                    val totalSteps = firstWireSteps + secondWireSteps +
                            min(manhattanDist(firstLine.from, inter.from), manhattanDist(firstLine.from, inter.to)) +
                            min(manhattanDist(secondLine.from, inter.from), manhattanDist(secondLine.from, inter.to))
                    intersectionSteps.add(totalSteps)
                }
                secondWireSteps += manhattanDist(secondLine.from, secondLine.to)
            }
            firstWireSteps += manhattanDist(firstLine.from, firstLine.to)
        }

        return intersectionSteps.min()
    }
}