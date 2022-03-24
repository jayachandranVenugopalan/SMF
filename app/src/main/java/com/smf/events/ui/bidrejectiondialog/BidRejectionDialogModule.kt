package com.smf.events.ui.bidrejectiondialog

import androidx.lifecycle.ViewModelProvider
import com.smf.events.helper.ViewModelProviderFactory
import com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialogViewModel
import dagger.Module
import dagger.Provides

@Module
class BidRejectionDialogModule {
    @Provides
    fun provideViewModelProvider(viewModel: BidRejectionDialogViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}