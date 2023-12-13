package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryChooserBinding
import ru.practicum.android.diploma.filter.domain.models.Industry
import ru.practicum.android.diploma.filter.presentation.IndustryViewModel
import ru.practicum.android.diploma.filter.ui.adapters.IndustryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class IndustryChooserFragment : Fragment(R.layout.fragment_industry_chooser) {

    private var _binding: FragmentIndustryChooserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IndustryViewModel by viewModel()

    private val adapter = IndustryAdapter(onItemCheckedListener = { item ->
        if (item.isChecked) {
            binding.btAdd.visibility = View.VISIBLE
            binding.btAdd.setOnClickListener {
                val action =
                    IndustryChooserFragmentDirections.actionIndustryChooserFragmentToFilterFragment(
                        industryArgs = item.name
                    )
                findNavController().navigate(action)
            }
        }

    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIndustryChooserBinding.bind(view)

        binding.rvIndustry.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvIndustry.adapter = adapter
        binding.rvIndustry.itemAnimator = null

        lifecycleScope.launch {
            viewModel.getIndustries().collect {industriesList ->
                adapter.updateData(industriesList)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("testTextWatcher", "$s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: " ")
                visibleBtAdd(adapter.listItem)

                val edText = binding.searchEditText
                if (!s.isNullOrEmpty()) {
                    edText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                    edText.setOnTouchListener { v, event ->
                        val iconBoundries = edText.compoundDrawables[2].bounds.width()
                        if (event.action == MotionEvent.ACTION_UP &&
                            event.rawX >= edText.right - iconBoundries * 2
                        ) {
                            edText.setText("")
                        }
                        view.performClick()
                        false
                    }
                } else {
                    edText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("testTextWatcher", "$s")
            }
        }
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    fun visibleBtAdd(list: List<Industry>) {
        val result = list.find { it.isChecked }
        if (result != null) {
            binding.btAdd.visibility = View.VISIBLE
        } else {
            binding.btAdd.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
