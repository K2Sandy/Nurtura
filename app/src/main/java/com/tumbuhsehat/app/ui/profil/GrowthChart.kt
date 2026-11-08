package com.tumbuhsehat.app.ui.profil

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tumbuhsehat.app.data.repository.GrowthReferenceRow
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.TextMuted
import kotlin.math.max
import kotlin.math.min

/**
 * Draws the child's actual measurements (pink line) against the WHO normal
 * range band (gray fill between the -2SD/+2SD reference rows). This is a
 * plain Compose Canvas implementation - no external chart library needed.
 * (Vico is still listed in build.gradle.kts if this ever needs upgrading to
 * something more elaborate - see the README for notes on that.)
 */
@Composable
fun GrowthChart(
    referenceRows: List<GrowthReferenceRow>,
    actualPoints: List<Pair<Double, Double>>, // (usia dalam bulan, nilai terukur)
    modifier: Modifier = Modifier,
    satuan: String = "kg",
) {
    val sortedRef = referenceRows.sortedBy { it.x }
    val sortedActual = actualPoints.sortedBy { it.first }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        if (sortedRef.isEmpty()) return@Canvas

        val paddingLeft = 40.dp.toPx()
        val paddingBottom = 24.dp.toPx()
        val paddingTop = 8.dp.toPx()
        val paddingRight = 8.dp.toPx()

        val chartWidth = size.width - paddingLeft - paddingRight
        val chartHeight = size.height - paddingTop - paddingBottom

        val xMin = sortedRef.first().x
        val xMax = sortedRef.last().x
        val yMin = (sortedRef.minOf { it.pLower }).let { ref ->
            min(ref, sortedActual.minOfOrNull { it.second } ?: ref)
        } * 0.95
        val yMax = (sortedRef.maxOf { it.pUpper }).let { ref ->
            max(ref, sortedActual.maxOfOrNull { it.second } ?: ref)
        } * 1.05

        fun xToPx(x: Double): Float =
            (paddingLeft + ((x - xMin) / (xMax - xMin).coerceAtLeast(0.001)) * chartWidth).toFloat()

        fun yToPx(y: Double): Float =
            (paddingTop + chartHeight - ((y - yMin) / (yMax - yMin).coerceAtLeast(0.001)) * chartHeight).toFloat()

        // ---- Gray band: WHO normal range (pLower..pUpper) ----
        val bandPath = androidx.compose.ui.graphics.Path().apply {
            sortedRef.forEachIndexed { i, row ->
                val px = xToPx(row.x); val py = yToPx(row.pUpper)
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            for (i in sortedRef.indices.reversed()) {
                val row = sortedRef[i]
                lineTo(xToPx(row.x), yToPx(row.pLower))
            }
            close()
        }
        drawPath(bandPath, color = Color(0xFFB4B2A9).copy(alpha = 0.25f), style = Fill)

        // ---- Dashed median line ----
        val medianPath = androidx.compose.ui.graphics.Path().apply {
            sortedRef.forEachIndexed { i, row ->
                val px = xToPx(row.x); val py = yToPx(row.pMedian)
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
        }
        drawPath(
            medianPath,
            color = Color(0xFF898781),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
            ),
        )

        // ---- Pink actual-data line + points ----
        if (sortedActual.isNotEmpty()) {
            val actualPath = androidx.compose.ui.graphics.Path().apply {
                sortedActual.forEachIndexed { i, (x, y) ->
                    val px = xToPx(x); val py = yToPx(y)
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
            }
            drawPath(
                actualPath,
                color = PinkPrimary,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round,
                ),
            )
            sortedActual.forEach { (x, y) ->
                drawCircle(color = PinkPrimary, radius = 4.dp.toPx(), center = Offset(xToPx(x), yToPx(y)))
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(xToPx(x), yToPx(y)),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()),
                )
            }
        }

        // ---- Minimal axis labels (min/max only, to keep this simple) ----
        val labelPaint = Paint().asFrameworkPaint().apply {
            color = TextMuted.toArgb()
            textSize = 10.sp.toPx()
            isAntiAlias = true
        }
        drawContext.canvas.nativeCanvas.apply {
            drawText("${yMin.toInt()} $satuan", 0f, yToPx(yMin), labelPaint)
            drawText("${yMax.toInt()} $satuan", 0f, yToPx(yMax) + 10.sp.toPx(), labelPaint)
            drawText("${xMin.toInt()} bln", paddingLeft, size.height - 4.dp.toPx(), labelPaint)
            drawText("${xMax.toInt()} bln", size.width - paddingRight - 30.dp.toPx(), size.height - 4.dp.toPx(), labelPaint)
        }
    }
}

/** Keterangan grafik dalam satu baris: garis anak, median WHO, rentang normal WHO. */
@Composable
fun GrowthChartLegend(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendItem("Anak") {
            drawLine(PinkPrimary, Offset(0f, center.y), Offset(size.width, center.y), strokeWidth = 2.5.dp.toPx())
            drawCircle(PinkPrimary, radius = 3.dp.toPx(), center = center)
        }
        LegendItem("Median WHO") {
            drawLine(
                Color(0xFF898781), Offset(0f, center.y), Offset(size.width, center.y),
                strokeWidth = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
            )
        }
        LegendItem("Rentang normal") {
            drawRect(Color(0xFFB4B2A9).copy(alpha = 0.35f))
        }
    }
}

@Composable
private fun LegendItem(label: String, swatch: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(width = 18.dp, height = 10.dp), onDraw = swatch)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(start = 6.dp))
    }
}
