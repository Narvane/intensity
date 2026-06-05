package com.intensity.mobile.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Tokens adicionais alinhados a [docs/ui-new/layout-board.html] (fora do ColorScheme M3).
 */
object IntensityBrand {
    val RoleParticipant: Color = Color(0xFF1E5EFF)
    val RoleParticipant2: Color = Color(0xFF4C7CFF)
    val RoleParticipantSurface: Color = Color(0xFFE8F1FF)

    val ParamEffort: Color = Color(0xFF00A3B4)
    val ParamEffortSurface: Color = Color(0xFFDCFBFF)
    val ParamDiscomfort: Color = Color(0xFF84CC16)
    val ParamDiscomfortSurface: Color = Color(0xFFF7FEE7)
    val ParamDaring: Color = Color(0xFFE11D48)
    val ParamDaringSurface: Color = Color(0xFFFFE4E6)

    val Intensity1: Color = Color(0xFF2E7D32)
    val Intensity1Surface: Color = Color(0xFFE8F5E9)
    val Intensity2: Color = Color(0xFF0277BD)
    val Intensity2Surface: Color = Color(0xFFE3F2FD)
    val Intensity3: Color = Color(0xFFF9A825)
    val Intensity3Surface: Color = Color(0xFFFFF8E1)
    val Intensity4: Color = Color(0xFFEF6C00)
    val Intensity4Surface: Color = Color(0xFFFFF3E0)
    val Intensity5: Color = Color(0xFFC62828)
    val Intensity5Surface: Color = Color(0xFFFFEBEE)

    val RatingStar: Color = Color(0xFFF9A825)

    val ParticipantBarBrush: Brush = Brush.horizontalGradient(listOf(RoleParticipant, RoleParticipant2))

    val CardBorderWarm: Color = Color(0x38B0946F)
}
