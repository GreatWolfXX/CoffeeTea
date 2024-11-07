package com.gwolf.coffeetea.presentation.component

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun CategorySmallCard(
    category: Category
) {
    Card(
        modifier = Modifier.width(80.dp),
        colors = CardDefaults.cardColors(PrimaryDarkColor),
        shape = RoundedCornerShape(8.dp),
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

//@Preview(showBackground = true)
//@Composable
//private fun CategorySmallCardPreview() {
//    CategorySmallCard()
//}