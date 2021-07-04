package com.app.video.videoapps.adapter.video;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.video.videoapps.R;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.bean.CollectBean;
import com.app.video.videoapps.bean.StarItemBean;
import com.app.video.videoapps.event.CollectStarEvent;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.utils.GlideUtils;
import com.app.video.videoapps.utils.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.app.video.videoapps.http.AppConfig.TOKEN;

public class PlayStarAdapter extends BaseMultiItemQuickAdapter<StarItemBean, BaseViewHolder> {


    public PlayStarAdapter(List<StarItemBean> data) {
        super(data);
        setDefaultViewTypeLayout(R.layout.item_star);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final StarItemBean item) {
        ImageView image = holder.getView(R.id.image);
        GlideUtils.loadCircleImagView(mContext, item.getPic(), image);

        holder.setText(R.id.tv_name, item.getUname());

        //是否已收藏 1：已收藏 0：未收藏
        final TextView tvCollect = holder.getView(R.id.tv_collect);
        if (item.getIs_collect() == 1) {
            setCollected(tvCollect);
        } else {
            setNoCollected(tvCollect);
        }

        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getIs_collect() == 1) {
                    delCollect(tvCollect, item);
                } else {
                    addCollect(tvCollect, item);
                }
            }
        });
    }

    private void setNoCollected(TextView tvCollect) {
        tvCollect.setTextColor(mContext.getResources().getColor(R.color.c_ff6c00));
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_box_20).mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_ff6c00)));
        tvCollect.setBackground(drawable);
    }

    private void setCollected(TextView tvCollect) {
        tvCollect.setTextColor(mContext.getResources().getColor(R.color.white));

        Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_my_vip);
        tvCollect.setBackground(drawable);
    }


    private void addCollect(final TextView tvCollect, final StarItemBean item) {
        Client.getApiService().addCollect(TOKEN, item.getSid() + "", "1")
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(item.getSid());
                            collectBean.setName(item.getUname());
                            collectBean.setPic(item.getPic());

                            EventBus.getDefault().post(new CollectStarEvent("PLAYSTSR", true, collectBean));
                            setCollected(tvCollect);
                            item.setIs_collect(1);
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }

    private void delCollect(final TextView tvCollect, final StarItemBean item) {
        Client.getApiService().delCollect(TOKEN, item.getSid() + "", "1")
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(item.getSid());

                            EventBus.getDefault().post(new CollectStarEvent("PLAYSTSR", false, collectBean));
                            setNoCollected(tvCollect);
                            item.setIs_collect(0);
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }
}