package com.example.tasksapp.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.tasksapp.R
import com.example.tasksapp.databinding.DialogConfirmationBinding

class ConfirmationDialog(
    private val title:String,
    private val clickListener: View.OnClickListener
) : DialogFragment() {

    private lateinit var binding: DialogConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogConfirmationBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        setupData()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupData() {
        binding.tvTitle.text = String.format(resources.getString(R.string.confirmation_message), title)

        clickListeners()
    }

    private fun clickListeners() {
        binding.tvOK.setOnClickListener{
            clickListener.onClick(it)
            dismiss()
        }
        binding.tvCancel.setOnClickListener{
            clickListener.onClick(it)
            dismiss()
        }
    }

}