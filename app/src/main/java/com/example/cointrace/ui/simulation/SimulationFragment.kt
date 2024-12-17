package com.example.cointrace.ui.simulation
import com.example.cointrace.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.app.DatePickerDialog
import java.util.Calendar

import android.widget.*
import androidx.core.widget.addTextChangedListener

class SimulationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simulation, container, false)
    }
    private fun validateFields(
        montant: EditText,
        dateInput1: EditText,
        dateInput2: EditText,
        frequencySpinner: Spinner,
        cryptoSpinner: Spinner,
        submitButton: Button
    ) {
        // Vérifie si tous les champs sont remplis
        val isMontantFilled = montant.text.toString().isNotEmpty()
        val isDateInput1Filled = dateInput1.text.toString().isNotEmpty()
        val isDateInput2Filled = dateInput2.text.toString().isNotEmpty()


        // Active le bouton seulement si tout est rempli
        submitButton.isEnabled = isMontantFilled && isDateInput1Filled && isDateInput2Filled
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateInput1: EditText = view.findViewById(R.id.date_input1)
        val dateInput2: EditText = view.findViewById(R.id.date_input2)
        val montant: EditText = view.findViewById(R.id.montant_input)
        val cryptoSpinner: Spinner = view.findViewById(R.id.crypto_spinner)
        val frequencySpinner: Spinner = view.findViewById(R.id.frequency_spinner)
        val submitButton: Button = view.findViewById(R.id.submit_button)
        val resultContainer: TextView = view.findViewById(R.id.result_container)

        submitButton.isEnabled = false

        val validate = {
            validateFields(dateInput1, dateInput2, montant, cryptoSpinner, frequencySpinner, submitButton)
        }
        montant.addTextChangedListener{ validate() }
        dateInput1.addTextChangedListener { validate() }
        dateInput2.addTextChangedListener { validate() }

        val adapterCrypto = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_crypto_options,
            android.R.layout.simple_spinner_item
        )
        adapterCrypto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cryptoSpinner.adapter = adapterCrypto

        val adapterFrequency = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_frequency_options,
            android.R.layout.simple_spinner_item
        )
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapterFrequency

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        dateInput1.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput1.setText(date)
            }, year, month, day)
            datePicker.show()
        }
        dateInput2.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput2.setText(date)
            }, year, month, day)
            datePicker.show()
        }
        cryptoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validate()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        frequencySpinner    .onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validate()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        submitButton.setOnClickListener {
            val selectedDate1 = dateInput1.text.toString()
            val selectedDate2 = dateInput2.text.toString()
            val montantInput = montant.text.toString()
            val selectedCrypto = cryptoSpinner.selectedItem.toString()
            val selectedFrequency = frequencySpinner.selectedItem.toString()
            val result = """
                Date de début : $selectedDate1
                Date de fin : $selectedDate2
                montant : $montantInput
                Crypto choisi : $selectedCrypto
                Fréquence défini : $selectedFrequency
            """.trimIndent()

            resultContainer.text = result
            resultContainer.visibility = View.VISIBLE
        }
    }
}