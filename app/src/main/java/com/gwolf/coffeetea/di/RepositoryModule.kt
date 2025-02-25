package com.gwolf.coffeetea.di

import android.content.Context
import com.gwolf.coffeetea.data.local.repository.DataStoreRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.api.NovaPostRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.AddressRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.AuthRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.CartRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.CategoryRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.FavoriteRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.ProductRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.ProfileRepositoryImpl
import com.gwolf.coffeetea.data.remote.repository.supabase.PromotionRepositoryImpl
import com.gwolf.coffeetea.domain.repository.local.DataStoreRepository
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.CategoryRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.PromotionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
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
        postgrest: Postgrest
    ): PromotionRepository = PromotionRepositoryImpl(postgrest)

    @Provides
    @Singleton
    fun provideCategoryRepository(
        postgrest: Postgrest
    ): CategoryRepository = CategoryRepositoryImpl(postgrest)

    @Provides
    @Singleton
    fun provideProductRepository(
        auth: Auth,
        postgrest: Postgrest
    ): ProductRepository = ProductRepositoryImpl(auth, postgrest)

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
        postgrest: Postgrest
    ): FavoriteRepository = FavoriteRepositoryImpl(auth, postgrest)

    @Provides
    @Singleton
    fun provideCartRepository(
        postgrest: Postgrest,
        auth: Auth
    ): CartRepository = CartRepositoryImpl(postgrest, auth)

    @Provides
    @Singleton
    fun provideNovaPostRepository(
        httpClient: HttpClient
    ): NovaPostRepository = NovaPostRepositoryImpl(httpClient)

    @Provides
    @Singleton
    fun provideAddressRepository(
        postgrest: Postgrest,
        auth: Auth
    ): AddressRepository = AddressRepositoryImpl(postgrest, auth)
}