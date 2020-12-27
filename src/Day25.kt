class Day25: Day {
    private fun convert(input: List<String>) : List<Long> {
        return input[0].split(",").map { it.toLong() }
    }

    private fun List<Long>.fromAscii() = this
            .map { it.toChar() }
            .joinToString("") { it.toString() }
            .trim()

    private fun String.toAscii(): List<Long> = this.map { it.toLong() }

    override fun solvePartOne(input: List<String>): String {
        val computer = Intcode(convert(input))

        // Navigate and get all items
        var commands: String = ("north,take tambourine,east,take astrolabe," +
                "south,take shell,north,east,north,take klein bottle,north,take easter egg," +
                "south,south,west,west,south,south,south,take hypercube,north,north," +
                "west,take dark matter,west,north,west,take coin,south,inv,")
                .replace(',', '\n')

        var prompt = computer.execute(commands.toAscii()).fromAscii()
//        println(prompt)

        // Drop all items before checkpoint
        val items = prompt.substringAfter("inventory:")
                .replace("- ","").trim().split("\n").dropLast(2)
        commands = items.joinToString("") { "drop $it\n" }
        prompt = computer.execute(commands.toAscii()).fromAscii()
//        println(prompt)

        for (bitmask in 0 until 2.pow(items.size)) {
            commands = items
                    .mapIndexedNotNull { idx, s -> if (bitmask and 2.pow(idx) != 0) "take $s\n" else null }
                    .joinToString("")
            commands += "south\n"
            prompt = computer.execute(commands.toAscii()).fromAscii()
//            println(prompt)

            if (!prompt.contains("lighter") && !prompt.contains("heavier")) break

            commands = items
                    .mapIndexedNotNull { idx, s -> if (bitmask and 2.pow(idx) != 0) "drop $s\n" else null }
                    .joinToString("")
            prompt = computer.execute(commands.toAscii()).fromAscii()
//            println(prompt)
        }
        return prompt.substringAfter("typing ").substringBefore(" ")

//        while (true) {
//            commands = readLine()?.plus('\n') ?: ""
//            out = computer.execute(commands.map { it.toLong() })
//            println(outputToStr(out))
//        }

    }

    override fun solvePartTwo(input: List<String>): String {
        return "Done"
    }
}