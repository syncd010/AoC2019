class Day16: Day {
    private fun convert(input: List<String>) : List<Int> {
        return input[0].map { it - '0' }
    }

    override fun solvePartOne(input: List<String>): Int {
        var signal = convert(input)
        val basePattern = listOf(0, 1, 0, -1)

        val newSignal = MutableList(signal.size) { '0' }

        for (phase in 1..100) {
            for (i in signal.indices) {
                val pattern = basePattern
                        .map { value -> List(i + 1) { value } }
                        .flatten()
                val patternSeq = generateSequence(1) { (it + 1) % pattern.size }
                        .take(signal.size)
                        .map { pattern[it] }.toList()

                newSignal[i] = signal.zip(patternSeq).sumBy { (a, b) -> a * b }.toString().last()
            }
            signal = newSignal.map { it - '0' }
        }

        return digitsToNum(signal.take(8))
    }

    override fun solvePartTwo(input: List<String>): Int {
        val inputSignal = convert(input).toMutableList()
        val startIdx = digitsToNum(inputSignal.take(7))
        val repeatCount = 10000

        // This solution doesn't work for small indexes...
        if (startIdx < (inputSignal.size * repeatCount) / 2) return -1

        val signal = List(repeatCount) { inputSignal }.flatten().drop(startIdx).toIntArray()
        val signalSum = IntArray(signal.size) { 0 }
        for (phase in 1..100) {
            signalSum[0] = signal.sum()
            for (idx in 1 until signal.size)
                signalSum[idx] = signalSum[idx - 1] - signal[idx - 1]
            for (idx in signal.indices) signal[idx] = signalSum[idx] % 10
        }

        return digitsToNum(signal.take(8))
    }
}