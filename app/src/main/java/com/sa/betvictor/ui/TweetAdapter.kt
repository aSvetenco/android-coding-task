package com.sa.betvictor.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sa.betvictor.R
import com.sa.betvictor.domain.Tweet
import kotlinx.android.synthetic.main.item_tweet.view.*

class TweetAdapter : ListAdapter<Tweet, TweetAdapter.TweetViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder =
        TweetViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_tweet, parent, false)
        )

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.itemView.tweet.text = getItem(position).text
    }

    class TweetViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class ItemCallback : DiffUtil.ItemCallback<Tweet>() {
        override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet)
                = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet) = oldItem == newItem
    }

}