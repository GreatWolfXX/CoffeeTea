package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun CategoryFullCard() {
    Card(
        modifier = Modifier
            .size(
                width = 116.dp,
                height = 144.dp
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // image
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.image_category_mock),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.size(4.dp))
            // title
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Кава",
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = OnSurfaceColor
            )
            Spacer(Modifier.size(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFullCardPreview() {
    CategoryFullCard()
}