package com.example.applux.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    borderColor: Color = Color.Black,
    focusedBorder: Color = Color.Blue,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = TextStyle.Default,
    hint: @Composable (() -> Unit)? = null,
    cornerSize: Dp = 0.dp,
) {

    var focused by remember {
        mutableStateOf(false)
    }


    BasicTextField(
        value = value,
        modifier = modifier
            .onFocusChanged {
                focused = it.isFocused
            },
        onValueChange = onValueChange,
        decorationBox = { inner ->
            Box(
                modifier = modifier
                    .border(
                        width = 0.5.dp,
                        color = if (!focused) borderColor else focusedBorder,
                        shape = RoundedCornerShape(cornerSize)
                    )
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && hint != null) {
                    hint()
                }

                if(isError){
                    Icon(Icons.Filled.Warning, contentDescription = "Error", tint = Color.Red)
                }

                inner()
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = textStyle
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    value: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    borderBrush: Brush = Brush.horizontalGradient(),
    focusedBrush: Brush = Brush.horizontalGradient(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = TextStyle.Default,
    hint: @Composable (() -> Unit)? = null,
    cornerSize: Dp = 0.dp,
) {

    var focused by remember {
        mutableStateOf(false)
    }


    BasicTextField(
        value = value,
        modifier = modifier
            .onFocusChanged {
                focused = it.isFocused
            },
        onValueChange = onValueChange,
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        brush = if (!focused) borderBrush else focusedBrush,
                        shape = RoundedCornerShape(cornerSize)
                    )
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text("Hello", color = Color.LightGray)
                }
                inner()
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = textStyle
    )
}

@Composable
fun Hint(
    text: String = "",
    color: Color = Color.LightGray,
    fontFamily: FontFamily? = null,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(text,
        color = color,
        fontFamily = fontFamily,
        fontSize = fontSize)
}