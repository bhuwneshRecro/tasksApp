package com.example.tasksapp.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.tasksapp.R
import com.example.tasksapp.databinding.FragmentAddToDoBinding
import com.example.tasksapp.model.TaskModel
import com.example.tasksapp.utils.CommonUtils
import com.example.tasksapp.viewmodel.TasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddToDoFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAddToDoBinding
    private val viewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScreen()
    }

    private fun setupScreen() {
        binding.ivBackFATD.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
        setAMPMAdapter()
    }

    private fun setAMPMAdapter() {
        val modes = listOf("AM", "PM")
        val adapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            modes
        )
        binding.atvAA.setAdapter(adapter)
    }

    private fun checkForm(): Boolean {
        CommonUtils.hideKeyboard(requireActivity(), binding.root)
        if (binding.etTitle.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.etTime.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter time", Toast.LENGTH_SHORT).show()
            return false
        } else {
            var time = binding.etTime.text.toString()
            if (!time.contains(":")) {
                if (time.toInt() > 12 || time.toInt() < 1) {
                    Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
                time = time.plus(":00")
                binding.etTime.setText(time)
            } else if (!time.contains(":") && time.toInt() < 1) {
                Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else {
                if (time.split(":").size > 2) {
                    Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                        .show()
                    return false
                } else {
                    if (time.split(":")[0].length > 2) {
                        Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                            .show()
                        return false
                    } else if (time.split(":")[0].isNotEmpty()) {
                        if (time.split(":")[0].toInt() > 12) {
                            Toast.makeText(
                                requireContext(),
                                "Invalid time format",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        } else if (time.split(":")[0].isEmpty() || time.split(":")[0].toInt() < 1) {
                            Toast.makeText(
                                requireContext(),
                                "Invalid time format",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        } else {
                            binding.etTime.setText(time)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Invalid time format",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return false
                    }
                    if (time.split(":")[1].length > 2) {
                        Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                            .show()
                        return false
                    } else if (time.split(":")[1].length == 1) {
                        time = time.plus("0")
                        if (time.split(":")[1].toInt() > 59) {
                            Toast.makeText(
                                requireContext(),
                                "Invalid time format",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        } else
                            binding.etTime.setText(time)
                    } else {
                        if (time.split(":")[1].toInt() > 59) {
                            Toast.makeText(
                                requireContext(),
                                "Invalid time format",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return false
                        } else {
                            binding.etTime.setText(time)
                        }
                    }
                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackFATD, R.id.btnCancel -> {
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.btnAdd -> {
                if (checkForm()) {
                    var time = binding.etTime.text.toString()
                    if (time.split(":")[0].length == 1)
                        time = "0$time"
                    if (CommonUtils.isTimeCheck("$time ${binding.atvAA.text}")) {
                        insertTask(true)
                    } else insertTask(false)
                }
                binding.etTime.setSelection(binding.etTime.length())
            }
        }
    }

    private fun insertTask(isNextDay: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModel.insertTask(
                TaskModel(
                    meridiem = binding.atvAA.text.toString(),
                    time = "${binding.etTime.text.toString()} ${binding.atvAA.text}",
                    title = binding.etTitle.text.toString(),
                    date = if (isNextDay) CommonUtils.getNextDate() else CommonUtils.getCurrentDate()
                )
            )
            val tasks = viewModel.getAllTasks()
            requireActivity().runOnUiThread {
                viewModel.tasksList.value = tasks
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}