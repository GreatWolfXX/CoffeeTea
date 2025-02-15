package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun StepProgressBar(
    modifier: Modifier = Modifier,
    stepsNumber: Int = 3,
    currentStep: Int = 0,
    listTitles: List<String> = listOf<String>(),
    stepCircleSize: Dp = 32.dp,
    thickness: Dp = 4.dp,
    colorActive: Color = Color(0xFF002C21),
    colorInactive: Color = Color(0xFF149B75),
    colorIcon: Color = Color(0xFF1DD49E)
) {
    var localStep by remember { mutableIntStateOf(currentStep) }

    LaunchedEffect(currentStep) {
        localStep = currentStep
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        for (step in 0..stepsNumber.dec()) {
            Line(
                modifier = Modifier.weight(1f),
                currentStep = currentStep,
                stepIndex = step,
                stepCircleSize = stepCircleSize,
                thickness = thickness,
                colorActive = colorActive,
                colorInactive = colorInactive
            )
            StepWithText(
                currentStep = currentStep,
                stepIndex = step,
                stepCircleSize = 32.dp,
                titleStep = listTitles[step],
                colorActive = colorActive,
                colorInactive = colorInactive,
                colorIcon = colorIcon
            )
        }
    }
}

@Composable
private fun Step(
    currentStep: Int,
    stepIndex: Int,
    stepCircleSize: Dp,
    colorActive: Color,
    colorInactive: Color,
    colorIcon: Color
) {
    val isCurrent = stepIndex == currentStep
    val isActive = stepIndex <= currentStep
    val animatedCircleSize by animateDpAsState(if (isActive) stepCircleSize else 0.dp)

    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .size(32.dp)
            .align(Alignment.Center)
            .border(
                shape = CircleShape,
                width = 2.dp,
                color = colorInactive
            ),
            onDraw = {
                drawCircle(color = Color.Transparent)
            }
        )
        Canvas(modifier = Modifier
            .size(animatedCircleSize)
            .align(Alignment.Center)
            .border(
                shape = CircleShape,
                width = 2.dp,
                color = Color.Transparent
            ),
            onDraw = {
                drawCircle(color = colorActive)
            }
        )
        AnimatedVisibility(isCurrent) {
            Text(
                modifier = Modifier,
                text = stepIndex.inc().toString(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colorIcon
            )
        }
        AnimatedVisibility(!isCurrent) {
            Icon(
                imageVector = Icons.Rounded.Check,
                tint = colorIcon,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun StepWithText(
    currentStep: Int,
    stepIndex: Int,
    stepCircleSize: Dp,
    titleStep: String,
    colorActive: Color,
    colorInactive: Color,
    colorIcon: Color
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Step(
            currentStep = currentStep,
            stepIndex = stepIndex,
            stepCircleSize = stepCircleSize,
            colorActive = colorActive,
            colorInactive = colorInactive,
            colorIcon = colorIcon
        )
        Text(
            modifier = Modifier
                .width(stepCircleSize+32.dp),
            text = titleStep,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Visible,
            color = OnSurfaceColor
        )
    }
}

@Composable
private fun Line(
    modifier: Modifier = Modifier,
    currentStep: Int,
    stepIndex: Int,
    stepCircleSize: Dp,
    thickness: Dp,
    colorActive: Color,
    colorInactive: Color
) {
    val isActive = stepIndex <= currentStep
    val animatedFillWidth by animateFloatAsState(
        if (isActive) 1f else 0f
    )
    val topPadding = (stepCircleSize - thickness) / 2

    if (stepIndex != 0) {
        Box(
            modifier = modifier
                .scale(scaleX = 1.25f, scaleY = 1f)
                .padding(top = topPadding)
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = colorInactive,
                thickness = thickness
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(animatedFillWidth)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = colorActive,
                thickness = 4.dp
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1DD49E
)
@Composable
private fun StepProgressBarPreview() {
    StepProgressBar(
        stepsNumber = 3,
        currentStep = 0,
        listTitles = listOf(
            "Доставка",
            "Отримувач",
            "Оплата"
        )
    )
}

