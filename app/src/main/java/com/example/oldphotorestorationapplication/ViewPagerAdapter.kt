package com.example.oldphotorestorationapplication

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(private val context: Context, private val photoArray: Array<Bitmap?>): PagerAdapter() {

    override fun getCount(): Int {
        return photoArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var imageView = ImageView(context)
        imageView.setImageBitmap(photoArray[position])
        container.addView(imageView, 0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}