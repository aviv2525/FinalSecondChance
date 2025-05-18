package com.example.secondchance.adapter

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.secondchance.R
import com.example.secondchance.data.model.Product
import com.example.secondchance.databinding.ItemProductBinding
import com.google.android.material.card.MaterialCardView
import androidx.core.net.toUri

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onItemLongClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))

        (holder.itemView as MaterialCardView).setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleUp = AnimationUtils.loadAnimation(view.context, R.anim.scale_up)
                    view.startAnimation(scaleUp)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val scaleDown = AnimationUtils.loadAnimation(view.context, R.anim.scale_down)
                    view.startAnimation(scaleDown)
                }
            }
            false
        }
        val context = holder.itemView.context
        val layoutParams = holder.itemView.layoutParams
        val orientation = context.resources.configuration.orientation
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density

        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val spacingPx = (8 * density).toInt()

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val maxWidthPx = (300 * density).toInt()
                layoutParams.width = maxWidthPx

                val screenWidth = displayMetrics.widthPixels
                val sideMargin = ((screenWidth - maxWidthPx) / 2).coerceAtMost((16 * density).toInt())
                layoutParams.setMargins(sideMargin, spacingPx, sideMargin, spacingPx)
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.setMargins(spacingPx, spacingPx, spacingPx, spacingPx)
            }

            holder.itemView.layoutParams = layoutParams
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = product.price

            val context = binding.root.context
            val imageSource = product.imageUri

            if (!imageSource.isNullOrEmpty()) {
                Glide.with(context)
                    .load(Uri.parse(imageSource))
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivProductImage)
            } else {
                Glide.with(context)
                    .load(product.imageRes)
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivProductImage)
            }

            binding.root.setOnClickListener { onItemClick(product) }
            binding.root.setOnLongClickListener {
                onItemLongClick(product)
                true
            }
        }

    }
}
