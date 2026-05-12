package denis.and.co.handshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.*
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.WhiteText

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val controller = rememberColorPickerController()

    var selectedColor by remember { mutableStateOf(initialColor) }
    var hexText by remember { mutableStateOf(colorToHex(initialColor)) }

    LaunchedEffect(initialColor) {
        controller.selectByColor(initialColor, fromUser = false)
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                controller = controller,
                onColorChanged = { envelope ->
                    selectedColor = envelope.color
                    hexText = colorToHex(envelope.color)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                controller = controller
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = hexText,
                onValueChange = { newValue ->
                    val filtered = newValue
                        .uppercase()
                        .filterIndexed { index, char ->
                            when {
                                index == 0 && char == '#' -> true
                                char in "0123456789ABCDEF" -> true
                                else -> false
                            }
                        }
                        .take(7)

                    hexText = filtered

                    if (filtered.length == 7) {
                        hexToColor(filtered)?.let { color ->
                            selectedColor = color
                            controller.selectByColor(color, fromUser = false)
                        }
                    }
                },
                label = { Text("HEX (#RRGGBB)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            RGBFields(
                color = selectedColor,
                onColorChange = { color ->
                    selectedColor = color
                    hexText = colorToHex(color)
                    controller.selectByColor(color, fromUser = false)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = WhiteText
                )
            ) {
                Text("Готово")
            }
        }
    }
}

@Composable
private fun RGBFields(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    var r by remember(color) { mutableStateOf((color.red * 255).toInt().toString()) }
    var g by remember(color) { mutableStateOf((color.green * 255).toInt().toString()) }
    var b by remember(color) { mutableStateOf((color.blue * 255).toInt().toString()) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RGBField("R", r, Modifier.weight(1f)) {
            r = it
            updateRgb(r, g, b, onColorChange)
        }
        RGBField("G", g, Modifier.weight(1f)) {
            g = it
            updateRgb(r, g, b, onColorChange)
        }
        RGBField("B", b, Modifier.weight(1f)) {
            b = it
            updateRgb(r, g, b, onColorChange)
        }
    }
}

@Composable
private fun RGBField(
    label: String,
    value: String,
    modifier: Modifier,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue
                .filter { it.isDigit() }
                .take(3)

            val number = filtered.toIntOrNull()

            if (number == null || number <= 255) {
                onChange(filtered)
            }
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true
    )
}

private fun updateRgb(
    r: String,
    g: String,
    b: String,
    onColorChange: (Color) -> Unit
) {
    val red = r.toIntOrNull()?.coerceIn(0, 255) ?: return
    val green = g.toIntOrNull()?.coerceIn(0, 255) ?: return
    val blue = b.toIntOrNull()?.coerceIn(0, 255) ?: return

    onColorChange(Color(red, green, blue))
}

private fun colorToHex(color: Color): String {
    return String.format(
        "#%06X",
        0xFFFFFF and color.toArgb()
    )
}

private fun hexToColor(hex: String): Color? {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        null
    }
}