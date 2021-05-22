package com.example.oldphotorestorationapplication

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView

class ViewPagerAdapter(private val context: Context, private val photoArray: Array<Bitmap?>) :
    PagerAdapter() {

    override fun getCount(): Int {
        return photoArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //        var imageView = ImageView(context)
        var photoView = PhotoView(context)
        photoView.setImageBitmap(photoArray[position])
        container.addView(photoView, 0)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}
