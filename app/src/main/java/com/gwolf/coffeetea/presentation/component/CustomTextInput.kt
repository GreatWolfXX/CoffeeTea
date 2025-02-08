package com.gwolf.coffeetea.presentation.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.UiText

enum class CustomTextInputStyle {
    STANDARD,
    EMAIL,
    PHONE,
    PASSWORD
}

@Composable
fun CustomTextInput(
    icon: ImageVector? = null,
    prefixText: String = "",
    @StringRes placeholder: Int,
    text: String,
    isError: Boolean,
    passwordVisible: Boolean = false,
    onPasswordVisibleChanged: (Boolean) -> Unit = {},
    errorMessage: UiText? = null,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    style: CustomTextInputStyle = CustomTextInputStyle.STANDARD

) {
    val context = LocalContext.current

    val typeKeyboardOptions = when(style) {
        CustomTextInputStyle.PHONE ->  KeyboardOptions(keyboardType = KeyboardType.Phone)
        CustomTextInputStyle.PASSWORD -> KeyboardOptions(keyboardType = KeyboardType.Password)
        else -> KeyboardOptions.Default
    }
    val keyboardOptions = typeKeyboardOptions.copy(imeAction = imeAction)

    val visualTransformTypeCase = when(style) {
        CustomTextInputStyle.PASSWORD -> if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        else -> VisualTransformation.None
    }
    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = OutlineColor,
                    shape = RoundedCornerShape(4.dp)
                ),
            value = text,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Black
            ),
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                errorCursorColor = LightRedColor
            ),
            placeholder = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = placeholder),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = OutlineColor
                )
            },
            prefix = {
                if(icon != null) {
                    Icon(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = OutlineColor
                    )
                } else {
                    Text(
                        modifier = Modifier,
                        text = prefixText,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                }
            },
            trailingIcon = if(style == CustomTextInputStyle.PASSWORD) {
                {
                    val iconVisibility = if (!passwordVisible)
                        Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff

                    IconButton(onClick = { onPasswordVisibleChanged(!passwordVisible) }) {
                        Icon(
                            imageVector  = iconVisibility,
                            contentDescription = null,
                            tint = OutlineColor
                        )
                    }
                }
            } else { null },
            visualTransformation = visualTransformTypeCase,
            keyboardOptions = keyboardOptions,
            isError = isError
        )
        Spacer(modifier = Modifier.size(4.dp))
        AnimatedVisibility(visible = isError) {
            Text(
                modifier = Modifier,
                text = if(isError) errorMessage!!.asString(context) else "",
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = LightRedColor
            )
        }
    }
}