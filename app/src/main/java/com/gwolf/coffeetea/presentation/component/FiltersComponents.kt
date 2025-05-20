package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.GrayAlpha05
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun RowScope.FilterItem(
    icon: ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .weight(0.5f)
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryDarkColor
        )
        Column {
            Text(
                modifier = Modifier,
                text = title,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OnSurfaceColor
            )
            Text(
                modifier = Modifier,
                text = desc,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = OutlineColor
            )
        }
    }
}

@Composable
fun MenuSort(
    isOpen: Boolean,
    isDescending: Boolean,
    onSortingStateChanged: (isDescending: Boolean) -> Unit,
    onDismiss: () -> Unit
) {

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Surface(
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.25f)
                        .background(GrayAlpha05)
                        .clickable(onClick = onDismiss)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.75f)
                        .background(Color.White)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.title_sorting),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = OnSurfaceColor
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.title_param_sorting),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = OutlineColor
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        val onLowToHighClick = {
                            onSortingStateChanged(
                                false
                            )
                        }
                        val onHighToLowClick = {
                            onSortingStateChanged(
                                true
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clickable(onClick = onLowToHighClick),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                modifier = Modifier.size(20.dp),
                                selected = !isDescending,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryDarkColor
                                ),
                                onClick = onLowToHighClick
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                modifier = Modifier,
                                text = stringResource(R.string.title_low_to_high),
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = OnSurfaceColor
                            )
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Row(
                            modifier = Modifier
                                .clickable(onClick = onHighToLowClick),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                modifier = Modifier.size(20.dp),
                                selected = isDescending,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryDarkColor
                                ),
                                onClick = onHighToLowClick
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                modifier = Modifier,
                                text = stringResource(R.string.title_high_to_low),
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = OnSurfaceColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = GrayAlpha05
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            text = stringResource(R.string.btn_menu_close),
                            textAlign = TextAlign.Center,
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = OnSurfaceColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuFilters(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    minAndMaxPriceRange: ClosedFloatingPointRange<Float>,
    sliderRangeState: ClosedFloatingPointRange<Float>,
    textLow: String,
    textHigh: String,
    onValueChangesSlider: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeSliderFinished: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangesTextLow: (value: String) -> Unit,
    onValueChangesTextHigh: (value: String) -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Surface(
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.25f)
                        .background(GrayAlpha05)
                        .clickable(onClick = onDismiss)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.75f)
                        .background(Color.White)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                modifier = Modifier,
                                text = stringResource(R.string.title_filter),
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = OnSurfaceColor
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                modifier = Modifier,
                                text = stringResource(R.string.title_filter_desc),
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = OutlineColor
                            )
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        MenuFilterPrice(
                            minAndMaxPriceRange = minAndMaxPriceRange,
                            sliderRangeState = sliderRangeState,
                            textLow = textLow,
                            textHigh = textHigh,
                            onValueChangeSlider = onValueChangesSlider,
                            onValueChangeSliderFinished = onValueChangeSliderFinished,
                            onValueChangeTextLow = onValueChangesTextLow,
                            onValueChangeTextHigh = onValueChangesTextHigh
                        )
                    }
                    Column(
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = GrayAlpha05
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            text = stringResource(R.string.btn_menu_close),
                            textAlign = TextAlign.Center,
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = OnSurfaceColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuFilterItem(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isVisible = !isVisible
            }
            .padding(vertical = 8.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            text = title,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = OnSurfaceColor
        )
        val icon = if (isVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
        Icon(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryDarkColor
        )
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it / 5 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it / 5 }) + fadeOut()
    ) {
        Column(
            content = content
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuFilterPrice(
    minAndMaxPriceRange: ClosedFloatingPointRange<Float>,
    sliderRangeState: ClosedFloatingPointRange<Float>,
    textLow: String,
    textHigh: String,
    onValueChangeSlider: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeSliderFinished: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeTextLow: (value: String) -> Unit,
    onValueChangeTextHigh: (value: String) -> Unit
) {
    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }

    MenuFilterItem(
        title = stringResource(R.string.title_price)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.4f),
                value = textLow,
                label = {
                    Text(
                        text = stringResource(R.string.title_from),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = OnSurfaceColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = OnSurfaceColor,
                    unfocusedTextColor = OutlineColor,
                    disabledContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    disabledIndicatorColor = OutlineColor,
                    focusedIndicatorColor = OutlineColor,
                    unfocusedIndicatorColor = OutlineColor,
                    errorIndicatorColor = OutlineColor,
                    selectionColors = TextSelectionColors(
                        handleColor = OutlineColor,
                        backgroundColor = Color.Unspecified
                    ),
                    cursorColor = Color.Black,
                    errorCursorColor = LightRedColor
                ),
                singleLine = true,
                onValueChange = onValueChangeTextLow,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.weight(0.2f))
            OutlinedTextField(
                modifier = Modifier.weight(0.4f),
                value = textHigh,
                label = {
                    Text(
                        text = stringResource(R.string.title_to),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = OnSurfaceColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = OnSurfaceColor,
                    unfocusedTextColor = OutlineColor,
                    disabledContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    disabledIndicatorColor = OutlineColor,
                    focusedIndicatorColor = OutlineColor,
                    unfocusedIndicatorColor = OutlineColor,
                    errorIndicatorColor = OutlineColor,
                    selectionColors = TextSelectionColors(
                        handleColor = OutlineColor,
                        backgroundColor = Color.Unspecified
                    ),
                    cursorColor = Color.Black,
                    errorCursorColor = LightRedColor
                ),
                singleLine = true,
                onValueChange = onValueChangeTextHigh,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        RangeSlider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = sliderRangeState,
            onValueChange = onValueChangeSlider,
            onValueChangeFinished = {
                onValueChangeSliderFinished(sliderRangeState)
            },
            valueRange = minAndMaxPriceRange,
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            startThumb = {
                SliderDefaults.Thumb(
                    modifier = Modifier.size(16.dp),
                    interactionSource = startInteractionSource,
                    thumbSize = DpSize(16.dp, 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryColor
                    )
                )
            },
            endThumb = {
                SliderDefaults.Thumb(
                    modifier = Modifier.size(16.dp),
                    interactionSource = endInteractionSource,
                    thumbSize = DpSize(16.dp, 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryColor
                    )
                )
            },
            track = { rangeSliderState ->
                SliderDefaults.Track(
                    modifier = Modifier.height(8.dp),
                    rangeSliderState = rangeSliderState,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = PrimaryColor,
                        inactiveTrackColor = GrayAlpha05
                    )
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF7C7F7E
)
@Composable
private fun FilterItemPreview() {
    Row {
        FilterItem(
            icon = Icons.Outlined.SwapVert,
            title = stringResource(R.string.title_sorting),
            desc = "",
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF7C7F7E
)
@Composable
private fun MenuSortPreview() {
    var menuStateSort by remember { mutableStateOf(true) }

    var isDescending by remember { mutableStateOf(true) }

    MenuSort(
        isOpen = menuStateSort,
        isDescending = true,
        onSortingStateChanged = { value ->
            isDescending = value
        },
        onDismiss = {
            menuStateSort = false
        }
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF7C7F7E
)
@Composable
private fun MenuFiltersPreview() {
    var menuStateFilter by remember { mutableStateOf(true) }

    val priceRange: ClosedFloatingPointRange<Float> = 0f..100f

    MenuFilters(
        isOpen = menuStateFilter,
        onDismiss = {
            menuStateFilter = false
        },
        minAndMaxPriceRange = priceRange,
        sliderRangeState = priceRange,
        textLow = "",
        textHigh = "",
        onValueChangesSlider = {},
        onValueChangeSliderFinished = {},
        onValueChangesTextLow = {},
        onValueChangesTextHigh = {}
    )
}