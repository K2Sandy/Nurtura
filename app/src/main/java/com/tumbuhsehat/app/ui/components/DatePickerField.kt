package com.tumbuhsehat.app.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tumbuhsehat.app.ui.theme.BorderNeutral
import com.tumbuhsehat.app.ui.theme.TextMuted
import com.tumbuhsehat.app.util.DateUtils
import java.util.Calendar

@Composable
fun DatePickerField(
    label: String,
    selectedMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { selectedMillis?.let { timeInMillis = it } }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val c = Calendar.getInstance()
                        c.set(year, month, day, 0, 0, 0)
                        onDateSelected(c.timeInMillis)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.5.dp, BorderNeutral),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TextMuted)
            Text(
                text = selectedMillis?.let { DateUtils.formatTanggal(it) } ?: label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
    }
}
