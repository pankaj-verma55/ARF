package com.example.airavatresearchfoundation.di

import com.example.airavatresearchfoundation.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

}