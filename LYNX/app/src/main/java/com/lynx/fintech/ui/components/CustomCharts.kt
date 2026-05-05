package com.lynx.fintech.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lynx.fintech.domain.model.CategoryBreakdown
import com.lynx.fintech.domain.model.MonthlyDataPoint
import com.lynx.fintech.ui.theme.DividerDark
import com.lynx.fintech.ui.theme.Emerald
import com.lynx.fintech.ui.theme.ExpenseRed
import com.lynx.fintech.ui.theme.IncomeGreen
import com.lynx.fintech.ui.theme.ObsidianLight
import com.lynx.fintech.ui.theme.TextPrimary
import com.lynx.fintech.ui.theme.TextSecondary
import com.lynx.fintech.ui.theme.TextTertiary
import kotlin.math.max

@Composable
fun DoughnutChart(
    data: List<CategoryBreakdown>,
    modifier: Modifier = Modifier,
    centerText: String = "",
    centerSubtext: String = ""
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }

    val totalAmount = data.sumOf { it.amount }
    val colors = data.map { categoryBreakdown ->
        val colorHex = categoryBreakdown.category.colorHex
        Color(android.graphics.Color.parseColor(colorHex))
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2
            val radius = (canvasWidth.coerceAtMost(canvasHeight) / 2) * 0.8f
            val strokeWidth = 32f

            if (data.isEmpty() || totalAmount <= 0) {
                drawArc(
                    color = ObsidianLight,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                return@Canvas
            }

            var startAngle = -90f
            val animatedSweepAngles = data.map { categoryBreakdown ->
                val sweep = (categoryBreakdown.amount / totalAmount) * 360f
                sweep * animationProgress.value
            }

            animatedSweepAngles.forEachIndexed { index, sweepAngle ->
                if (sweepAngle > 0) {
                    drawArc(
                        color = colors.getOrElse(index) { Emerald },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (centerText.isNotEmpty()) {
                Text(
                    text = centerText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (centerSubtext.isNotEmpty()) {
                Text(
                    text = centerSubtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun DoughnutChartLegend(
    data: List<CategoryBreakdown>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.take(5).forEach { categoryBreakdown ->
            val color = Color(android.graphics.Color.parseColor(categoryBreakdown.category.colorHex))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(
                        color = color,
                        radius = size.minDimension / 2,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = categoryBreakdown.category.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(categoryBreakdown.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<MonthlyDataPoint>,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    val maxValue = data.maxOfOrNull { max(it.income, it.expense) } ?: 1.0

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val paddingLeft = 40f
            val paddingRight = 16f
            val paddingTop = 24f
            val paddingBottom = 32f
            val chartWidth = canvasWidth - paddingLeft - paddingRight
            val chartHeight = canvasHeight - paddingTop - paddingBottom

            val barGroupWidth = chartWidth / data.size
            val barWidth = barGroupWidth * 0.35f
            val spacing = barGroupWidth * 0.1f

            // Draw grid lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = paddingTop + (chartHeight / gridLines) * i
                drawLine(
                    color = DividerDark,
                    start = Offset(paddingLeft, y),
                    end = Offset(canvasWidth - paddingRight, y),
                    strokeWidth = 1f
                )
            }

            data.forEachIndexed { index, dataPoint ->
                val groupCenterX = paddingLeft + (barGroupWidth * index) + (barGroupWidth / 2)
                val incomeBarHeight = ((dataPoint.income / maxValue) * chartHeight * animationProgress.value).toFloat()
                val expenseBarHeight = ((dataPoint.expense / maxValue) * chartHeight * animationProgress.value).toFloat()

                // Income bar
                drawRect(
                    color = IncomeGreen,
                    topLeft = Offset(
                        groupCenterX - barWidth - spacing / 2,
                        paddingTop + chartHeight - incomeBarHeight
                    ),
                    size = Size(barWidth, incomeBarHeight)
                )

                // Expense bar
                drawRect(
                    color = ExpenseRed,
                    topLeft = Offset(
                        groupCenterX + spacing / 2,
                        paddingTop + chartHeight - expenseBarHeight
                    ),
                    size = Size(barWidth, expenseBarHeight)
                )
            }
        }
    }
}

@Composable
fun BarChartLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawRect(
                    color = IncomeGreen,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Income",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawRect(
                    color = ExpenseRed,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
