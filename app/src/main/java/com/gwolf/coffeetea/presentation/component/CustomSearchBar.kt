package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomSearchBar(
    title: String,
    placeholder: String,
    sheetState: SheetState,
    list: List<T>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onClickItem: (T) -> Unit,
    itemText: (T) -> String
) {
    SearchBarBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        title = title,
        placeholder = placeholder,
        query = query,
        onQueryChange = onQueryChange,
        onClear = onClear,
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.75f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = list,
                key = { item -> itemText(item) }
            ) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClickItem(item)
                        }
                ) {
                    Text(
                        modifier = Modifier,
                        text = itemText(item),
                        textAlign = TextAlign.Start,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = OnSurfaceColor
                    )
                }
            }
        }
    }
}