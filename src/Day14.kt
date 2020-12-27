class Day14: Day {
    data class ChemSpec(val element: String, var quantity: Long)

    private fun convert(input: List<String>) : Map<ChemSpec, List<ChemSpec>> {
        return input.associate { line: String ->
            val spec = line.split(" => ")
            val outElement = spec[1].split(" ")
            val inList = spec[0].split(", ")

            Pair(ChemSpec(outElement[1], outElement[0].toLong()), inList.map {
                val inElement = it.split(" ")
                ChemSpec(inElement[1], inElement[0].toLong()) })
        }
    }

    private fun calcOre(reactions: Map<ChemSpec, List<ChemSpec>>,
                        goal: ChemSpec,
                        initialSurplus: Map<String, Long>): Pair<Long, Map<String, Long>> {
        val surplus = initialSurplus.toMutableMap()
        val toProduce = mutableListOf(goal)
        var totalOre = 0L

        while (toProduce.size > 0) {
            val product = toProduce.removeAt(0)

            if (product.element == "ORE") {
                totalOre += product.quantity
                continue
            }
            if (product.quantity <= surplus[product.element] ?: 0) {
                surplus[product.element] = surplus[product.element]!! - product.quantity
                continue
            }

            product.quantity -= (surplus[product.element] ?: 0)

            val recipeKey = reactions.keys.find { it.element == product.element }!!
            val batches = ceil(product.quantity, recipeKey.quantity)
            surplus[product.element] = batches * recipeKey.quantity - product.quantity
            toProduce.addAll(reactions[recipeKey]!!.map {
                ChemSpec(it.element, batches * it.quantity)
            })
        }
        return Pair(totalOre, surplus)
    }

    override fun solvePartOne(input: List<String>): Long {
        return calcOre(convert(input), ChemSpec("FUEL", 1), emptyMap()).first
    }

    override fun solvePartTwo(input: List<String>): Long {
        val reactions = convert(input)

        var surplus = emptyMap<String, Long>()
        var totalOre = 1000000000000L
        var fuel = 0L
        var goalFuel = 100000L

        while (goalFuel > 0) {
            val (usedOre, newSurplus) = calcOre(reactions, ChemSpec("FUEL", goalFuel), surplus)
            if (usedOre <= totalOre) {
                totalOre -= usedOre
                surplus = newSurplus
                fuel += goalFuel
            } else {
                goalFuel /= 2
            }
        }

        return fuel
    }
}