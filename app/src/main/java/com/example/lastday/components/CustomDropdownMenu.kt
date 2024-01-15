package com.example.lastday.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    placeHolder: String,
    selectedItem: String, onItemSelect: (Int) -> Unit, items: List<String>
) {
    var showDropdown by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = showDropdown,
        onExpandedChange = {
            showDropdown = it
        }) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown)
            },
            placeholder = {
                Text(text = placeHolder)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false }) {
            items.forEachIndexed { index, title ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(modifier = Modifier.fillMaxWidth(), text = title) },
                    onClick = {
                        onItemSelect.invoke(index)
                        showDropdown = false
                    })
            }
        }
    }
}