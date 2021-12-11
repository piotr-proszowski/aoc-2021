package eu.proszkie.adventofcode.day10

class SyntaxScoring {

    private val errorScoring: Map<Char, Long> = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )

    private val autocompleteScoring: Map<Char, Long> = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4,
    )

    private val correspondingSigns: Map<Char, Char> = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )

    private val closingSigns = "]}>)"

    fun scoreLineAutocomplete(line: String): Long? {
        val withoutMatchingParenthesis = withoutMatchingParenthesis(line)
        if(firstClosingSign(withoutMatchingParenthesis) != null) {
            return null
        }
        return withoutMatchingParenthesis.mapNotNull { correspondingSigns[it] }.reversed()
            .mapNotNull { autocompleteScoring[it] }
            .fold(0L) { acc, i ->
                acc * 5 + i
            }
    }

    fun scoreLineErrors(line: String): Long{
        val withoutMatchingParenthesis = withoutMatchingParenthesis(line)
        val firstInvalidClosingSign = firstClosingSign(withoutMatchingParenthesis)
        return firstInvalidClosingSign?.let { errorScoring[it]!! } ?: 0L
    }

    private fun firstClosingSign(line: String): Char? {
        return line.firstOrNull { closingSigns.contains(it) }
    }

    private fun withoutMatchingParenthesis(line: String): String {
        if (doesNotHaveMatchingParenthesis(line)) {
            return line
        }

        val withoutParenthesis = line.replace("()", "")
            .replace("<>", "")
            .replace("{}", "")
            .replace("[]", "")

        return withoutMatchingParenthesis(withoutParenthesis)
    }

    private fun doesNotHaveMatchingParenthesis(line: String): Boolean {
        return !line.contains("<>") && !line.contains("[]") && !line.contains("{}") && !line.contains("()")
    }
}