
class Day8: Day {
    private val WIDTH = 25
    private val HEIGHT = 6

    private fun convert(input: List<String>) : List<Int> {
        return input[0].map { it - '0' }
    }

    override fun solvePartOne(input: List<String>): Int {
        val image = convert(input).chunked(WIDTH * HEIGHT)
        val zeroCount = image.map { it.count { it == 0 } }
        val oneCount = image.map { it.count { it == 1 } }
        val twoCount = image.map { it.count { it == 2 } }
        val idx = zeroCount.indexOf(zeroCount.min())

        return oneCount[idx] * twoCount[idx]
    }

    private fun layerToString(layer: List<List<Int>>): String {
        return layer.joinToString("\n") {
            it.joinToString(separator = "") {
                when (it) {
                    0 -> " "; 1 -> "#"; 2 -> " " else -> "$it"
                }
            }
        }
    }

    override fun solvePartTwo(input: List<String>): String {
        val layers = convert(input).chunked(WIDTH * HEIGHT)
        val message = List(WIDTH * HEIGHT) { idx ->
            for (l in layers) {
                if (l[idx] != 2) {
                    return@List l[idx]
                }
            }
            2
        }

        return "\n" + layerToString(message.chunked(WIDTH))
    }
}