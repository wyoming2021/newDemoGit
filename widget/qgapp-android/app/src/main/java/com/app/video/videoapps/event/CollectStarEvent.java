package com.app.video.videoapps.event;

import com.app.video.videoapps.bean.CollectBean;

/**
 * ζΆθ ζζ
 */
public class CollectStarEvent {
    private CollectBean collectBean;
    private boolean isAdd;

    private String from;

    public CollectStarEvent(String from, boolean isAdd, CollectBean collectBean) {
        this.collectBean = collectBean;
        this.isAdd = isAdd;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public CollectBean getCollectBean() {
        return collectBean;
    }

    public void setCollectBean(CollectBean collectBean) {
        this.collectBean = collectBean;
    }
}
