package com.example.tasksapp.data.database

import androidx.room.*
import com.example.tasksapp.model.TaskModel

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskModel)

    @Query("SELECT * FROM tasks")
    suspend fun getTasksList(): List<TaskModel>

    @Delete
    suspend fun deleteTask(taskModel: TaskModel)

    @Update
    suspend fun updateTask(taskModel: TaskModel)

    @Query("SELECT * FROM tasks WHERE date = :date")
    suspend fun getTasksListByDate(date: String): List<TaskModel>

}