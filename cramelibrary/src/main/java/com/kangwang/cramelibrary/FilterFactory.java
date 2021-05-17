package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.Bitmap;

import com.kangwang.cramelibrary.filter.BaseFilter;
import com.kangwang.cramelibrary.filter.CoolFilter;
import com.kangwang.cramelibrary.filter.OriginalFilter;

public class FilterFactory {
    public static BaseFilter createFilter(Context context, int type){
        BaseFilter filter = null;
        switch (type){
            case 1:
                filter = new CoolFilter(context);
                break;
        }
        if (filter == null){
            filter = new OriginalFilter(context);
        }
        return filter;
    }
}
