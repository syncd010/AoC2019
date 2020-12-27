import java.io.File
import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

interface Day {
    fun solvePartOne(input: List<String>): Any?
    fun solvePartTwo(input: List<String>): Any?
}

@ExperimentalTime
fun main(args: Array<String>) {

    val dayNumber: Int = if (args.isEmpty()) {
        println("Which day to run (1-25) ?")
        readLine()!!.toInt()
    } else {
        args[0].toInt()
    }

    var fileName: String? = when {
        args.isEmpty() -> {
            println("File to process?")
            readLine()
        }
        args.size >= 2 -> args[1]
        else -> null
    }
    fileName = when {
        fileName.isNullOrEmpty() -> "res/input${dayNumber}"
        fileName in listOf("t", "test") -> "res/input${dayNumber}Test"
        else -> fileName
    }

    val input = File(fileName).readLines()

    val dayClass = Class.forName("Day$dayNumber")
    val day = dayClass.constructors[0].newInstance() as Day

    val partOne = measureTimedValue { day.solvePartOne(input) }
    val partTwo = measureTimedValue { day.solvePartTwo(input) }

    val padding = max(partOne.value.toString().length, partTwo.value.toString().length) + 14
    println("Part 1: ${partOne.value}".padEnd(padding, ' ') + "(${partOne.duration})")
    println("Part 2: ${partTwo.value}".padEnd(padding, ' ') + "(${partTwo.duration})")

}