package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun CategorySmallCard(
    modifier: Modifier = Modifier,
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(PrimaryDarkColor),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally),
            text = category.name,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySmallCardPreview() {
    val category = Category(
        id = 0,
        name = "Coffee",
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnRlkHl_qadBAMBqFScSWT-C_xhIgZPjlMxQ&s"
    )

    CategorySmallCard(
        category = category,
        onClick = {}
    )
}