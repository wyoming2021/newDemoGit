package com.app.video.videoapps.bean;

import com.app.video.videoapps.adapter.my.ProblemAdviseFragmentAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ProblemAdviseLv1Bean implements MultiItemEntity {
    @Override
    public int getItemType() {
        return ProblemAdviseFragmentAdapter.TYPE_LEVEL_1;
    }

}
