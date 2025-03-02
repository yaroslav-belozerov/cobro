package com.yaabelozerov.tribede.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = false,
    ime: Pair<ImeAction, () -> Unit>? = null,
    placeholder: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }
    TextField(
        enabled = enabled,
        value = value,
        visualTransformation = visualTransformation,
        onValueChange = onValueChange,
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            errorTextColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        trailingIcon = {
            AnimatedVisibility(
                value.isNotEmpty() && isFocused,
                enter = slideInHorizontally(initialOffsetX = { it }) + expandIn(expandFrom = Alignment.CenterEnd) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + shrinkOut(shrinkTowards = Alignment.CenterEnd) + fadeOut()
            ) {
                IconButton(modifier = Modifier.padding(end = 8.dp), onClick = { onValueChange("") }) {
                    Icon(
                        Icons.Default.Close, contentDescription = null
                    )
                }
            }
        },
        supportingText = {
            AnimatedVisibility(
                isError && !isFocused && value.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() + expandVertically(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() + shrinkVertically()
            ) {
                errorText?.let {
                    Text(it, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        },
        singleLine = singleLine,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.small,
        isError = isError && !isFocused && value.isNotEmpty(),
        keyboardActions = KeyboardActions(onAny = { if (!isError) ime?.second?.invoke() }),
        keyboardOptions = KeyboardOptions(
            imeAction = ime?.first ?: ImeAction.Default
        )
    )
}