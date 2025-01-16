package com.gwolf.coffeetea.di

import android.app.Application
import androidx.room.Room
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideLocalDataBase(app: Application): LocalDatabase {
        return Room.databaseBuilder(
            app,
            LocalDatabase::class.java,
            "CoffeeAndTeaLocalDB"
        )
            .build()
    }

}