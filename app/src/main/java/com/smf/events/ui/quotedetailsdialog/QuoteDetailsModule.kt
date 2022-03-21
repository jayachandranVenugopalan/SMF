package com.smf.events.ui.quotedetailsdialog

import androidx.lifecycle.ViewModelProvider
import com.smf.events.helper.ViewModelProviderFactory
import com.smf.events.ui.quotebrief.QuoteBriefViewModel
import dagger.Module
import dagger.Provides

@Module
class QuoteDetailsModule {
    @Provides
    fun provideViewModelProvider(viewModel: QuoteDetailsDialogViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}