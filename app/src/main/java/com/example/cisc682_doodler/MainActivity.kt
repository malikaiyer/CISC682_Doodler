package com.example.cisc682_doodler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cisc682_doodler.ui.theme.CISC682_DoodlerTheme
import com.example.cisc682_doodler.DoodleView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CISC682_DoodlerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var isDrawingCleared by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val doodleView = remember { DoodleView(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tool Panel at the top
        ToolPanel(
            onColorSelected = { color ->
                doodleView.updateBrush(color, doodleView.currentPaint.strokeWidth, doodleView.currentPaint.alpha) // Example: Update with default size and opacity
            },
            onBrushSizeSelected = { size ->
                doodleView.updateBrush(doodleView.currentPaint.color, size, doodleView.currentPaint.alpha)},
            onOpacitySelected = { opacity ->
                doodleView.updateBrush(doodleView.currentPaint.color, doodleView.currentPaint.strokeWidth, opacity)
            },
            onClearSketch = {
                doodleView.clearCanvas()
                isDrawingCleared = true
            },
            onUndo = { doodleView.undo() }, // Wire to DoodleView undo
            onRedo = { doodleView.redo() }  // Wire to DoodleView redo
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between tool panel and canvas

        // Canvas placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            DoodleViewComposable(doodleView)
        }
    }
}

@Composable
fun DoodleViewComposable(doodleView: DoodleView) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { context ->
            doodleView}

    )
}

// Tool panel with buttons arranged in a row
@Composable
fun ToolPanel(
    onColorSelected: (Int) -> Unit,
    onBrushSizeSelected: (Float) -> Unit,
    onOpacitySelected: (Int) -> Unit,
    onClearSketch: () -> Unit,
    onUndo: () -> Unit, // Add undo handler
    onRedo: () -> Unit  // Add redo handler
) {
    val colors = listOf("Black" to android.graphics.Color.BLACK, "Red" to android.graphics.Color.RED, "Blue" to android.graphics.Color.BLUE)
    val brushSizes = listOf(5f, 10f, 15f, 20f)
    val opacities = listOf(64, 128, 192, 255)

    var selectedColor by remember { mutableStateOf(colors[0].first) }
    var selectedBrushSize by remember { mutableStateOf(brushSizes[0]) }
    var selectedOpacity by remember { mutableStateOf(opacities[0]) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Space between rows
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First row with three buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between buttons
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown for Color
            DropdownMenuSelector(
                label = "Color",
                options = colors.map { it.first },
                selectedOption = selectedColor,
                onOptionSelected = { option ->
                    selectedColor = option
                    val color = colors.find { it.first == option }!!.second
                    onColorSelected(color)
                }
            )

            // Dropdown for Brush Size
            DropdownMenuSelector(
                label = "Brush Size",
                options = brushSizes.map { it.toString() },
                selectedOption = selectedBrushSize.toString(),
                onOptionSelected = { option ->
                    selectedBrushSize = option.toFloat()
                    onBrushSizeSelected(selectedBrushSize)
                }
            )

            // Dropdown for Opacity
            DropdownMenuSelector(
                label = "Opacity",
                options = opacities.map { it.toString() },
                selectedOption = selectedOpacity.toString(),
                onOptionSelected = { option ->
                    selectedOpacity = option.toInt()
                    onOpacitySelected(selectedOpacity)
                })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between buttons
            verticalAlignment = Alignment.CenterVertically
        ){
        // Undo button
        Button(onClick = { onUndo() }) {
            Text("⤺") // Undo icon
        }
        // Redo button
        Button(onClick = { onRedo() }) {
            Text("⤻") // Redo icon
        }
        // Clear Sketch button below the row
        Button(onClick = { onClearSketch() }) {
            Text(text = "Clear Sketch")
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.width(120.dp)
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()  // Important for proper dropdown behavior
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Preview for design iteration
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CISC682_DoodlerTheme {
        MainScreen()
    }
}
