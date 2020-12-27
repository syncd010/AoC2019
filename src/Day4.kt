class Day4 : Day {
    private fun convert(input: List<String>): Pair<Int, Int> {
        val split = input[0].split("-")
        return Pair(split[0].toInt(), split[1].toInt())
    }

    private fun countPasswords(minVal: Int, maxVal: Int, sequenceRule: (List<Int>) -> Boolean): Int {
        val digits = minVal.toDigits()
        var count = 0

        // Rule: The value is within the range given in your puzzle input
        while (digitsToNum(digits) <= maxVal) {
            // Rules: Sorted and sequence Rule as provided
            if (digits.isSorted() && sequenceRule(digits))
                count++

            // Move to next possible number
            val idx = digits.indexOfLast { it != 9 }
            digits[idx]++
            digits.subList(idx + 1, digits.size).fill(digits[idx])
        }

        return count
    }

    override fun solvePartOne(input: List<String>): Int {
        val (minVal, maxVal) = convert(input)
        // Rule: Two adjacent digits are the same
        return countPasswords(minVal, maxVal) { it.zipWithNext { a, b -> a == b }.any { it } }
    }

    override fun solvePartTwo(input: List<String>): Int {
        val (minVal, maxVal) = convert(input)
        // Rule: Two adjacent digits are the same and the two adjacent matching digits are not part of a larger group of matching digits
        return countPasswords(minVal, maxVal) {
            val diff = it.zipWithNext { a, b -> a == b }
            for ((idx, b) in diff.withIndex()) {
                if (b && ((idx == 0) || !diff[idx - 1]) && ((idx == diff.size - 1) || !diff[idx + 1])) {
                    return@countPasswords true
                }
            }
            false
        }
    }
}