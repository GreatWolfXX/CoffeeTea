package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import kotlinx.coroutines.launch

@Composable
fun PostComponent(
    snackbarHostState: SnackbarHostState,
    icon: ImageVector,
    iconTint: Color = Color.Unspecified,
    title: String,
    desc: String,
    departmentName: String,
    priceTitle: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onSelectedChange: (Boolean) -> Unit = {},
    onAddressClick: () -> Unit = {}
) {
    var localSelected by remember { mutableStateOf(selected) }

    LaunchedEffect(selected) {
        localSelected = selected
    }

    val snackbarMessage = stringResource(R.string.snackbar_choose_address)
    val coroutineScope = rememberCoroutineScope()

    val borderColor = if (localSelected) OnSurfaceColor else Color.Transparent
    val blurEnabled by animateDpAsState(if (enabled) 0.dp else 1.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .blur(blurEnabled)
            .background(
                color = WhiteAlpha06
            )
            .border(
                width = 1.dp,
                color = borderColor
            )
            .clickable {
                if (enabled) {
                    localSelected = !localSelected
                    onSelectedChange.invoke(localSelected)
                } else {
                    coroutineScope.launch {
                        snackbarHostState
                            .showSnackbar(
                                message = snackbarMessage,
                                duration = SnackbarDuration.Short
                            )
                    }
                }
            }
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.95f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = title,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    modifier = Modifier,
                    text = desc,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = OnSurfaceColor
                )
            }
            Spacer(modifier = Modifier.weight(0.05f))
            Text(
                modifier = Modifier,
                text = priceTitle,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OnSurfaceColor
            )
        }
        AnimatedVisibility(localSelected) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .border(
                        width = 1.dp,
                        color = OnSurfaceColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
                    .clickable {
                        onAddressClick.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(0.8f),
                    text = departmentName,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = OnSurfaceColor
                )
                Box(
                    modifier = Modifier.weight(0.2f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        modifier = Modifier
                            .size(16.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = OnSurfaceColor
                    )
                }
            }
        }
    }
}