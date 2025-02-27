package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun SavedDeliveryAddressSmallCard(
    modifier: Modifier = Modifier,
    typeString: String,
    address: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
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

    val iconTint = when (type) {
        SavedDeliveryAddressType.NovaPostDepartment, SavedDeliveryAddressType.NovaPostCabin -> {
            NovaPostColor
        }

        SavedDeliveryAddressType.Ukrposhta -> {
            Color.Unspecified
        }
    }

    val borderColor = if (isSelected) OnSurfaceColor else Color.Transparent

    Row(
        modifier = modifier
            .width(300.dp)
            .background(
                color = WhiteAlpha06,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(4.dp),
                color = borderColor
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = iconTint
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = address,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = OnSurfaceColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun SavedDeliveryAddressPreview() {
    SavedDeliveryAddressSmallCard(
        typeString = SavedDeliveryAddressType.NovaPostDepartment.value,
        address = "sfsdfsdf, sdf 32, sdfsfsdasdasd sfdsdfds sfsfsdf",
        isSelected = false
    )
}
