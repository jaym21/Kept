package dev.jaym21.kept.data.pdf

interface PDFTextExtractor {

    suspend fun extractTextFromFile(filePath: String): String
}