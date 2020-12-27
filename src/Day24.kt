import kotlin.math.sqrt

class Day24: Day {
    private fun convert(input: List<String>) : IntArray {
        return input.map { line ->
            line.mapNotNull { c ->
                when (c) {
                    '.', '?' -> 0; '#' -> 1; else -> null
                }
            }.toIntArray()
        }.reduce { acc, ints -> acc + ints }
    }

    override fun solvePartOne(input: List<String>): Long {
        fun countAdjacentBugs(board: IntArray, pos: Int): Int {
            val sz = sqrt(board.size.toFloat()).toInt()
            return (if (pos - sz >= 0) board[pos - sz] else 0) +
                    (if (pos + sz < board.size) board[pos + sz] else 0) +
                    (if (pos % sz > 0) board[pos - 1] else 0) +
                    (if (pos % sz < sz - 1) board[pos + 1] else 0)
        }

        fun evolve(source: IntArray, dest: IntArray) {
            for (i in dest.indices) {
                dest[i] = when {
                    source[i] == 1 ->
                        if (countAdjacentBugs(source, i) == 1) 1 else 0
                    else ->
                        if (countAdjacentBugs(source, i) in 1..2) 1 else 0
                }
            }
        }

        fun boardRating(board: IntArray): Long {
            return board.foldIndexed(0L, { index, acc, i -> acc + i * 2.pow(index) })
        }

        var source = convert(input)
        var dest = source.copyOf()
        val seen = mutableListOf<Long>()
        var rating = boardRating(source)

        while (rating !in seen) {
            seen.add(rating)
            evolve(source, dest)
            rating = boardRating(dest)
            val tmp = source; source = dest; dest = tmp
        }
        return rating
    }

    override fun solvePartTwo(input: List<String>): Int {
        val board = convert(input)
        val emptyBoard = IntArray(board.size) { 0 }
        val sz = sqrt(board.size.toFloat()).toInt()
        val mid = board.size / 2
        val firstRowIdx = 0 until sz
        val lastRowIdx = (board.size - sz) until board.size
        val firstColIdx = 0 until board.size step sz
        val lastColIdx = (sz - 1) until board.size step sz

        fun countAdjacentBugs(board: List<IntArray>, lvl: Int, pos: Int): Int {
            return when {
                (pos in firstRowIdx) -> if (lvl - 1 >= 0) board[lvl - 1][mid - sz] else 0
                (pos == mid + sz) -> if (lvl + 1 < board.size) board[lvl + 1].slice(lastRowIdx).sum() else 0
                else -> board[lvl][pos - sz]
            } + when {
                (pos in lastRowIdx) -> if (lvl - 1 >= 0) board[lvl - 1][mid + sz] else 0
                (pos == mid - sz) -> if (lvl + 1 < board.size) board[lvl + 1].slice(firstRowIdx).sum() else 0
                else -> board[lvl][pos + sz]
            } + when {
                (pos in firstColIdx) -> if (lvl - 1 >= 0) board[lvl - 1][mid - 1] else 0
                (pos == mid + 1) -> if (lvl + 1 < board.size) board[lvl + 1].slice(lastColIdx).sum() else 0
                else -> board[lvl][pos - 1]
            } + when {
                (pos in lastColIdx) -> if (lvl - 1 >= 0) board[lvl - 1][mid + 1] else 0
                (pos == mid - 1) -> if (lvl + 1 < board.size) board[lvl + 1].slice(firstColIdx).sum() else 0
                else -> board[lvl][pos + 1]
            }
        }

        fun evolve(source: List<IntArray>, dest: List<IntArray>) {
            for (lvl in source.indices) {
                for (pos in source[lvl].indices) {
                    if (pos == source[lvl].size / 2) continue

                    dest[lvl][pos] = when {
                        source[lvl][pos] == 1 ->
                            if (countAdjacentBugs(source, lvl, pos) == 1) 1 else 0
                        else ->
                            if (countAdjacentBugs(source, lvl, pos) in 1..2) 1 else 0
                    }
                }
            }
        }

        board[board.size / 2] = 0
        var source = mutableListOf(board)
        var dest = mutableListOf(board.copyOf())

        for (m in 0 until 200) {
            // Add empty boards at beggining/end if there's a chance of evolving bugs there
            // in the next iteration. Make sure that dest has the same size
            if (source.first().any { it == 1 }) source.add(0, emptyBoard.copyOf())
            if (source.last().any { it == 1 }) source.add(emptyBoard.copyOf())
            for (i in 0 until source.size - dest.size) dest.add(emptyBoard.copyOf())

            evolve(source, dest)

            val tmp = source; source = dest; dest = tmp
        }

        return source.sumBy { it.sum() }
    }
}