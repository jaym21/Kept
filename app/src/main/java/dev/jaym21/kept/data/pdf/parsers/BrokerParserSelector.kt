package dev.jaym21.kept.data.pdf.parsers

import javax.inject.Inject

interface BrokerParserSelector {
    fun selectParserFor(text: String): BrokerPDFParser?
}

class BrokerParserSelectorImpl @Inject constructor(
    private val parsers: Set<@JvmSuppressWildcards BrokerPDFParser>
) : BrokerParserSelector {

    override fun selectParserFor(text: String): BrokerPDFParser? {
        return parsers.firstOrNull { it.canParse(text) }
    }
}