package com.gwolf.coffeetea.di

import android.content.Context
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.repository.DataStoreRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.AuthRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.CartRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.CategoryRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.FavoriteRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.ProductRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.ProfileRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.PromotionRepositoryImpl
import com.gwolf.coffeetea.domain.repository.local.DataStoreRepository
import com.gwolf.coffeetea.domain.repository.remote.AuthRepository
import com.gwolf.coffeetea.domain.repository.remote.CartRepository
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.domain.repository.remote.PromotionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: Auth
    ): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun providePromotionRepository(
        storage: Storage,
        postgrest: Postgrest,
        localDatabase: LocalDatabase
    ): PromotionRepository = PromotionRepositoryImpl(storage, postgrest, localDatabase)

    @Provides
    @Singleton
    fun provideCategoryRepository(
        storage: Storage,
        postgrest: Postgrest,
        localDatabase: LocalDatabase
    ): CategoryRepository = CategoryRepositoryImpl(storage, postgrest, localDatabase)

    @Provides
    @Singleton
    fun provideProductRepository(
        auth: Auth,
        storage: Storage,
        postgrest: Postgrest,
        localDatabase: LocalDatabase
    ): ProductRepository = ProductRepositoryImpl(auth, storage, postgrest, localDatabase)

    @Provides
    @Singleton
    fun provideProfileRepository(
        postgrest: Postgrest,
        storage: Storage,
        auth: Auth
    ): ProfileRepository = ProfileRepositoryImpl(postgrest, storage, auth)

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        auth: Auth,
        storage: Storage,
        postgrest: Postgrest,
        localDatabase: LocalDatabase
    ): FavoriteRepository = FavoriteRepositoryImpl(auth, storage, postgrest, localDatabase)

    @Provides
    @Singleton
    fun provideCartRepository(
        postgrest: Postgrest,
        auth: Auth
    ): CartRepository = CartRepositoryImpl(postgrest, auth)
}