package dev.jaym21.kept.data.pdf

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PDFBoxTextExtractor: PDFTextExtractor {

    override suspend fun extractTextFromFile(filePath: String): String = withContext(Dispatchers.IO) {
        val file = File(filePath)
        PDDocument.load(file).use { document ->
            val stripper = PDFTextStripper()
            stripper.getText(document)
        }
    }
}