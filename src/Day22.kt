import java.math.BigInteger

class Day22: Day {
    enum class Oper {CUT, DEAL, REVERSE}
    data class Instruction(val op: Oper, val arg: Long)

    fun convert(input: List<String>, sz: Long) : List<Instruction> {
        return input.mapNotNull { inst ->
            when {
                inst.startsWith("cut") ->
                    Instruction(Oper.CUT, (inst.substringAfter("cut ").toLong() + sz) % sz)
                inst.startsWith("deal with increment") ->
                    Instruction(Oper.DEAL, inst.substringAfter("increment ").toLong())
                inst.startsWith("deal into new stack") ->
                    Instruction(Oper.REVERSE, 0)
                else -> null
            }
        }
    }

    override fun solvePartOne(input: List<String>): Int {
        val sz = 10007L
        val instructions = convert(input, sz)
        var deck = Array(sz.toInt()) { it }

        instructions.forEach { inst ->
            when (inst.op) {
                Oper.CUT -> {
                    deck = deck.sliceArray(inst.arg.toInt() until sz.toInt()) + deck.sliceArray(0 until inst.arg.toInt())
                }
                Oper.DEAL -> {
                    val oldDeck = deck.copyOf()
                    for (i in deck.indices)
                        deck[((i * inst.arg) % sz).toInt()] = oldDeck[i]
                }
                Oper.REVERSE -> {
                    deck.reverse()
                }
            }
        }
        return deck.indexOf(2019)
    }

    fun unshufflePos(startPos: BigInteger, sz: BigInteger, instructions: List<Instruction>): BigInteger {
        var pos = startPos
        instructions.asReversed().forEach { inst ->
            val n = BigInteger.valueOf(inst.arg)
            pos = when (inst.op) {
                Oper.CUT -> (pos + n).mod(sz)
                Oper.DEAL -> (n.modInverse(sz) * pos).mod(sz)
                Oper.REVERSE -> sz - pos - BigInteger.ONE
            }
        }
        return pos
    }

    override fun solvePartTwo(input: List<String>): Long {
        // Couldn't solve this. Implemented solution from:
        // https://www.reddit.com/r/adventofcode/comments/ee0rqi/2019_day_22_solutions/fbnifwk/
        val sz = BigInteger.valueOf(119315717514047)
        val instructions = convert(input, sz.toLong())

        // Shuffle is linear, so there exists A and B such that shuffle(x) = A*x + B (in modulo sz)
        // So, find f1 = shuffle(x) and f2 = shuffle(f1), f1 = A*x + B, f2 = A*f1 + B
        // f1 - f2 = A*(x - f1) thus A = (f1 - f2)/(x - f1) and B = f1 - A*x
        val x = BigInteger.ZERO
        val f1 = unshufflePos(x, sz, instructions)
        val f2 = unshufflePos(f1, sz, instructions)
        val a = ((f1 - f2) * (x - f1).modInverse(sz)).mod(sz)
        val b = (f1 - a * x).mod(sz)

        // Now to apply n times f:
        // f^n(x) = A^n*x + A^(n-1)*B + A^(n-2)*B + ... B
        //        = A^n*x + (A^(n-1) + A^(n-2) + ... 1) * B
        //        = A^n*x + (A^n-1) / (A - 1) * B
        val pos = BigInteger.valueOf(2020L)
        val iter = BigInteger.valueOf(101741582076661)

        val an = a.modPow(iter, sz)
        return ((an * pos + (an - BigInteger.ONE) * (a - BigInteger.ONE).modInverse(sz) * b).mod(sz)).toLong()
    }
}