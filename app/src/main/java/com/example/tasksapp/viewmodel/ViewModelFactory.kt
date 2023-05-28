package com.example.tasksapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tasksapp.data.database.TaskRepository

class ViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            val constructor = modelClass.getDeclaredConstructor(TaskRepository::class.java)
            return constructor.newInstance(repository)
        } catch (e: Exception) {
            Log.d(e.message.toString(), "")
        }
        return super.create(modelClass)
    }
}