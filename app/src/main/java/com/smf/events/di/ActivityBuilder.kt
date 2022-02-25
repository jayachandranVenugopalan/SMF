package com.example.demodragger.di

import com.smf.events.MainActivity
import com.smf.events.MainModule
import com.smf.events.ui.businessregistration.BusinessRegistrationFragment
import com.smf.events.ui.businessregistration.BusinessRegistrationModule
import com.smf.events.ui.dashboard.DashBoardFragment
import com.smf.events.ui.dashboard.DashBoardModule
import com.smf.events.ui.emailotp.EmailOTPFragment
import com.smf.events.ui.emailotp.EmailOTPModule
import com.smf.events.ui.signin.SignInFragment
import com.smf.events.ui.signin.SignInModule
import com.smf.events.ui.signup.SignUpFragment
import com.smf.events.ui.signup.SignUpModule
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

    @ContributesAndroidInjector(modules = [SignUpModule::class])
    abstract fun provideSignUpFragment():SignUpFragment

    @ContributesAndroidInjector(modules = [EmailOTPModule::class])
    abstract fun provideEmailOTPFragment():EmailOTPFragment

    @ContributesAndroidInjector(modules = [DashBoardModule::class])
    abstract fun provideDashBoardFragment():DashBoardFragment

    @ContributesAndroidInjector(modules = [BusinessRegistrationModule::class])
    abstract fun provideBusinessRegistrationFragment():BusinessRegistrationFragment
}