package com.example.oldphotorestorationapplication.dependencyinjection

import com.example.oldphotorestorationapplication.network.RestorationNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideRestorationNetwork() = RestorationNetwork()
}