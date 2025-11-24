package dev.jaym21.kept.data.pdf.parsers

import dev.jaym21.kept.domain.model.Trade

interface BrokerPDFParser {

    fun canParse(extractedText: String): Boolean
    fun parse(extractedText: String): List<Trade>
}