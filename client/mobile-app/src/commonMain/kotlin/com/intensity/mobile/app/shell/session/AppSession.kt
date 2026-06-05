package com.intensity.mobile.app.shell.session

import com.intensity.contracts.model.AuthResponseDto

data class AppSession(
    val accessMode: String,
    val token: String,
    val groupId: String? = null,
    val boxId: String? = null,
    val experienceBoxType: String? = null,
    val curateUserId: String? = null
) {
    companion object {
        fun from(response: AuthResponseDto): AppSession? {
            val token = response.token ?: return null
            val accessMode = response.accessMode ?: return null
            return AppSession(
                accessMode = accessMode,
                token = token,
                groupId = response.groupId,
                boxId = response.boxId,
                experienceBoxType = response.experienceBoxType,
                curateUserId = response.userId
            )
        }
    }
}
