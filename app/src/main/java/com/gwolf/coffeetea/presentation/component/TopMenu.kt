package com.gwolf.coffeetea.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun BoxScope.TopMenu(
    @StringRes title: Int,
    menuIcon: ImageVector? = null,
    color: Color = Color.White,
    onClickBack: () -> Unit,
    onClickMenu: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 64.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            modifier = Modifier.clickable {
                onClickBack.invoke()
            },
            imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
            contentDescription = null,
            tint = color
        )
        Text(
            modifier = Modifier,
            text = stringResource(id = title),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            color = color
        )
        if(menuIcon != null) {
            Icon(
                modifier = Modifier.clickable {
                    onClickMenu.invoke()
                },
                imageVector = menuIcon,
                contentDescription = null,
                tint = color
            )
        } else {
            Spacer(modifier = Modifier)
        }
    }
}