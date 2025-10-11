package com.example.androidbigapp.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidbigapp.data.CharacterResponse
import com.example.androidbigapp.databinding.CharacterBinding

class ApiResponseAdapter(private var items: List<CharacterResponse>) : RecyclerView.Adapter<ApiResponseAdapter.ApiResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiResponseViewHolder {
        val binding = CharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiResponseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApiResponseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<CharacterResponse>) {
        this.items = newData
        notifyDataSetChanged()
    }

    class ApiResponseViewHolder(private val binding: CharacterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(apiResponse: CharacterResponse) {
            with(binding) {
                tvItemName.text = apiResponse.characterName ?: "-"
                tvItemHouseName.text = apiResponse.houseName?.get(0) ?: "-"
                tvItemGender.text = apiResponse.gender ?: "-"
                tvItemActorName.text = apiResponse.actorName ?: "-"
                tvItemParent.text = apiResponse.parents?.get(0) ?: "-"
            }
        }
    }
}