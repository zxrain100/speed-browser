package com.sdb.ber.sl

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.sdb.ber.dt.Info
import com.sdb.ber.R
import com.sdb.ber.sr.SDBUtils
import com.sdb.ber.databinding.ListItemBinding

class MHAdapter : ListAdapter<Info, MHAdapter.ItemHolder>(InfoDiffCallback) {

    class ItemHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = getItem(position)

        val bind = holder.binding
        val iconUrl = SDBUtils.getIconUrl(item.url)

        Glide.with(bind.root.context)
            .load(iconUrl)
            .error(R.drawable.sh_item_def_bg)
            .placeholder(R.drawable.sh_item_def_bg)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(bind.img)
        bind.title.text = item.title
        bind.host.text = Uri.parse(item.url).host

        bind.pageDelete.setOnClickListener {
            listener?.invoke(position, item)
        }
        bind.root.setOnClickListener {
            click?.invoke(position, item)
        }
    }



    private var listener: ((Int, Info) -> Unit)? = null

    fun setOnDeleteListener(l: (Int, Info) -> Unit) {
        listener = l
    }

    private var click: ((Int, Info) -> Unit)? = null
    fun setOnItemClickListener(c: (Int, Info) -> Unit) {
        this.click = c
    }


    object InfoDiffCallback : DiffUtil.ItemCallback<Info>() {
        override fun areItemsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem.url == newItem.url
        }
    }

}

