package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.Bitmap;

import com.kangwang.cramelibrary.filter.BaseFilter;
import com.kangwang.cramelibrary.filter.BeautyFilter;
import com.kangwang.cramelibrary.filter.CoolFilter;
import com.kangwang.cramelibrary.filter.OriginalFilter;

public class FilterFactory {
    public static BaseFilter createFilter(Context context, int type){
        BaseFilter filter = null;
        switch (type){
            case 1:
                filter = new CoolFilter(context);
                break;
            case 2:
                filter = new OriginalFilter(context);
                break;
            case 3:
                filter = new BeautyFilter(context);
                ((BeautyFilter)(filter)).setSmoothOpacity(0.1F);
                break;
        }
        if (filter == null){
            filter = new OriginalFilter(context);
        }
        return filter;
    }
}
