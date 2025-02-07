package com.gwolf.coffeetea.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

enum class CustomButtonStyle{
    STANDARD,
    GOOGLE
}

@Composable
fun CustomButton(
    icon: ImageVector? = null,
    @StringRes text: Int,
    style: CustomButtonStyle = CustomButtonStyle.STANDARD,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val isStandardStyle = style == CustomButtonStyle.STANDARD
    val btnIcon = if(isStandardStyle) icon else ImageVector.vectorResource(R.drawable.google_ic)
    val bgColor = if(isStandardStyle) Color.Transparent else Color.White
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryDarkColor,
                shape = RoundedCornerShape(100.dp)
            ),
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor
        ),
        contentPadding = PaddingValues(16.dp),
        enabled = isEnabled,
        onClick = { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(btnIcon != null) {
                Icon(
                    imageVector = btnIcon,
                    contentDescription = null,
                    tint = if(isStandardStyle) Color.White else Color.Unspecified
                )
                Spacer(modifier = Modifier.size(8.dp))
            } else {
                Spacer(modifier = Modifier)
            }
            Text(
                modifier = Modifier,
                text = stringResource(id = text),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = if(isStandardStyle) Color.White else Color.Black
            )
            Spacer(modifier = Modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomButtonPreview() {
    CustomButton(
        text = R.string.title_next,
        style = CustomButtonStyle.STANDARD
    ) { }
}