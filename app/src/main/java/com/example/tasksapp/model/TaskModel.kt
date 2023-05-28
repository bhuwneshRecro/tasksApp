package com.example.tasksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) var dbId: Int = 0,
    var meridiem: String? = null,
    var title: String? = null,
    var time: String? = null,
    var date: String? = null,
    var isComplete: Boolean = false
)
