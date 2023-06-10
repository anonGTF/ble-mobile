package com.jamal.blescanner.di

import com.jamal.blescanner.data.preferences.Preferences
import com.jamal.blescanner.data.remote.BaseApi
import com.jamal.blescanner.data.remote.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApi(): BaseApi = RetrofitInstance().createApi()

    @Singleton
    @Provides
    fun providePreferences(): Preferences = Preferences.instance
}