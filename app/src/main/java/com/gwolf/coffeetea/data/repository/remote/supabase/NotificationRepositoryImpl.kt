package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.NotificationDto
import com.gwolf.coffeetea.data.toListNotificationDomain
import com.gwolf.coffeetea.domain.entities.Notification
import com.gwolf.coffeetea.domain.repository.remote.supabase.NotificationRepository
import com.gwolf.coffeetea.util.NOTIFICATIONS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
) : NotificationRepository {

    override fun getNotifications(): Flow<List<Notification>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(NOTIFICATIONS_TABLE)
                .select(Columns.raw("*")) {
                    filter {
                        or {
                            eq("user_id", id)
                            exact("user_id", null)
                        }
                    }
                }
                .decodeList<NotificationDto>()
        }
        emit(response.toListNotificationDomain())
    }
}