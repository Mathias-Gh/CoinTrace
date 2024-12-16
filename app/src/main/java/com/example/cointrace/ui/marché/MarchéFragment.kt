package com.example.cointrace.ui.marche

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrace.databinding.FragmentMarcheBinding

class MarchéFragment : Fragment() {

    private var _binding: FragmentMarcheBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val marchéViewModel =
            ViewModelProvider(this)[MarchéViewModel::class.java]

        _binding = FragmentMarcheBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMarch
        marchéViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}