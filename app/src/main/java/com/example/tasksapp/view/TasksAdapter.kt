package com.example.tasksapp.view

import android.graphics.Paint
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tasksapp.R
import com.example.tasksapp.databinding.ItemToDoBinding
import com.example.tasksapp.model.TaskModel
import com.example.tasksapp.utils.CommonUtils
import java.util.Calendar

class TasksAdapter(
    var dataList: MutableList<TaskModel>,
    val recyclerItemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ItemToDoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind()
    }

    inner class ItemViewHolder(var binding: ItemToDoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind() {

            val model = dataList[layoutPosition]

            if (CommonUtils.getCurrentDate() == model.date) {
                binding.tvTime.text = model.time
            } else {
                binding.tvTime.text = "${model.date} ${model.time}"
            }
            binding.tvTitle.text = model.title
            binding.cbITD.isChecked = model.isComplete
            if (!model.isComplete) {
                checkForPending(model)
            } else {
                binding.lblPending.visibility = View.GONE
                binding.tvTitle.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            this.context,
                            R.color.black
                        )
                    )
                }
                binding.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.ivCross.setOnClickListener {
                it.setTag(R.id.ivCross, model)
                it.setTag(R.id.tvTitle, layoutPosition)
                recyclerItemClickListener.onClick(it)
            }
            binding.cbITD.setOnClickListener {
                model.isComplete = binding.cbITD.isChecked
                if (model.isComplete) {
                    binding.lblPending.visibility = View.GONE
                    binding.tvTitle.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.black
                            )
                        )
                    }
                    binding.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    checkForPending(model)
                }
                it.setTag(R.id.cbITD, model)
                recyclerItemClickListener.onClick(it)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun checkForPending(model: TaskModel) {
            binding.tvTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            val currentMeridiem =
                DateFormat.format("AAA", Calendar.getInstance().time).toString().uppercase()
            if (model.meridiem.equals(currentMeridiem)) {
                var modelTime = model.time.toString()
                if (modelTime.isNotEmpty() && modelTime.split(" ")[0].split(":")[0].length < 2) {
                    modelTime = "0$modelTime"
                } else {
                    //do nothing
                }
                if (CommonUtils.isTimeGreater(
                        CommonUtils.getCurrentDateAndTime().uppercase(),
                        "${model.date} $modelTime"
                    )
                ) {
                    binding.lblPending.visibility = View.VISIBLE
                    binding.tvTitle.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.text_color_red
                            )
                        )
                    }
                } else {
                    binding.lblPending.visibility = View.GONE
                    binding.tvTitle.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.black
                            )
                        )
                    }
                }
            } else {
                if (currentMeridiem == binding.tvTime.context.getString(R.string.am)) {
                    binding.lblPending.visibility = View.GONE
                    binding.tvTitle.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.black
                            )
                        )
                    }
                } else {
                    binding.lblPending.visibility = View.VISIBLE
                    binding.tvTitle.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.text_color_red
                            )
                        )
                    }
                }
            }
        }
    }

    fun deleteTask(model: TaskModel, position: Int) {
        dataList.remove(model)
        notifyItemRemoved(position)
    }

}
