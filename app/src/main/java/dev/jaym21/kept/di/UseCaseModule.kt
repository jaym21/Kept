package dev.jaym21.kept.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jaym21.kept.domain.usecase.GetHoldingsUseCase
import dev.jaym21.kept.domain.usecase.GetHoldingsUseCaseImpl
import dev.jaym21.kept.domain.usecase.ImportContractNoteUseCase
import dev.jaym21.kept.domain.usecase.ImportContractNoteUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindImportContractNoteUseCase(
        impl: ImportContractNoteUseCaseImpl
    ): ImportContractNoteUseCase

    @Binds
    @Singleton
    abstract fun bindGetHoldingsUseCase(
        impl: GetHoldingsUseCaseImpl
    ): GetHoldingsUseCase
}