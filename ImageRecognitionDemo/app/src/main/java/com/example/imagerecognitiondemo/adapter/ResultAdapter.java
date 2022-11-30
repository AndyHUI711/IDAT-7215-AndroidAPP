package com.example.imagerecognitiondemo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.imagerecognitiondemo.R;
import com.example.imagerecognitiondemo.model.GetResult;

import java.util.List;

public class ResultAdapter extends BaseQuickAdapter<GetResult.ResultBean, BaseViewHolder> {
    public ResultAdapter(int layoutResId, @Nullable List<GetResult.ResultBean> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, GetResult.ResultBean resultBean) {
        baseViewHolder.setText(R.id.tv_keyword, resultBean.getKeyword())
                .setText(R.id.tv_root, resultBean.getRoot())
                .setText(R.id.tv_score, String.valueOf(resultBean.getScore()));
    }
}

