package com.gwolf.coffeetea.data.remote.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity
import com.gwolf.coffeetea.data.local.database.remotemediator.PromotionRemoteMediator
import com.gwolf.coffeetea.domain.repository.remote.PromotionRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase
) : PromotionRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPromotions(): Flow<PagingData<LocalPromotionEntity>> {
        val pagingSourceFactory = { localDatabase.promotionDao.getPromotions() }

        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = PromotionRemoteMediator(storage, postgrest, localDatabase),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}