package com.kangwang.cramelibrary.filter;

import android.content.Context;
import com.kangwang.cramelibrary.R;
public class OriginalFilter extends BaseFilter {

    public OriginalFilter(Context c) {
        super(c);
    }

    @Override
    public void setshaderpath() {

        vertextShader = R.raw.base_vertex_shader;
        fragtextShader = R.raw.base_fragment_shader;

    }

    @Override
    public void onDrawArraysPre() {

    }

    @Override
    public void onDrawArraysAfter() {

    }


}
