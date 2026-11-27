package fr.antidemarchage

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Local preview only. Never invokes TelecomManager, CallScreeningService, notifications,
 * audio, vibration or any outgoing/incoming call API.
 */
class SimulatorActivity : Activity() {
    private lateinit var input: EditText
    private lateinit var output: TextView
    private val examples = listOf(
        "01 62 45 78 90", "09 48 32 15 67", "02 70 12 34 56",
        "06 12 34 56 78", "07 12 34 56 78", "02 97 12 34 56",
        "0800 112 112", "+33 1 62 45 78 90"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val density = resources.displayMetrics.density
        val pad = (20 * density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        val scroll = ScrollView(this).apply { addView(layout) }
        setContentView(scroll)

        layout.addView(TextView(this).apply {
            text = "Simulateur d’appels fictifs"
            textSize = 23f
        })
        layout.addView(TextView(this).apply {
            text = "TEST UNIQUEMENT — aucun appel, aucune sonnerie, aucun rejet réel, aucun SMS et aucune notification. Le compteur réel ne change pas."
            textSize = 15f
        })
        input = EditText(this).apply {
            hint = "Numéro fictif : 01 62 45 78 90"
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(examples[0])
        }
        layout.addView(input)
        output = TextView(this).apply {
            textSize = 18f
            setPadding(0,pad,0,pad)
        }
        layout.addView(Button(this).apply {
            text = "Tester ce numéro (simulation)"
            setOnClickListener { simulate(input.text.toString()) }
        })
        layout.addView(output)
        layout.addView(TextView(this).apply {
            text = "Exemples à tester :"
            textSize = 17f
        })
        examples.forEach { number ->
            layout.addView(Button(this).apply {
                text = number
                setOnClickListener {
                    input.setText(number)
                    simulate(number)
                }
            })
        }
        simulate(examples[0])
    }

    private fun simulate(number: String) {
        val preferences = getSharedPreferences("filter", Context.MODE_PRIVATE)
        val result = CallDecision.evaluate(number, preferences.getBoolean("enabled", true)) {
            preferences.getBoolean(it, true)
        }
        output.text = when {
            PrefixFilter.normalize(number).isEmpty() ->
                "⚪ Numéro non reconnu : aucun rejet par le filtre (simulation)."
            result.blocked ->
                "🔴 SERAIT REJETÉ — préfixe ${result.prefix}. Le téléphone ne sonnerait pas."
            result.prefix.isNotEmpty() ->
                "🟢 AUTORISÉ — le filtrage général ou le préfixe ${result.prefix} est désactivé."
            else -> "🟢 AUTORISÉ — aucun préfixe filtré."
        }
        output.setTextColor(if(result.blocked) Color.rgb(170, 35, 35) else Color.rgb(20, 110, 75))
    }
}
