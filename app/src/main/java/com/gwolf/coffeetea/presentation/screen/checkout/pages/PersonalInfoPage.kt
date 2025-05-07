package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomTextInput
import com.gwolf.coffeetea.presentation.component.CustomTextInputStyle
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.getFlagEmoji
import java.util.Locale

@Composable
fun PersonalInfoPage(
    viewModel: PersonalInfoViewModel = hiltViewModel(),
    nextStep: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = PersonalInfoEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is PersonalInfoEvent.Idle -> {}
            is PersonalInfoEvent.Navigate -> {
                nextStep()
            }
        }
    }

    PersonalInfoContent(
        state = state,
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )


}

@Composable
private fun PersonalInfoContent(
    state: PersonalInfoScreenState,
    onIntent: (PersonalInfoIntent) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
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
                    onIntent(PersonalInfoIntent.Input.EnterPhone(text))
                },
                style = CustomTextInputStyle.PHONE,
                imeAction = ImeAction.Done,
                isError = state.phoneError.asString().isNotBlank(),
                errorMessage = state.phoneError
            )
            Spacer(modifier = Modifier.size(16.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.first_name_placeholder,
                text = state.firstName,
                onValueChange = { text ->
                    onIntent(PersonalInfoIntent.Input.EnterFirstName(text))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.firstNameError.asString().isNotBlank(),
                errorMessage = state.firstNameError
            )
            Spacer(modifier = Modifier.size(16.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.last_name_placeholder,
                text = state.lastName,
                onValueChange = { text ->
                    onIntent(PersonalInfoIntent.Input.EnterLastName(text))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.lastNameError.asString().isNotBlank(),
                errorMessage = state.lastNameError
            )
        }

//        val btnEnabled = state.selectedDepartment != null
        CustomButton(
            text = R.string.title_continue,
            isEnabled = true //btnEnabled
        ) {
            onIntent(PersonalInfoIntent.ButtonClick.Submit)
        }
    }
}

@Preview
@Composable
private fun PersonalInfoPagePreview() {
    PersonalInfoContent(
        state = PersonalInfoScreenState()
    )
}