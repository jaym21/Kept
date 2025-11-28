package dev.jaym21.kept.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.jaym21.kept.data.pdf.PDFBoxTextExtractor
import dev.jaym21.kept.data.pdf.PDFTextExtractor
import dev.jaym21.kept.data.pdf.parsers.BrokerPDFParser
import dev.jaym21.kept.data.pdf.parsers.BrokerParserSelector
import dev.jaym21.kept.data.pdf.parsers.BrokerParserSelectorImpl
import dev.jaym21.kept.data.pdf.parsers.ZerodhaPDFParser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PdfParsingModule {

    @Binds
    @Singleton
    abstract fun bindPdfTextExtractor(
        impl: PDFBoxTextExtractor
    ): PDFTextExtractor

    @Binds
    @Singleton
    abstract fun bindBrokerParserSelector(
        impl: BrokerParserSelectorImpl
    ): BrokerParserSelector

    @Binds
    @IntoSet
    abstract fun bindZerodhaParser(
        parser: ZerodhaPDFParser
    ): BrokerPDFParser

}