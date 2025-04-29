package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.GrayAlpha05
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun FiltersComponent(
    onSortingStateChanged: (lowToHighSelected: Boolean, highToLowSelected: Boolean) -> Unit,
    priceRange: ClosedFloatingPointRange<Float>,
    onValueChange: (lowPrice: Float, highPrice: Float) -> Unit
) {
    Box {
        var menuStateSort by remember { mutableStateOf(false) }
        var menuStateFilter by remember { mutableStateOf(false) }

        var lowToHighSelected by remember { mutableStateOf(true) }
        var highToLowSelected by remember { mutableStateOf(false) }

        val sortingDescription = when {
            lowToHighSelected -> stringResource(R.string.title_low_to_high)
            highToLowSelected -> stringResource(R.string.title_high_to_low)
            else -> stringResource(R.string.title_filter_empty)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    WhiteAlpha06
                )
        ) {
            Filter(
                icon = Icons.Outlined.SwapVert,
                title = stringResource(R.string.title_sorting),
                desc = sortingDescription
            ) {
                menuStateSort = !menuStateSort
            }
            Filter(
                icon = Icons.Outlined.FilterAlt,
                title = stringResource(R.string.title_filter),
                desc = "Не обраний"
            ) {
                menuStateFilter = !menuStateFilter
            }
        }
        
        MenuSort(
            isOpen = menuStateSort,
            onSortingStateChanged = { lowToHigh, highToLow ->
                lowToHighSelected = lowToHigh
                highToLowSelected = highToLow
                onSortingStateChanged(lowToHigh, highToLow)
            },
            onDismiss = {
                menuStateSort = false
            }
        )

        MenuFilters(
            isOpen = menuStateFilter,
            onDismiss = {
                menuStateFilter = false
            },
            priceRange = priceRange,
            onValueChange = onValueChange
        )

    }
}

@Composable
private fun RowScope.Filter(
    icon: ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .weight(0.5f)
            .clickable {
                onClick.invoke()
            }
            .padding(vertical = 4.dp),
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
private fun MenuSort(
    isOpen: Boolean,
    onSortingStateChanged: (lowToHighSelected: Boolean, highToLowSelected: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var lowToHighSelected by remember { mutableStateOf(true) }
    var highToLowSelected by remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
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
                    .clickable {
                        onDismiss.invoke()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.75f)
                    .background(Color.White),
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
                        lowToHighSelected = true
                        highToLowSelected = false
                        onSortingStateChanged(
                            lowToHighSelected,
                            highToLowSelected
                        )
                    }
                    val onHighToLowClick = {
                        highToLowSelected = true
                        lowToHighSelected = false
                        onSortingStateChanged(
                            lowToHighSelected,
                            highToLowSelected
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clickable {
                                onLowToHighClick.invoke()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.size(20.dp),
                            selected = lowToHighSelected,
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
                            .clickable {
                                onHighToLowClick.invoke()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.size(20.dp),
                            selected = highToLowSelected,
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
                        .clickable {
                            onDismiss.invoke()
                        }
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

@Composable
private fun MenuFilters(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    priceRange: ClosedFloatingPointRange<Float>,
    onValueChange: (lowPrice: Float, highPrice: Float) -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
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
                    .clickable {
                        onDismiss.invoke()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.75f)
                    .background(Color.White),
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
                        priceRange = priceRange,
                        onValueChange = onValueChange
                    )
                }
                Column(
                    modifier = Modifier
                        .clickable {
                            onDismiss.invoke()
                        }
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
            .padding(vertical = 8.dp, horizontal = 16.dp),
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
    AnimatedVisibility(isVisible) {
        Column(
            content = content
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuFilterPrice(
    priceRange: ClosedFloatingPointRange<Float>,
    onValueChange: (lowPrice: Float, highPrice: Float) -> Unit
) {
    MenuFilterItem(
        title = stringResource(R.string.title_price)
    ) {
        val sliderRange by remember { mutableStateOf(priceRange) }
        var sliderRangeState by remember { mutableStateOf(sliderRange) }
        val startInteractionSource = remember { MutableInteractionSource() }
        val endInteractionSource = remember { MutableInteractionSource() }

        var textLow by remember { mutableStateOf(sliderRange.start.toInt().toString()) }
        var textHigh by remember { mutableStateOf(sliderRange.endInclusive.toInt().toString()) }

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
                onValueChange = {
                    textLow = it + textLow.drop(1)
                    if (textLow.isNotBlank() && textLow.matches(Regex("\\d+"))) {
                        val low = textLow.toFloat()
                        if (low <= sliderRangeState.endInclusive && low in sliderRange) {
                            if (low !in sliderRange) {
                                textHigh = sliderRange.start.toString()
                            }
                            sliderRangeState = low..sliderRangeState.endInclusive
                        }
                    } else {
                        textLow = sliderRange.start.toString()
                    }
                },
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
                onValueChange = {
                    textHigh = it
                    if (textHigh.isNotBlank() && textLow.matches(Regex("\\d+"))) {
                        val high = textHigh.toFloat()
                        if (high >= sliderRangeState.start) {
                            if (high !in sliderRange) {
                                textHigh = sliderRange.endInclusive.toString()
                            }
                            sliderRangeState = sliderRangeState.start..high
                        }
                    } else {
                        textHigh = sliderRange.endInclusive.toString()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        RangeSlider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = sliderRangeState,
            onValueChange = { currentRange ->
                sliderRangeState = currentRange
                textLow = currentRange.start.toString()
                textHigh = currentRange.endInclusive.toString()
                onValueChange(currentRange.start, currentRange.endInclusive)
            },
            valueRange = sliderRange,
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

//@Preview(
//    showBackground = true,
//    backgroundColor = 0xFF7C7F7E
//)
//@Composable
//private fun FiltersComponentPreview() {
//    FiltersComponent(
//        onSortingStateChanged = { lowToHighSelected, highToLowSelected ->
//
//        }
//    )
//}