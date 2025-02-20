package com.gwolf.coffeetea.presentation.screen.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CustomTextInput
import com.gwolf.coffeetea.presentation.component.CustomTextInputStyle
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.getFlagEmoji
import java.util.Locale

@Composable
fun PersonalInfoPage(
    state: CheckoutUiState,
    viewModel: CheckoutViewModel
) {
    Column {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.title_delivery_info),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.size(16.dp))
        val flag = Locale("uk", "UA").getFlagEmoji()
        CustomTextInput(
            prefixText = "$flag $UKRAINE_PHONE_CODE",
            placeholder = R.string.new_phone_placeholder,
            text = state.phone,
            onValueChange = { text ->
                viewModel.onEvent(CheckoutEvent.PhoneChanged(text))
            },
            style = CustomTextInputStyle.PHONE,
            imeAction = ImeAction.Done,
            isError = state.phoneError != null,
            errorMessage = state.phoneError
        )
        Spacer(modifier = Modifier.size(16.dp))
        CustomTextInput(
            icon = Icons.Outlined.MailOutline,
            placeholder = R.string.first_name_placeholder,
            text = state.firstName,
            onValueChange = { text ->
                viewModel.onEvent(CheckoutEvent.FirstNameChanged(text))
            },
            style = CustomTextInputStyle.STANDARD,
            imeAction = ImeAction.Next,
            isError = state.firstNameError != null,
            errorMessage = state.firstNameError
        )
        Spacer(modifier = Modifier.size(16.dp))
        CustomTextInput(
            icon = Icons.Outlined.MailOutline,
            placeholder = R.string.last_name_placeholder,
            text = state.lastName,
            onValueChange = { text ->
                viewModel.onEvent(CheckoutEvent.LastNameChanged(text))
            },
            style = CustomTextInputStyle.STANDARD,
            imeAction = ImeAction.Next,
            isError = state.lastNameError != null,
            errorMessage = state.lastNameError
        )
    }
}