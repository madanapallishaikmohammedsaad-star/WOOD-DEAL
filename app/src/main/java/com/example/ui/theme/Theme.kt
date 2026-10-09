package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NaturalBrownPrimary,
    onPrimary = Color.White,
    primaryContainer = NaturalWoodDeep,
    onPrimaryContainer = NaturalClayLight,
    secondary = NaturalBrownMedium,
    onSecondary = Color.White,
    surface = TimberDarkSurface,
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF2D2926),
    onSurfaceVariant = Color(0xFFD7CCC8),
    background = Color(0xFF141210),
    onBackground = Color(0xFFF5F5F4),
    outline = Color(0xFF3E3A36)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NaturalBrownPrimary,
    onPrimary = Color.White,
    primaryContainer = NaturalClayExtraLight,
    onPrimaryContainer = NaturalWoodDeep,
    secondary = NaturalBrownDark,
    onSecondary = Color.White,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = NeutralSubCard,
    onSurfaceVariant = TextSecondary,
    background = NaturalBackground,
    onBackground = TextPrimary,
    outline = NaturalBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep distinctive timber branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
