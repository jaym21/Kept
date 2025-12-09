package dev.jaym21.kept.domain.usecase

import dev.jaym21.kept.data.pdf.PDFTextExtractor
import dev.jaym21.kept.data.pdf.parsers.BrokerParserSelector
import dev.jaym21.kept.domain.model.Trade
import dev.jaym21.kept.domain.repository.TradeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ImportContractNoteUseCase {

    sealed interface ImportContractNoteResult {

        data class Success(val tradesImported: Int, val brokerName: String) : ImportContractNoteResult

        data object UnknownBroker : ImportContractNoteResult

        data class NoTrades(val brokerName: String) : ImportContractNoteResult

        data class Error(val message: String) : ImportContractNoteResult
    }
    suspend fun import(filePath: String): ImportContractNoteResult
}

class ImportContractNoteUseCaseImpl @Inject constructor(
    private val pdfBoxExtractor: PDFTextExtractor,
    private val brokerParserSelector: BrokerParserSelector,
    private val tradeRepository: TradeRepository
): ImportContractNoteUseCase {

    override suspend fun import(filePath: String): ImportContractNoteUseCase.ImportContractNoteResult
    = withContext(Dispatchers.IO){
        return@withContext try {
            val text = pdfBoxExtractor.extractTextFromFile(filePath)

            val parser = brokerParserSelector.selectParserFor(text)
                ?: return@withContext ImportContractNoteUseCase.ImportContractNoteResult.UnknownBroker

            val trades: List<Trade> = parser.parse(text)
            if (trades.isEmpty()) {
                return@withContext ImportContractNoteUseCase.ImportContractNoteResult.NoTrades(
                    brokerName = parser.javaClass.simpleName.replace("PDFParser", "")
                )
            }

            tradeRepository.insertTrades(trades)
            ImportContractNoteUseCase.ImportContractNoteResult.Success(
                tradesImported = trades.size,
                brokerName = parser.javaClass.simpleName.replace("PDFParser", "")
            )

        } catch (e: Exception) {
            ImportContractNoteUseCase.ImportContractNoteResult.Error(
                message = e.message ?: "Failed to import contract note"
            )
        }
    }
}

