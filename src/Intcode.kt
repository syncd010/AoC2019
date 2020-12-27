/**
 * Expandable memory, which grows dynamically
 */
class Memory(initialState: List<Long>) {
    private var _mem = initialState.toLongArray()

    operator fun get(idx: Int): Long {
        return if (idx < _mem.size) _mem[idx] else 0
    }

    operator fun set(idx: Int, value: Long) {
        if (idx >= _mem.size)
            _mem = LongArray(idx + 100) { index -> get(index) }
        _mem[idx] = value
    }

    operator fun iterator(): Iterator<Long> = _mem.iterator()
}

class Intcode(initialState: List<Long>) {
    var mem = Memory(initialState)

    // Instruction pointer
    var ip = 0
        private set

    // Relative base
    var relBase = 0
        private set

    // Whether the program completed
    var halted = false
        private set

    private fun getParamValue(mode: Int, paramValue: Long): Long =
        when (mode) {
            0 -> mem[paramValue.toInt()]
            1 -> paramValue
            2 -> mem[paramValue.toInt() + relBase]
            else -> paramValue
        }

    private fun getAddrValue(mode: Int, addrValue: Long): Int =
        when (mode) {
            0 -> addrValue.toInt()
            2 -> addrValue.toInt() + relBase
            else -> addrValue.toInt()
        }

    private fun getMode(inst: List<Int>, paramNum: Int) = inst[3 - paramNum]

    data class ExecutionResult(val stop: Boolean, val consumedInput: Boolean, val output: Long?)

    fun executeNext(input: Long?): ExecutionResult {
        val inst = (listOf(0, 0, 0, 0) + mem[ip].toDigits()).takeLast(5)
        val opcode = digitsToNum(inst.slice(3..4))
        var stopExecution = false
        var consumedInput = false
        var output: Long? = null
        when (opcode) {
            1, 2 -> { // +, *
                val param1 = getParamValue(getMode(inst, 1), mem[ip + 1])
                val param2 = getParamValue(getMode(inst, 2), mem[ip + 2])
                val param3 = getAddrValue(getMode(inst, 3), mem[ip + 3])
                mem[param3] = if (opcode == 1) param1 + param2 else param1 * param2
                ip += 4
            }
            3 -> { // Input
                val param1 = getAddrValue(getMode(inst, 1), mem[ip + 1])
                if (input != null) {
                    mem[param1] = input
                    consumedInput = true
                    ip += 2
                } else {
                    // Don't have input, stop
                    stopExecution = true
                }
            }
            4 -> { // Output
                output = getParamValue(getMode(inst, 1), mem[ip + 1])
                ip += 2
            }
            5, 6 -> { // JNZ, JEZ
                val param1 = getParamValue(getMode(inst, 1), mem[ip + 1])
                val param2 = getParamValue(getMode(inst, 2), mem[ip + 2])
                ip = when {
                    (opcode == 5) && (param1 != 0L) -> param2.toInt()
                    (opcode == 6) && (param1 == 0L) -> param2.toInt()
                    else -> ip + 3
                }
            }
            7, 8 -> { // LT, EQ
                val param1 = getParamValue(getMode(inst, 1), mem[ip + 1])
                val param2 = getParamValue(getMode(inst, 2), mem[ip + 2])
                val param3 = getAddrValue(getMode(inst, 3), mem[ip + 3])
                mem[param3] = when {
                    ((opcode == 7) && (param1 < param2)) || ((opcode == 8) && (param1 == param2)) -> 1
                    else -> 0
                }
                ip += 4
            }
            9 -> { // Adjust Relative Base
                val param1 = getParamValue(getMode(inst, 1), mem[ip + 1])
                relBase += param1.toInt()
                ip += 2
            }
            99 -> {
                halted = true
                stopExecution = true
            }
            else -> {
                println("Unknown opcode: ${mem[ip]} at position $ip ")
                stopExecution = true
            }
        }
        return ExecutionResult(stopExecution, consumedInput, output)
    }

    /**
     * Executes the program in memory, consuming from [input] as needed, and returning the output
     * If more input than provided is needed [askForInput] controls whether this function
     * returns or explicitly asks the user for more input
     */
    fun execute(input: List<Long>? = null): List<Long> {
        var inputIdx = 0
        val output = mutableListOf<Long>()

        loop@ while (true) {
            val inputVal = if (input != null && inputIdx < input.size) input[inputIdx] else null
            val res = executeNext(inputVal)
            if (res.consumedInput) inputIdx++
            if (res.output != null) output.add(res.output)
            if (res.stop) break@loop
        }
        return output
    }

    fun reset(state: List<Long>) {
        mem = Memory(state)
        ip = 0
        relBase = 0
    }
}