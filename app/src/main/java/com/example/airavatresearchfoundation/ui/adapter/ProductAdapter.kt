package com.example.airavatresearchfoundation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.databinding.ItemProductBinding

class ProductAdapter :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private val productList = ArrayList<Product>()
    fun setProducts(list: List<Product>) {
        Log.d("Adapter", "List size = ${list.size}")
        productList.clear()
        productList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = productList[position]

        holder.binding.title.text = product.title
        holder.binding.price.text = "₹ ${product.price}"

        Glide.with(holder.itemView.context)
            .load(product.thumbnail)
            .into(holder.binding.image)
    }
}