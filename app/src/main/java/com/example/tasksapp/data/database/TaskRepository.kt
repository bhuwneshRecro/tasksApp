package com.example.tasksapp.data.database

import com.example.tasksapp.model.TaskModel


class TaskRepository(
    private val tasksDatabase: TasksAppDB
) {

    suspend fun insertTask(task: TaskModel) = tasksDatabase.roomDao().addTask(task)

    suspend fun updateTask(note: TaskModel) = tasksDatabase.roomDao().updateTask(note)

    suspend fun deleteTask(note: TaskModel) = tasksDatabase.roomDao().deleteTask(note)

    suspend fun getAllTasks(): List<TaskModel> = tasksDatabase.roomDao().getTasksList()

    suspend fun getAllTasksByDate(date: String): List<TaskModel> =
        tasksDatabase.roomDao().getTasksListByDate(date)
}