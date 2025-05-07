package com.gwolf.coffeetea.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

enum class ErrorOrEmptyStyle {
    ERROR,
    NETWORK,
    PRODUCT_EMPTY,
    FAVORITE_EMPTY
}

@Composable
fun ErrorOrEmptyComponent(
    style: ErrorOrEmptyStyle = ErrorOrEmptyStyle.NETWORK,
    @StringRes title: Int,
    @StringRes desc: Int,
    withBackButton: Boolean = false,
    onClick: () -> Unit = {}
) {
    val imageRes = when (style) {
        ErrorOrEmptyStyle.ERROR -> R.drawable.error_illustratoin
        ErrorOrEmptyStyle.PRODUCT_EMPTY -> R.drawable.products_empty
        ErrorOrEmptyStyle.FAVORITE_EMPTY -> R.drawable.favorites_empty
        ErrorOrEmptyStyle.NETWORK -> R.drawable.network_illustration
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.size(32.dp))
        Image(
            modifier = Modifier.padding(horizontal = 48.dp),
            imageVector = ImageVector.vectorResource(imageRes),
            contentDescription = null
        )
        Text(
            modifier = Modifier,
            text = stringResource(id = title),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier,
            text = stringResource(id = desc),
            textAlign = TextAlign.Center,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = OutlineColor
        )
        Spacer(modifier = Modifier.size(32.dp))
        if (withBackButton) {
            Box(
                modifier = Modifier.padding(horizontal = 64.dp)
            ) {
                CustomButton(text = R.string.btn_back, onClick = onClick)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ErrorOrEmptyComponentPreview() {
    ErrorOrEmptyComponent(
        style = ErrorOrEmptyStyle.NETWORK,
        title = R.string.title_product_empty,
        desc = R.string.desc_product_empty,
        withBackButton = false,
        onClick = {}
    )
}