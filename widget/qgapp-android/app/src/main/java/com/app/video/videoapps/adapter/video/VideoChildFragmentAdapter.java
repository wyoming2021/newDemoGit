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
import com.app.video.videoapps.bean.VideoItemBean;
import com.app.video.videoapps.event.CollectVideoEvent;
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

public class VideoChildFragmentAdapter extends BaseMultiItemQuickAdapter<VideoItemBean, BaseViewHolder> {
    public static final int TYPE_VIDEO = -0xff;
    public static final int TYPE_ADVERTISING = 1;

    public VideoChildFragmentAdapter(List<VideoItemBean> data) {
        super(data);
        addItemType(TYPE_VIDEO, R.layout.item_video_new);
        addItemType(TYPE_ADVERTISING, R.layout.item_advertising);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final VideoItemBean item) {
        ImageView imageView = holder.getView(R.id.image);
        GlideUtils.loadImagView(mContext, item.getPic(), imageView);

        holder.setText(R.id.tv_name, item.getTitle());

        switch (holder.getItemViewType()) {
            case TYPE_VIDEO:
                holder.setText(R.id.tv_hot, item.getHotcount() + "%");
                holder.setText(R.id.tv_time, item.getVideotime());

                TextView tvFree = holder.getView(R.id.tv_free);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_free).mutate();
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                //???????????? 1????????? 0????????????
                if (item.getIs_free() == 1) {
                    DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_F0982E)));
                    holder.setText(R.id.tv_free, mContext.getString(R.string.free));
                } else {
                    DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_EC72AD)));
                    holder.setText(R.id.tv_free, "VIP");
                }
                tvFree.setBackground(drawable);
                //???????????? 1???????????? 0????????????
                final ImageView ivCollect = holder.getView(R.id.iv_collect);

                if (item.getIs_collect() == 1) {
                    ivCollect.setImageResource(R.drawable.collected);
                } else {
                    ivCollect.setImageResource(R.drawable.add_collect);
                }
                ivCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getIs_collect() == 1) {
                            delCollect(ivCollect, item);
                        } else {
                            addCollect(ivCollect, item);
                        }
                    }
                });
                break;
        }
    }

    private void addCollect(final ImageView iamge, final VideoItemBean item) {
        Client.getApiService().addCollect(TOKEN, item.getVid() + "", "10")
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(item.getVid());
                            collectBean.setName(item.getTitle());
                            collectBean.setPic(item.getPic());
                            collectBean.setIs_free(item.getIs_free());

                            EventBus.getDefault().post(new CollectVideoEvent("VIDEO", true, collectBean));
                            iamge.setImageResource(R.drawable.collected);
                            item.setIs_collect(1);
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }

    private void delCollect(final ImageView iamge, final VideoItemBean item) {
        Client.getApiService().delCollect(TOKEN, item.getVid() + "", "10")
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(item.getVid());

                            EventBus.getDefault().post(new CollectVideoEvent("VIDEO", false, collectBean));
                            iamge.setImageResource(R.drawable.add_collect);
                            item.setIs_collect(0);
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }
}