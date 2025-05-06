package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun CitySelector(
    cityName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .border(
                width = 1.dp,
                color = OnSurfaceColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        }
        Column(
            modifier = Modifier.weight(0.8f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.title_your_city),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = OutlineColor
            )
            val city = cityName.ifBlank {
                stringResource(
                    R.string.choose_city
                )
            }
            Text(
                modifier = Modifier,
                text = city,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OnSurfaceColor
            )
        }
        Box(
            modifier = Modifier.weight(0.1f),
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