package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.DELIVERY_ADDRESS_TYPE_NOVA_POST_CABIN
import com.gwolf.coffeetea.util.DELIVERY_ADDRESS_TYPE_NOVA_POST_DEPARTMENT
import com.gwolf.coffeetea.util.DELIVERY_ADDRESS_TYPE_UKRPOSHTA

enum class SavedDeliveryAddressType(val value: String) {
    NovaPostDepartment(DELIVERY_ADDRESS_TYPE_NOVA_POST_DEPARTMENT),
    NovaPostCabin(DELIVERY_ADDRESS_TYPE_NOVA_POST_CABIN),
    Ukrposhta(DELIVERY_ADDRESS_TYPE_UKRPOSHTA)
}

@Composable
fun SavedDeliveryAddressCard(
    typeString: String,
    city: String,
    address: String,
    isDefault: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val type = SavedDeliveryAddressType.entries.find { element ->
        element.value == typeString
    }

    val icon = when (type!!) {
        SavedDeliveryAddressType.NovaPostDepartment, SavedDeliveryAddressType.NovaPostCabin -> {
            R.drawable.nova_post
        }

        SavedDeliveryAddressType.Ukrposhta -> {
            R.drawable.ukrposhta
        }
    }

    val title = when (type) {
        SavedDeliveryAddressType.NovaPostDepartment -> {
            R.string.nova_post_departments
        }

        SavedDeliveryAddressType.NovaPostCabin -> {
            R.string.nova_post
        }

        SavedDeliveryAddressType.Ukrposhta -> {
            R.string.ukrposhta_departments
        }
    }


    val iconTint = when (type) {
        SavedDeliveryAddressType.NovaPostDepartment, SavedDeliveryAddressType.NovaPostCabin -> {
            NovaPostColor
        }

        SavedDeliveryAddressType.Ukrposhta -> {
            Color.Unspecified
        }
    }

    val borderColor = if (isDefault) OnSurfaceColor else Color.Transparent

    Row(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .background(
                color = WhiteAlpha06
            )
            .border(
                width = 1.dp,
                color = borderColor
            )
            .padding(16.dp)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = null,
                        tint = iconTint
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(title),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                }
                AnimatedVisibility(isDefault) {
                    Text(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = OnSurfaceColor
                            )
                            .padding(all = 4.dp),
                        text = stringResource(R.string.title_selected_address),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = stringResource(R.string.title_city),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = OnSurfaceColor
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = city,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = OnSurfaceColor
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = stringResource(R.string.title_address),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = OnSurfaceColor
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = address,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = OnSurfaceColor,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LightRedColor
                    )
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .clickable {
                        onRemove.invoke()
                    },
                text = stringResource(R.string.btn_delete),
                textAlign = TextAlign.Center,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = LightRedColor
            )
        }
    }
}
