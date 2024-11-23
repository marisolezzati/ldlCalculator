package com.example.ldlcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ldlcalculator.ui.theme.LdlCalculatorTheme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LdlCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LdlCalculatorLayout()
                }
            }
        }
    }
}

@Composable
fun LdlCalculatorLayout() {
    var totalCholesterol by remember { mutableStateOf("") }
    var hdlCholesterol by remember { mutableStateOf("") }
    var triglycerides by remember { mutableStateOf("") }

    val total = totalCholesterol.toDoubleOrNull() ?: 0.0
    val hdl = hdlCholesterol.toDoubleOrNull() ?: 0.0
    val trig = triglycerides.toDoubleOrNull() ?: 0.0
    val ldl = calculateLdl(total, hdl, trig)
    val evaluation = evaluateLdl(ldl)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_ldl),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        MeasureField(
            label = R.string.total_cholesterol,
            value = totalCholesterol,
            onValueChanged = { totalCholesterol = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        MeasureField(
            label = R.string.hdl_cholesterol,
            value = hdlCholesterol,
            onValueChanged = { hdlCholesterol = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        MeasureField(
            label = R.string.triglycerides,
            value = triglycerides,
            onValueChanged = { triglycerides = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.ldl_cholesterol, NumberFormat.getNumberInstance().format(ldl)),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 20.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Text(
            text = stringResource(R.string.ldl_cholesterolev, evaluation),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 20.sp,
            modifier = Modifier.align(alignment = Alignment.Start)
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun MeasureField(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
    )
}

/**
 * Calculates the LDL by formula:
 * Cholesterol LDL = Colesterol total - ( Colesterol VLDL + Colesterol HDL)
 * Colesterol VLDL = Triglicéridos / 5
 */
private fun calculateLdl(totalCholesterol: Double, hdlCholesterol: Double, triglycerides: Double): Double {
    var ldlCholesterol = totalCholesterol - ( triglycerides/5 + hdlCholesterol)
    return ldlCholesterol
}



/**
 * Evaluates the LDL result by this criteria:
 *  Optimal: Less than 100 mg/dL
 *  Near-optimal: 100-129;
 *  Borderline high: 130-159;
 *  High: 160-189;
 *  Very high: 190 or higher
 */
fun evaluateLdl(ldl: Double): String {
    var evaluation = ""
    when {
        ldl > 0 &&  ldl < 100 -> evaluation = "Optimal"
        ldl >= 100 && ldl < 130 -> evaluation = "Near-optimal"
        ldl >= 130 && ldl < 160 -> evaluation = "Borderline high"
        ldl >= 160 && ldl < 190 -> evaluation = "High"
        ldl >= 190 -> evaluation = "Very high"
        else -> ""
    }
    return evaluation
}

@Preview(showBackground = true)
@Composable
fun LdlCalculatorLayoutPreview() {
    LdlCalculatorTheme {
        LdlCalculatorLayout()
    }
}