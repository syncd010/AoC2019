class Day23: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    override fun solvePartOne(input: List<String>): Long {
        val mem = convert(input)
        val sz = 50
        val computers = Array(sz) { Intcode(mem.toList()) }
        val inVals = Array(sz) { i -> mutableListOf(i.toLong()) }
        val outVals = Array(sz) { mutableListOf<Long>() }

        while(true) {
            for (i in 0 until sz) {
                if (computers[i].halted) continue

                val inputVal = if (inVals[i].isNotEmpty()) inVals[i].first() else -1L
                val res = computers[i].executeNext(inputVal)
                if (res.consumedInput && inVals[i].isNotEmpty()) inVals[i].removeAt(0)

                if (res.output != null) {
                    outVals[i].add(res.output)

                    if (outVals[i].size == 3) {
                        val addr = outVals[i].first().toInt()
                        if (addr == 255) return res.output
                        if (addr < sz) inVals[addr].addAll(outVals[i].drop(1))
                        outVals[i].clear()
                    }
                }
            }
        }
    }

    override fun solvePartTwo(input: List<String>): Long {
        val mem = convert(input)
        val sz = 50
        val computers = Array(sz) { Intcode(mem.toList()) }
        val inVals = Array(sz) { i -> mutableListOf(i.toLong()) }
        val outVals = Array(sz) { mutableListOf<Long>() }
        val natPacket = mutableListOf<Long>()
        val lastIOWasReceive = Array(sz) { false }
        var natLastY: Long? = null
        var resuming = false

        while(true) {
            // Check if idle
            if (inVals.all { it.isEmpty() } && lastIOWasReceive.all { it } && natPacket.isNotEmpty() && !resuming) {
                inVals[0].addAll(natPacket)
                if (natLastY != null && natLastY == natPacket[1]) return natLastY
                natLastY = natPacket[1]
                resuming = true
            }

            for (i in 0 until sz) {
                if (computers[i].halted) continue

                val inputVal = if (inVals[i].isNotEmpty()) inVals[i].first() else -1L
                val res = computers[i].executeNext(inputVal)
                if (res.consumedInput && inVals[i].isNotEmpty()) inVals[i].removeAt(0)
                lastIOWasReceive[i] = (res.output == null) && (res.consumedInput || lastIOWasReceive[i])

                if (res.output != null) {
                    resuming = false
                    outVals[i].add(res.output!!)

                    if (outVals[i].size == 3) {
                        val addr = outVals[i].first().toInt()
                        if (addr == 255) {
                            natPacket.clear()
                            natPacket.addAll(outVals[i].drop(1))
                        } else if (addr < sz) {
                            inVals[addr].addAll(outVals[i].drop(1))
                        }
                        outVals[i].clear()
                    }
                }
            }
        }
    }
}