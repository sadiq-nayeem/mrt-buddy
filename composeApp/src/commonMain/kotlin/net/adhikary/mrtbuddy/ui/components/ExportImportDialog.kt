package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.repository.ImportResult

@Composable
fun ExportSuccessDialog(
    jsonData: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cards Exported Successfully")
        },
        text = {
            Text(
                "Your cards have been exported successfully. Below is your JSON data. You can copy it and save it somewhere safe.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onShare) {
                Text("Show JSON")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onSave) {
                Text("Done")
            }
        }
    )
}

@Composable
fun JsonDisplayDialog(
    jsonData: String,
    isCopying: Boolean,
    copySuccess: Boolean,
    onCopy: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Exported JSON Data")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()
            ) {
                Text(
                    "Copy this JSON data and save it for backup or import:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = jsonData.take(1000) + if (jsonData.length > 1000) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                if (jsonData.length > 1000) {
                    Text(
                        "Note: JSON is truncated. Full data contains ${jsonData.length} characters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (copySuccess) {
                    Text(
                        "✓ Copied to clipboard!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCopy,
                enabled = !isCopying
            ) {
                if (isCopying) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    when {
                        isCopying -> "Copying..."
                        copySuccess -> "✓ Copied!"
                        else -> "Copy JSON"
                    }
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ImportResultDialog(
    result: ImportResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (result.success) "Import Successful" else "Import Completed with Issues")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (result.importedCards.isNotEmpty()) {
                    Text(
                        "Successfully Imported (${result.importedCards.size}):",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    result.importedCards.forEach { cardName ->
                        Text("• $cardName", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (result.skippedCards.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Skipped (${result.skippedCards.size}):",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    result.skippedCards.forEach { cardName ->
                        Text("• $cardName (already exists)", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (result.errors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Errors (${result.errors.size}):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    result.errors.forEach { error ->
                        Text("• $error", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun ImportLoadingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Importing Cards")
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(24.dp).height(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Please wait while we import your cards...")
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ExportImportOptionsDialog(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Card Management")
        },
        text = {
            Text(
                "Choose what you'd like to do with your card data:",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onExport) {
                Text("Export Cards")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onImport) {
                Text("Import Cards")
            }
        }
    )
}