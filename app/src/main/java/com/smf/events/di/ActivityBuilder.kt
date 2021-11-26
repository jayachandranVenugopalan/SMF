package com.example.demodragger.di

import com.smf.events.MainActivity
import com.smf.events.MainModule
import com.smf.events.ui.signin.SignInFragment
import com.smf.events.ui.signin.SignInModule
import com.smf.events.ui.splash.SplashFragment
import com.smf.events.ui.splash.SplashModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun provideSplashFragment(): SplashFragment

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun provideSignInFragment():SignInFragment
}