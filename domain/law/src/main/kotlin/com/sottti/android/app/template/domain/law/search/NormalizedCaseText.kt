package com.sottti.android.app.template.domain.law.search

import java.text.Normalizer

/**
 * Texto do relato do caso já normalizado (minúsculas, sem acentos nem pontuação).
 *
 * A comparação usa tokens e tolera variações morfológicas simples, como
 * "ameaça"/"ameaçou" ou "furto"/"furtou".
 */
internal class NormalizedCaseText(rawValue: String) {

    val normalizedValue: String = normalize(rawValue)

    private val tokens: List<String> = normalizedValue
        .split(TOKEN_SEPARATOR)
        .filter(String::isNotEmpty)

    fun mentions(term: String): Boolean {
        val termTokens = normalize(term)
            .split(TOKEN_SEPARATOR)
            .filter(String::isNotEmpty)

        if (termTokens.isEmpty()) return false

        return termTokens.all { termToken ->
            tokens.any { token -> matches(token = token, termToken = termToken) }
        }
    }

    private fun matches(token: String, termToken: String): Boolean {
        val shortestLength = minOf(token.length, termToken.length)
        val sharesPrefix = shortestLength >= MINIMUM_PREFIX_LENGTH &&
            commonPrefixLength(token, termToken) >= shortestLength - PREFIX_TOLERANCE

        return token == termToken || sharesPrefix
    }

    private fun commonPrefixLength(first: String, second: String): Int =
        first.zip(second).takeWhile { (left, right) -> left == right }.size

    private companion object {
        private const val MINIMUM_PREFIX_LENGTH = 4
        private const val PREFIX_TOLERANCE = 1
        private val TOKEN_SEPARATOR = Regex(" +")
        private val ACCENTS = Regex("\\p{Mn}+")
        private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

        fun normalize(value: String): String =
            Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
                .replace(ACCENTS, "")
                .replace(NON_ALPHANUMERIC, " ")
                .trim()
    }
}
