package com.example.tasksapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.tasksapp.utils.CommonUtils
import com.example.tasksapp.R
import com.example.tasksapp.data.database.TasksAppDB
import com.example.tasksapp.data.database.TaskRepository
import com.example.tasksapp.databinding.ActivityMainBinding
import com.example.tasksapp.model.TaskModel
import com.example.tasksapp.viewmodel.TasksViewModel
import com.example.tasksapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TasksViewModel
    private lateinit var db: TasksAppDB
    private lateinit var repository: TaskRepository
    private lateinit var factory: ViewModelFactory
    private lateinit var taskToBeDeleted: TaskModel
    private var deletedPosition = -1
    private var isAscending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TasksAppDB(this)
        repository = TaskRepository(db)
        factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TasksViewModel::class.java]

        setupScreen()
    }

    private fun setupScreen() {
        binding.ivBack.setOnClickListener(this)
        binding.ivAdd.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)

        setObserver()
        getTasksList()
    }

    private fun getTasksList() {
        CoroutineScope(Dispatchers.Default).launch {
            val tasksList = viewModel.getAllTasks()
            runOnUiThread {
                viewModel.tasksList.value = (tasksList)
            }
        }
    }

    private fun setObserver() {
        viewModel.tasksList.observe(this) {
            val sortedList: List<TaskModel>? = if (isAscending) {
                it?.let { it1 -> CommonUtils.sortAsc(it1) }
            } else {
                it?.let { it1 -> CommonUtils.sortDsc(it1) }
            }
            if (sortedList?.isNotEmpty() == true) {
                binding.rvTasks.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.rvTasks.adapter = TasksAdapter(sortedList.toMutableList(), this)
            } else {
                binding.rvTasks.visibility = View.INVISIBLE
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                binding.dpTask.visibility = View.GONE
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.ivAdd -> {
                binding.dpTask.visibility = View.GONE
                supportFragmentManager.commit {
                    addToBackStack(AddToDoFragment::class.java.simpleName)
                    add<AddToDoFragment>(R.id.flContainer)
                }
            }

            R.id.ivCross -> {
                binding.dpTask.visibility = View.GONE
                CommonUtils.genericCastOrNull<TaskModel>(p0.getTag(R.id.ivCross))?.let {
                    taskToBeDeleted = it
                    CommonUtils.genericCastOrNull<Int>(p0.getTag(R.id.tvTitle))?.let { pos ->
                        deletedPosition = pos
                    }
                    ConfirmationDialog(it.title.toString(), this).show(
                        supportFragmentManager, ConfirmationDialog::class.java.name
                    )
                }
            }

            R.id.tvOK -> {
                if (::taskToBeDeleted.isInitialized && deletedPosition >= 0) {
                    (binding.rvTasks.adapter as TasksAdapter).apply {
                        if (dataList.size < 2) {
                            binding.rvTasks.visibility = View.INVISIBLE
                            binding.tvEmpty.visibility = View.VISIBLE
                        }
                        deleteTask(
                            taskToBeDeleted, deletedPosition
                        )
                    }
                    CoroutineScope(Dispatchers.Default).launch {
                        viewModel.deleteTask(taskToBeDeleted)
                    }
                }
            }

            R.id.cbITD -> {
                binding.dpTask.visibility = View.GONE
                CommonUtils.genericCastOrNull<TaskModel>(p0.getTag(R.id.cbITD))?.let {
                    CoroutineScope(Dispatchers.Default).launch {
                        viewModel.updateTask(it.apply { isComplete = it.isComplete })
                    }
                }
            }

            R.id.ivMore -> {
                binding.dpTask.visibility = View.GONE
                val popupMenu = PopupMenu(this, p0)
                popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.itemFilter -> {
                            openDatePicker()
                        }
                        R.id.itemSortAsc -> {
                            if (!isAscending) isAscending = true
                            getTasksList()
                        }
                        R.id.itemSortDsc -> {
                            if (isAscending) isAscending = false
                            getTasksList()
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun openDatePicker() {
        binding.dpTask.visibility = View.VISIBLE
        val today = Calendar.getInstance()
        binding.dpTask.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)

        ) { _, year, mon, day ->
            var month = "${mon + 1}"
            if (month.length < 2) month = "0$month"
            val date = "$day-$month-$year"
            binding.dpTask.visibility = View.GONE
            getTasksByDate(date)
        }
    }

    private fun getTasksByDate(date: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val tasksList = viewModel.getAllTasksByDate(date)
            runOnUiThread {
                viewModel.tasksList.value = (tasksList)
            }
        }
    }
}