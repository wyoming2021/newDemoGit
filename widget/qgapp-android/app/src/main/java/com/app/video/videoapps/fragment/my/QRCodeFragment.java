package com.app.video.videoapps.fragment.my;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.video.videoapps.R;
import com.app.video.videoapps.base.BaseBackFragment;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.bean.UserQrcodeBean;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.utils.GlideUtils;
import com.app.video.videoapps.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.video.videoapps.http.AppConfig.TOKEN;

public class QRCodeFragment extends BaseBackFragment {
    @BindView(R.id.paddingView)
    View paddingView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.tv_current_account)
    TextView mTvCurrentAccount;

    private String account;


    public static QRCodeFragment newInstance(String account) {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_qrcode;
    }

    @Override
    protected View setStatusBarView() {
        return paddingView;
    }

    @Override
    protected int statusBarColor() {
        return R.color.white;
    }

    @Override
    protected void initView() {
        initToolbarNav(toolbar);

        toolbar.setTitle("");
        title.setText(getString(R.string.change_account));
        account = getArguments().getString("account");
        mTvCurrentAccount.setText(String.format(getString(R.string.current_account), account));
    }

    @Override
    protected void initData() {
        getData();
    }

    private void getData() {
        Client.getApiService().getUserQrcode(TOKEN)
                .compose(RxsRxSchedulers.<BaseBean<UserQrcodeBean>>io_main())
                .subscribe(new ApiServiceResult<UserQrcodeBean>(getComposite()) {
                    @Override
                    public void onNext(BaseBean<UserQrcodeBean> bean) {
                        if (bean != null && bean.getData() != null) {
                            setData(bean);
                        }
                    }
                });
    }

    private void setData(BaseBean<UserQrcodeBean> bean) {
        GlideUtils.loadImagView(_mActivity, bean.getData().getQrcode(), ivQrcode);
    }

    @OnClick(R.id.tv_copy)
    void copy() {
        // ???API11??????android????????????android.content.ClipboardManager
        // ????????????????????????????????????????????????android.text.ClipboardManager???????????????deprecated????????????????????????
        ClipboardManager cm = (ClipboardManager) _mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        ClipData mClipData = ClipData.newPlainText("Label", account);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);

        ToastUtils.showShortToast("????????????");
    }

}
