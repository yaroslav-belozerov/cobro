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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = false,
    ime: Pair<ImeAction, () -> Unit>? = null,
    placeholder: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    visualTransformation = visualTransformation,
//    trailingIcon = {
//        AnimatedVisibility(
//            value.isNotEmpty(),
//            enter = slideInHorizontally(initialOffsetX = { it }) + expandIn(expandFrom = Alignment.CenterEnd) + fadeIn(),
//            exit = slideOutHorizontally(targetOffsetX = { it }) + shrinkOut(shrinkTowards = Alignment.CenterEnd) + fadeOut()
//        ) {
//            IconButton(modifier = Modifier
//                .padding(end = 8.dp),
//                onClick = { onValueChange("") }) {
//                Icon(
//                    Icons.Default.Close, contentDescription = null
//                )
//            }
//        }
//    },
    supportingText = {
        AnimatedVisibility(
            isError,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() + expandVertically(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() + shrinkVertically()
        ) {
            errorText?.let {
                Text(it)
                Spacer(Modifier.height(8.dp))
            }
        }
    },
    singleLine = singleLine,
    placeholder = placeholder,
    shape = MaterialTheme.shapes.small,
    isError = isError,
    keyboardActions = KeyboardActions(onAny = { if (!isError) ime?.second?.invoke() }),
    keyboardOptions = KeyboardOptions(
        imeAction = ime?.first ?: ImeAction.Default
    )
)