package com.example.tasksapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasksapp.data.database.TaskRepository
import com.example.tasksapp.model.TaskModel

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    var tasksList: MutableLiveData<List<TaskModel>?> = MutableLiveData()

    suspend fun insertTask(task: TaskModel) = repository.insertTask(task)

    suspend fun updateTask(task: TaskModel) = repository.updateTask(task)

    suspend fun deleteTask(task: TaskModel) = repository.deleteTask(task)

    suspend fun getAllTasks(): List<TaskModel> = repository.getAllTasks()

    suspend fun getAllTasksByDate(date: String) = repository.getAllTasksByDate(date)
}
