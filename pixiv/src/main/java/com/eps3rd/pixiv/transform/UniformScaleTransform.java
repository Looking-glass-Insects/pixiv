package com.eps3rd.pixiv.transform;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;


public class UniformScaleTransform extends ImageViewTarget<Bitmap> {

    private ImageView target;
    private boolean changeSize;

    public UniformScaleTransform(ImageView target, boolean c) {
        super(target);
        this.target = target;
        this.changeSize = c;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        super.onResourceReady(resource, transition);
        if (changeSize) {
            //获取原图的宽高
            int width = resource.getWidth();
            int height = resource.getHeight();

            //获取imageView的宽
            int imageViewWidth = target.getWidth();


            //计算图片等比例放大后的高
            int imageViewHeight = (height * imageViewWidth) / width;
            ViewGroup.LayoutParams params = target.getLayoutParams();
            params.height = imageViewHeight;
            Log.d("eps3rd", "height:" + imageViewHeight + "," + imageViewWidth);
            target.setLayoutParams(params);
        }
    }

    @Override
    protected void setResource(@Nullable Bitmap resource) {
        view.setImageBitmap(resource);
    }
}
