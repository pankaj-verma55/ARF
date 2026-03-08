package com.example.airavatresearchfoundation.di

import com.example.airavatresearchfoundation.data.repo.ProductRepository
import com.example.airavatresearchfoundation.data.service.ApiService
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepository(apiService: ApiService): ProductRepository {
        return ProductRepository(apiService)
    }
}