package dev.jaym21.kept.data.pdf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class PdfBoxExtractorInstrumentedTest {

    @Test
    fun pdf_extraction_returns_non_empty_text() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val samplePdfPath = "contract_notes/Contract_Note.pdf"
        val cacheFile = File(context.cacheDir, "test_contract_note.pdf")

        context.assets.open(samplePdfPath).use { input ->
            FileOutputStream(cacheFile).use { output ->
                input.copyTo(output)
            }
        }

        assertTrue("Test PDF found/copied", cacheFile.exists())

        val extractor = PDFBoxTextExtractor()
        PDFBoxResourceLoader.init(context)
        val text = extractor.extractTextFromFile(cacheFile.absolutePath)

        assertTrue("Extracted text should not be blank", text.isNotBlank())

        val header = text.take(2000).uppercase()
        assertTrue(
            "Extracted PDF is from Zerodha Contract Note",
            header.contains("ZERODHA") && header.contains("CONTRACT NOTE")
        )
    }
}