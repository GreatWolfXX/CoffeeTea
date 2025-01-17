package com.gwolf.coffeetea.data.remote.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import com.gwolf.coffeetea.data.local.database.remotemediator.CategoryRemoteMediator
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase
) : CategoryRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getCategories(): Flow<PagingData<LocalCategoryEntity>> {
        val pagingSourceFactory = { localDatabase.categoryDao.getCategories() }

        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = CategoryRemoteMediator(storage, postgrest, localDatabase),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}