import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

/**
 * Position class with helper functions
 */
data class Position(var x: Int, var y: Int = 0, var z: Int = 0) {
    operator fun plus(other: Position): Position =
        Position(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Position): Position =
        Position(x - other.x, y - other.y, z - other.z)

    operator fun unaryMinus(): Position =
        Position(-x, -y, -z)

    fun sign() : Position =
        Position(x.sign, y.sign, z.sign)
}

fun manhattanDist(pos1: Position, pos2: Position): Int =
    abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y) + abs(pos1.z - pos2.z)

typealias Velocity = Position

/**
 * Integer power
 */
infix fun Int.pow(exponent: Int): Int =
    toDouble().pow(exponent).toInt()

fun ceil(a: Int, b: Int): Int =
        (a / b) + (if (a % b > 0) 1 else 0)

fun ceil(a: Long, b: Long): Long =
        (a / b) + (if (a % b > 0) 1 else 0)

infix fun Long.pow(exponent: Long): Long =
        toDouble().pow(exponent.toDouble()).toLong()

/**
 * Returns the Greatest Common Divisor of [a] and [b], using Euclid's division algorithm
 * https://en.wikipedia.org/wiki/Euclidean_algorithm
 */
tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

/**
 * Returns the digits of this int as a [MutableList]
 */
fun Long.toDigits(): MutableList<Int> {
    val res = mutableListOf<Int>()
    var aux = this
    while (aux > 0) {
        res.add((aux % 10).toInt())
        aux /= 10
    }
    return res.asReversed()
}

fun Int.toDigits(): MutableList<Int> {
    return this.toLong().toDigits()
}

/**
 * Returns the number obtained by concatenating [digits]
 */
fun digitsToNum(digits: List<Int>): Int =
    digits.reversed().mapIndexed { index, i -> i * 10.pow(index) }.sum()


fun <T: Comparable<T>> List<T>.isSorted() : Boolean =
    this.zipWithNext { a: T, b: T -> a <= b }.all{ it }

/**
 *  Returns all permutations of elements from list. These are different ways to arrange elements from this list.
 */
fun <T> List<T>.permutations(): Set<List<T>> = when {
    isEmpty() -> setOf()
    size == 1 -> setOf(listOf(get(0)))
    else -> {
        val element = get(0)
        drop(1).permutations<T>()
            .flatMap { sublist -> (0..sublist.size).map { i -> sublist.plusAt(i, element) } }
            .toSet()
    }
}

private fun <T> List<T>.plusAt(index: Int, element: T): List<T> = when (index) {
    !in 0..size -> throw Error("Cannot put at index $index because size is $size")
    0 -> listOf(element) + this
    size -> this + element
    else -> dropLast(size - index) + element + drop(index)
}


/**
 *  Returns all combinations of elements in set
 */
fun <T> Set<T>.combinations(combinationSize: Int): Set<Set<T>> = when {
    combinationSize < 0 -> throw Error("combinationSize cannot be smaller then 0. It is equal to $combinationSize")
    combinationSize == 0 -> setOf(setOf())
    combinationSize >= size -> setOf(toSet())
    else -> powerset()
        .filter { it.size == combinationSize }
        .toSet()
}

fun <T> Collection<T>.powerset(): Set<Set<T>> = powerset(this, setOf(setOf()))

private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> = when {
    left.isEmpty() -> acc
    else ->powerset(left.drop(1), acc + acc.map { it + left.first() })
}


