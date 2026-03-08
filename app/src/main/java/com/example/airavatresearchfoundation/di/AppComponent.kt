package com.example.airavatresearchfoundation.di

import com.example.airavatresearchfoundation.ui.activity.FavoriteActivity
import com.example.airavatresearchfoundation.ui.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(favoriteActivity: FavoriteActivity)

}