package com.gwolf.coffeetea.presentation.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeuisuite.ohteepee.OhTeePeeDefaults
import com.composeuisuite.ohteepee.OhTeePeeInput
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpBottomSheet(
    sheetState: SheetState,
    @StringRes title: Int,
    @StringRes desc: Int,
    isError: Boolean,
    errorMessage: UiText? = null,
    onClickConfirm: (otpValue: String) -> Unit,
    onClickResendCode: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var otpValue: String by remember { mutableStateOf("") }

    val defaultCellConfig = OhTeePeeDefaults.cellConfiguration(
        borderColor = OutlineColor,
        borderWidth = 1.dp,
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = OnSurfaceColor
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = title),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = OnSurfaceColor
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = desc),
                textAlign = TextAlign.Center,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = TextUnit(16f, TextUnitType.Sp),
                color = OutlineColor
            )
            Spacer(modifier = Modifier.size(16.dp))
            OhTeePeeInput(
                value = otpValue,
                onValueChange = { newValue, isValid ->
                    otpValue = newValue
                },
                autoFocusByDefault = false,
                configurations = OhTeePeeDefaults.inputConfiguration(
                    cellsCount = 6,
                    emptyCellConfig = defaultCellConfig,
                    cellModifier = Modifier.size(48.dp),
                    placeHolder = "-"
                ),
            )
            Spacer(modifier = Modifier.size(16.dp))
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
            Spacer(modifier = Modifier.size(16.dp))
            CustomButton(
                text = R.string.btn_save,
                isEnabled = otpValue.isNotBlank(),
                onClick = {
                    onClickConfirm.invoke(otpValue)
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.didnt_receive_code),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = TextUnit(16f, TextUnitType.Sp),
                color = OutlineColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        onClickResendCode.invoke()
                    },
                text = stringResource(id = R.string.resend_code),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = TextUnit(16f, TextUnitType.Sp),
                color = OnSurfaceColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun OtpBottomSheetPreview() {
    OtpBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        title = R.string.title_verify_email,
        desc = R.string.desc_verify_email,
        isError = false,
        onClickConfirm = {},
        onClickResendCode = {},
        onDismiss = {}
    )
}