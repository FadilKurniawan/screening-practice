package com.devfk.ma.screeningpractice.ui.Adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.Event


class EventAdapter(nameItem: ArrayList<Event>) : BaseAdapter(){
    private val item = nameItem

    @SuppressLint("ViewHolder")
    override fun getView(position:Int, convertView: View?, parent: ViewGroup?):View{
        // Inflate the custom view
        val inflater = parent?.context?.
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view    = inflater.inflate(R.layout.list_item,null)
        // Get the custom view widgets reference
        val title = view.findViewById<TextView>(R.id.txvNameEvent)
        val date = view.findViewById<TextView>(R.id.txvDateEvent)
        val hashtag = view.findViewById<LinearLayout>(R.id.layTagEvent)
        val detail = view.findViewById<TextView>(R.id.txvDetailEvent)
        val image = view.findViewById<ImageView>(R.id.imgEvent)

        title.text = item[position].title
        date.text = item[position].date
        detail.text = item[position].detail

        for (tag in item[position].hashtag){
            var text = TextView(parent.context)
            text.text = tag
            text.setTextColor(parent.context.resources.getColor(R.color.white))
            text.setBackgroundColor(parent.context.resources.getColor(R.color.bluGrey))
            text.layoutParams = getSpacing()
            text.setPadding(5,2,5,2)
            hashtag.addView(text)
        }

        image.setBackgroundResource(item[position].image)
        image.scaleType = ImageView.ScaleType.CENTER_CROP

        return view
    }

    private fun getSpacing(): ViewGroup.LayoutParams? {
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 5, 20, 0)
        return params
    }

    override fun getItem(position: Int): Any? {
        return item[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Count the items
    override fun getCount(): Int {
        return item.size
    }

}