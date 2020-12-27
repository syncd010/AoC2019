class Day7: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Long {
        val initialState = convert((input))

        val res = emptyMap<Long, List<Int>>().toMutableMap()
        for (phase in listOf(0,1,2,3,4).permutations()) {
            var inputSignal= 0L
            for (elem in phase) {
                val computer = Intcode(initialState)
                inputSignal = computer.execute(listOf(elem.toLong(), inputSignal)).first()
                res[inputSignal] = phase
            }
        }

        return res.keys.max() ?: -1
    }

    override fun solvePartTwo(input: List<String>): Long {
        val initialState = convert((input))

        val res = emptyMap<Long, List<Int>>().toMutableMap()
        for (phase in listOf(5,6,7,8,9).permutations()) {
            val amplifiers = mutableListOf<Intcode>()
            val inputSignal = mutableListOf<MutableList<Long>>()
            for (elem in phase) {
                amplifiers.add(Intcode(initialState))
                inputSignal.add(mutableListOf(elem.toLong()))
            }
            inputSignal[0].add(0)

            var idx = 0
            var output = 0L
            while (!amplifiers.last().halted) {
                output = amplifiers[idx].execute(inputSignal[idx]).first()
                inputSignal[idx].clear()

                idx = (idx + 1) % amplifiers.size
                inputSignal[idx].add(output)
            }
            res[output] = phase
        }

        return res.keys.max() ?: -1
    }
}