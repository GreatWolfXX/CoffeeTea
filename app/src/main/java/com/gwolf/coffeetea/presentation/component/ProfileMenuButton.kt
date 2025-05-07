package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ProfileMenuButton(
    icon: ImageVector,
    text: String,
    isVisibleArrow: Boolean = true,
    isVisibleDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryDarkColor
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    modifier = Modifier,
                    text = text,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = TextUnit(20f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
            }
            if(isVisibleArrow) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = OnSurfaceColor
                )
            } else {
                Spacer(modifier = Modifier)
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        if(isVisibleDivider) {
            HorizontalDivider(color = OnSurfaceColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileMenuButtonPreview() {
    ProfileMenuButton(
        icon = Icons.Outlined.AccountCircle,
        text = stringResource(R.string.title_welcome)
    ) {}
}