package com.example.explorekotlinflows.first

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.explorekotlinflows.LifeCycleLogger
import com.example.explorekotlinflows.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val viewModel by viewModels<FirstFragmentViewModel>()
    private val logTag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(LifeCycleLogger(logTag))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(LifeCycleLogger("$logTag view"))
        setupListeners(view)
        observeEvents(view)
    }

    private fun observeEvents(view: View) {
        Log.d(logTag, "observeEvents")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            Log.d(logTag, "observeEvents before collect")
            viewModel.events
                .collect { event ->
                    Log.d(logTag, "observeEvents: handle $event")
                    when (event) {
                        FirstFragmentViewModel.FirstFragmentEvents.GoToNextScreen -> {
                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                        }
                        FirstFragmentViewModel.FirstFragmentEvents.MessageFromInit -> {
                            Snackbar.make(view, "MessageFromInit", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun setupListeners(view: View) {
        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            viewModel.onNextClicked()
        }
    }
}