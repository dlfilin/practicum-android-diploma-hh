package ru.practicum.android.diploma.filter.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ItemAreaChooserBinding
import ru.practicum.android.diploma.filter.domain.models.Area

class AreaAdapter(private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<AreaAdapter.AreaHolder>() {

    var listItem: MutableList<Area> = ArrayList()
    private val originalListItem: MutableList<Area> = ArrayList()
    private val filterListItem: MutableList<Area> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_area_chooser, parent, false)
        return AreaHolder(view)
    }

    override fun onBindViewHolder(holder: AreaHolder, position: Int) {
        holder.bind(listItem[position])
        holder.itemView.setOnClickListener { itemClickListener.onItemListener(listItem[position]) }
    }

    override fun getItemCount(): Int = listItem.size


    class AreaHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemAreaChooserBinding.bind(item)
        fun bind(area: Area) = with(binding) {
            areaName.text = area.name
        }
    }


    private class MyDiffCallback(private val oldList: List<Area>, private val newList: List<Area>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    fun updateData(newData: List<Area>) {
        val diffCallback = MyDiffCallback(listItem, newData)
        val diffRisult = DiffUtil.calculateDiff(diffCallback)
        listItem.clear()
        listItem.addAll(newData)
        diffRisult.dispatchUpdatesTo(this)
        originalListItem.clear()
        originalListItem.addAll(newData)
    }

    private fun updateDisplayList(updateList: List<Area>) {
        val diffCallback = MyDiffCallback(listItem, updateList)
        val diffRisult = DiffUtil.calculateDiff(diffCallback)
        listItem.clear()
        listItem.addAll(updateList)
        diffRisult.dispatchUpdatesTo(this)
    }

    fun filter(searchQuery: String?) {
        filterListItem.clear()
        if (searchQuery.isNullOrBlank()) {
            updateDisplayList(originalListItem)
        } else {
            for (item in originalListItem) {
                if (item.name.contains(searchQuery, true)) {
                    filterListItem.add(item)
                }
            }
            updateDisplayList(filterListItem)
        }
    }

}
