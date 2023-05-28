package com.example.tasksapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tasksapp.model.TaskModel

@Database(entities = [TaskModel::class], version = 1, exportSchema = false)
abstract class TasksAppDB : RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        private const val DB_NAME = "note_database.db"
        @Volatile private var instance: TasksAppDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TasksAppDB::class.java,
            DB_NAME
        ).build()
    }

}