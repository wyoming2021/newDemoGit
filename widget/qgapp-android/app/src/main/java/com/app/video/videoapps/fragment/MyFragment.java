package com.app.video.videoapps.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.video.videoapps.AllHandle;
import com.app.video.videoapps.MyCaptureActivity;
import com.app.video.videoapps.R;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.base.BaseMainFragment;
import com.app.video.videoapps.bean.LoginBean;
import com.app.video.videoapps.bean.UserBean;
import com.app.video.videoapps.event.BindAccountSuccessedEvent;
import com.app.video.videoapps.event.LoginSuccessedEvent;
import com.app.video.videoapps.event.SetPSWEvent;
import com.app.video.videoapps.event.ShareSuccessedEvent;
import com.app.video.videoapps.event.UserInfoEvent;
import com.app.video.videoapps.event.UserLoginOther;
import com.app.video.videoapps.fragment.my.BindAccountFragment;
import com.app.video.videoapps.fragment.my.CodedLockFragment;
import com.app.video.videoapps.fragment.my.ComplaintLineFragment;
import com.app.video.videoapps.fragment.my.DownloadFragment;
import com.app.video.videoapps.fragment.my.HistoryWatchFragment;
import com.app.video.videoapps.fragment.my.LoginFragment;
import com.app.video.videoapps.fragment.my.QRCodeFragment;
import com.app.video.videoapps.fragment.my.SetterPSWFragment;
import com.app.video.videoapps.fragment.my.ShareFragment;
import com.app.video.videoapps.fragment.my.VIPFragment;
import com.app.video.videoapps.fragment.my.WithdrawDepositFragment;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.AppConfig;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.DialogTransformer;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.utils.AppUtils;
import com.app.video.videoapps.utils.DialogUtils;
import com.app.video.videoapps.utils.GlideUtils;
import com.app.video.videoapps.utils.IntentUtils;
import com.app.video.videoapps.utils.KeyboardUtils;
import com.app.video.videoapps.utils.MQGlideImageLoader4;
import com.app.video.videoapps.utils.SPUtils;
import com.app.video.videoapps.utils.ToastUtils;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.app.video.videoapps.http.AppConfig.PSW;
import static com.app.video.videoapps.http.AppConfig.PSW_SWITCH;
import static com.app.video.videoapps.http.AppConfig.SP_NAME;
import static com.app.video.videoapps.http.AppConfig.SP_TOKEN;
import static com.app.video.videoapps.http.AppConfig.TOKEN;
import static com.app.video.videoapps.http.AppConfig.USERBEAN;

/**
 * ??????
 */
@RuntimePermissions
public class MyFragment extends BaseMainFragment {
    private static final int REQUEST_CODE = 10086;
    @BindView(R.id.paddingView)
    View paddingView;
    @BindView(R.id.iv_oval)
    ImageView ivOval;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.ll_account)
    LinearLayout llAccount;
    @BindView(R.id.tv_bind_account)
    TextView tvBindAccount;
    @BindView(R.id.ll_bind_account)
    LinearLayout llBindAccount;
    @BindView(R.id.tv_account_number)
    TextView tvAccountNumber;
    @BindView(R.id.tv_watch_day)
    TextView tvWatchDay;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_people_num)
    TextView tvPeopleNum;
    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;
    @BindView(R.id.psw_switch)
    Switch pswSwitch;
    @BindView(R.id.ll_buy_vip)
    LinearLayout mLlBuyVip;
    @BindView(R.id.ll_share)
    LinearLayout mLlShare;
    @BindView(R.id.iv_qrcode)
    ImageView mIvQrcode;
    @BindView(R.id.iv_sacn_qrcode)
    ImageView mIvSacnQrcode;
    @BindView(R.id.tv_find_account)
    TextView mTvFindAccount;
    @BindView(R.id.tv_coded_lock)
    TextView mTvCodedLock;
    @BindView(R.id.tv_request_video)
    TextView mTvRequestVideo;
    @BindView(R.id.tv_history_watch)
    TextView mTvHistoryWatch;
    @BindView(R.id.tv_download)
    TextView mTvDownload;
    @BindView(R.id.tv_invitation_code)
    TextView mTvInvitationCode;
    @BindView(R.id.tv_customer_services)
    TextView mTvCustomerServices;
    @BindView(R.id.tv_car_crowd)
    TextView mTvCarCrowd;
    @BindView(R.id.iv_help)
    ImageView mIvHelp;
    @BindView(R.id.ll)
    LinearLayout mLl;
    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;
    @BindView(R.id.iv_oval_vip)
    ImageView mIvOvalVip;
    @BindView(R.id.iv_header_vip)
    ImageView mIvHeaderVip;
    @BindView(R.id.tv_month_des)
    TextView mTvMonthDes;
    @BindView(R.id.rl_head_vip)
    RelativeLayout mRlHeadVip;
    @BindView(R.id.tv_vip_type)
    TextView tvVipType;
    @BindView(R.id.ll_daili2)
    LinearLayout ll_daili2;


    private Dialog loadingDialog;

    private boolean isChangeUser;


    private AllHandle mHandler;

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_my;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZXingLibrary.initDisplayOpinion(_mActivity);
        EventBus.getDefault().register(this);

    }


    // ????????????
    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        Intent intent = new Intent(_mActivity, MyCaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * ???????????????????????????
         */
        Log.e("cjn", "GGGGGGG" + requestCode + "       " + REQUEST_CODE);
        if (requestCode == REQUEST_CODE) {
            Log.e("cjn", "EEEEEE");
            //??????????????????????????????????????????
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    Log.e("cjn", "BBBBBBB");
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    Log.e("cjn", "CCCCCCC");
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    qrCodeLogin(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Log.e("cjn", "DDDDDDD");
                    Toast.makeText(_mActivity, "?????????????????????", Toast.LENGTH_LONG).show();
                }
            }
        }
        Log.e("cjn", "FFFFFFF");
    }

    private void qrCodeLogin(String result) {
        dialogTransformer = new DialogTransformer(_mActivity);
        Client.getApiService().getToken(result + "&&flag=1").flatMap(new Function<BaseBean<String>, ObservableSource<BaseBean<LoginBean>>>() {
            @Override
            public ObservableSource<BaseBean<LoginBean>> apply(BaseBean<String> bean) throws Exception {
                if (!bean.getCode().equals("0")) {
                    Log.e("cjn", "bean.getMsg()" + bean.getMsg().toString());
                    ToastUtils.showShortToastSafe(bean.getMsg());
                }
                return Client.getApiService().doLogin(bean.getData());
            }
        }).flatMap(new Function<BaseBean<LoginBean>, ObservableSource<BaseBean<UserBean>>>() {
            @Override
            public ObservableSource<BaseBean<UserBean>> apply(BaseBean<LoginBean> bean) throws Exception {
                if (bean.getCode().equals("0")) {
                    if (bean != null && bean.getData() != null) {
                        new SPUtils(SP_NAME).putString(SP_TOKEN, bean.getData().getToken());
                        TOKEN = bean.getData().getToken();
                    }
                }
                return Client.getApiService().getUserInfo(TOKEN);
            }
        })
                .compose(RxsRxSchedulers.<BaseBean<UserBean>>io_main())
                .compose(dialogTransformer.transformer())
                .subscribe(new ApiServiceResult<UserBean>(getComposite()) {
                    @Override
                    public void onNext(BaseBean<UserBean> bean) {
                        super.onNext(bean);
                        if (bean != null && bean.getData() != null) {
                            Log.e("cjn", "bean.getData()" + bean.getData().toString());
                            EventBus.getDefault().post(new UserLoginOther());
                            USERBEAN = bean.getData();
                            initData();
                        }
                    }
                });
    }

    // ??????????????????????????????????????????????????????
    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(_mActivity)
                .setMessage(R.string.permission_camera_rationale)
                .show();
    }

    // ????????????
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showWriteExternalStorage() {
        MQConfig.isShowClientAvatar = true;
        HashMap<String, String> clientInfo = new HashMap<>();
        clientInfo.put("name", USERBEAN.getRandomnum());
        clientInfo.put("avatar", USERBEAN.getPic());
        Intent intent = new MQIntentBuilder(_mActivity)
                .setCustomizedId(USERBEAN.getRandomnum()) // ????????? id ??????????????????????????????
                .setClientInfo(clientInfo) // ?????????????????? PS: ??????????????????????????????,??????????????????????????????,????????????????????????
                .updateClientInfo(clientInfo) // ?????????????????? PS: ????????????????????????????????????????????????????????????????????????????????????
                .build();
        startActivity(intent);
    }

    // ??????????????????????????????????????????????????????
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForWriteExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(_mActivity)
                .setMessage(R.string.permission_writeexternalstorage_rationale)
                .show();
    }

    @Override
    protected void initView() {

    }

//    public void initdata11() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1500);
//                    mHandler.sendEmptyMessage(0);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//    }


    @Override
    protected void initData() {
        super.initData();
        tvAppVersion.setText(AppUtils.getAppVersionName(_mActivity));
        pswSwitch.setChecked(new SPUtils(SP_NAME).getBoolean(PSW_SWITCH));
        if (USERBEAN != null) {
            ImageView imageView;
            if (USERBEAN.getIs_vip() == 1) {//??????

                mRlHeadVip.setVisibility(View.VISIBLE);
                mRlHead.setVisibility(View.GONE);
                imageView = mIvHeaderVip;

                tvVipType.setText(USERBEAN.getVipotype().toString());
                if (USERBEAN.getVipotype().toString().equals("??????")) {
                    mRlHeadVip.setBackground(getResources().getDrawable(R.mipmap.vip_month));
                }
                if (USERBEAN.getVipotype().toString().equals("??????")) {
                    mRlHeadVip.setBackground(getResources().getDrawable(R.mipmap.vip_quarter));
                }
                if (USERBEAN.getVipotype().toString().equals("??????")) {
                    mRlHeadVip.setBackground(getResources().getDrawable(R.mipmap.vip_yead));
                }

//                switch (USERBEAN.getVipotype()) {
//                    case 1:
//                        tvVipType.setText(getString(R.string.vip_month));
//                        break;
//                    case 5:
//                        tvVipType.setText(getString(R.string.vip_quarter));
//                        break;
//                    case 10:
//                        tvVipType.setText(getString(R.string.vip_year));
//                        break;
//                }
            } else {
                mRlHead.setVisibility(View.VISIBLE);
                mRlHeadVip.setVisibility(View.GONE);
                imageView = ivHeader;
            }

            if (!TextUtils.isEmpty(USERBEAN.getPic())) {

                GlideUtils.loadCircleImagView(_mActivity, USERBEAN.getPic(), imageView);
            } else
                GlideUtils.loadCircleImagView(_mActivity, R.mipmap.head, imageView);

            //??????????????????
            tvWatchDay.setText("??????????????????" + USERBEAN.getLookedcount() + "/" + USERBEAN.getLookcount());
//            tvWatchDay.setText(String.format(getString(R.string.residue_num), USERBEAN.getLookedcount() + "/" + USERBEAN.getLookcount()));

            //??????
            tvMoney.setText(USERBEAN.getResidual_asset());
//            tvMoney.setText(String.format(getString(R.string.show_head_3), USERBEAN.getResidual_asset()));
            //???????????????
            tvPeopleNum.setText("?????????" + USERBEAN.getSharecount());
//            tvPeopleNum.setText(String.format(getString(R.string.show_head_4), USERBEAN.getSharecount()));

            if (TextUtils.isEmpty(USERBEAN.getMobile())) {
                tvAccountNumber.setText(getString(R.string.up_watch_number));
            } else {
                tvAccountNumber.setText(USERBEAN.getMobile());
            }
//            //6???????????????????????????
            GlideUtils.loadCircleImagView(_mActivity, USERBEAN.getPic(), ivHeader);
            GlideUtils.loadCircleImagView(_mActivity, USERBEAN.getPic(), mIvHeaderVip);
        } else {
            GlideUtils.loadCircleImagView(_mActivity, R.mipmap.head, ivHeader);
            getUserInfo();
        }

    }


    @Override
    protected View setStatusBarView() {
        return paddingView;
    }

    @Override
    protected int statusBarColor() {
        return R.color.white;
    }

    @OnClick(R.id.iv_help)
    void help() {
        showHelpDialog();
    }

    @OnClick(R.id.tv_car_crowd)
    void crowd() {
        startActivity(IntentUtils.getViewIntent(AppConfig.CAR_CROWD));

    }

    /**
     * ????????????
     */
    @OnClick(R.id.ll_daili2)
    void daili() {
        ((MainFragment) getParentFragment()).startBrotherFragment(WebFragment.newInstance(AppConfig.getUrl()+"upload/zhaomu/index.html"));

    }


    @OnClick(R.id.ll_buy_vip)
    void vip() {
        ((MainFragment) getParentFragment()).startBrotherFragment(VIPFragment.newInstance());
    }

    /**
     * ??????
     */

    @OnClick(R.id.ll_withdraw_deposit)
    void withdrawDeposit() {
        ((MainFragment) getParentFragment()).startBrotherFragment(WithdrawDepositFragment.newInstance());
    }

    @OnClick(R.id.iv_qrcode)
    void qrcode() {
        ((MainFragment) getParentFragment()).startBrotherFragment(QRCodeFragment.newInstance(USERBEAN.getRandomnum()));
    }

    @OnClick(R.id.iv_sacn_qrcode)
    void sacnQRCode() {
        MyFragmentPermissionsDispatcher.showCameraWithPermissionCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnClick(R.id.ll_account)
    void changeAccount() {
        ((MainFragment) getParentFragment()).startBrotherFragment(LoginFragment.newInstance(USERBEAN.getRandomnum()));
    }

    @OnClick(R.id.tv_find_account)
    void findAccount() {
        ((MainFragment) getParentFragment()).startBrotherFragment(BindAccountFragment.newInstance(true));
    }

    @OnClick(R.id.ll_bind_account)
    void bindAccount() {
        ((MainFragment) getParentFragment()).startBrotherFragment(BindAccountFragment.newInstance(false));
    }

    @OnClick(R.id.tv_coded_lock)
    void codedLock() {
        ((MainFragment) getParentFragment()).startBrotherFragment(CodedLockFragment.newInstance(USERBEAN.getIs_safe() == 1));
    }

//    @OnClick(R.id.ll_change_bind_account)
//    void changeBindAccount() {
//        ((MainFragment) getParentFragment()).startBrotherFragment(ChangeBindAccountFragment.newInstance());
//    }

//    @OnClick(R.id.tv_change_psw)
//    void changePsw() {
//        ((MainFragment) getParentFragment()).startBrotherFragment(ChangePswFragment.newInstance());
//    }

//    @OnClick(R.id.tv_project_management)
//    void projectManagement() {
//        ((MainFragment) getParentFragment()).startBrotherFragment(ProjectManagementFragment.newInstance());
//    }

    @OnClick(R.id.tv_request_video)
    void complaintLine() {
        ((MainFragment) getParentFragment()).startBrotherFragment(ComplaintLineFragment.newInstance());
    }

    /**
     * ????????????
     */
    @OnClick(R.id.tv_history_watch)
    void historyWatch() {
        ((MainFragment) getParentFragment()).startBrotherFragment(HistoryWatchFragment.newInstance());
    }

    /**
     * ????????????
     */
    @OnClick(R.id.tv_download)
    void download() {
        ((MainFragment) getParentFragment()).startBrotherFragment(DownloadFragment.newInstance());
    }

    /**
     * ????????????
     */
    @OnClick(R.id.ll_share)
    void share() {
        ((MainFragment) getParentFragment()).startBrotherFragment(WebFragment.newInstance("http://zzasus.com/appsetup/sharelink.php?token="+TOKEN));
//        ((MainFragment) getParentFragment()).startBrotherFragment(ShareFragment.newInstance());
    }

    @OnClick(R.id.psw_switch)
    void pswSwitch() {
        if (pswSwitch.isChecked()) {
            ((MainFragment) getParentFragment()).startBrotherFragment(SetterPSWFragment.newInstance());
        } else {
            new SPUtils(SP_NAME).putBoolean(PSW_SWITCH, false);
            new SPUtils(SP_NAME).putString(PSW, "");
        }
        pswSwitch.setChecked(false);
    }

    @OnClick(R.id.tv_invitation_code)
    void invitationCode() {
        showInvitationCodeDialog();
    }

    @OnClick(R.id.tv_customer_services)
    void customerServices() {
        MQImage.setImageLoader(new MQGlideImageLoader4());
        MyFragmentPermissionsDispatcher.showWriteExternalStorageWithPermissionCheck(this);
    }

    /**
     * ?????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPSWEvent(SetPSWEvent event) {
        pswSwitch.setChecked(true);
    }

    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSuccessedEvent(LoginSuccessedEvent event) {
        getUserInfo();
        isChangeUser = true;
    }

    /**
     * ??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindAccountSuccessedEvent(BindAccountSuccessedEvent event) {
        getUserInfo();
    }

    /**
     * ????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userInfoEvent(UserInfoEvent event) {
        initData();
        if (isChangeUser) {
            isChangeUser = false;
            EventBus.getDefault().post(new UserLoginOther());
        }
    }

    /**
     * ???????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void shareSuccessed(ShareSuccessedEvent event) {
        getUserInfo();
    }

    private void showInvitationCodeDialog() {
        dialogTransformer = new DialogTransformer(_mActivity);
        View view = View.inflate(_mActivity, R.layout.dialog_input_code, null);
        final EditText etCode = view.findViewById(R.id.et_code);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.dismiss();
                loadingDialog = null;
                KeyboardUtils.hideSoftInput(_mActivity);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.dismiss();
                loadingDialog = null;
                KeyboardUtils.hideSoftInput(_mActivity);
            }
        });
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.showShortToast(getString(R.string.input_invitation_code3));
                } else {
                    subInviteCode(code);
                }
            }
        });
        loadingDialog = new DialogUtils().showDialog(_mActivity, view, false);
    }

    private void subInviteCode(String code) {
        Client.getApiService().subInviteCode(TOKEN, code)
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .compose(dialogTransformer.transformer())
                .subscribe(new ApiServiceResult(getComposite()) {
                    @Override
                    public void onNext(BaseBean bean) {
                        Log.e("cjn", "??????????????????" + bean.toString());
                        if (bean != null) {
                            ToastUtils.showShortToast(bean.getMsg());

                            loadingDialog.dismiss();
                            loadingDialog = null;
                            KeyboardUtils.hideSoftInput(_mActivity);
                        }
                    }
                });
    }

    private void showHelpDialog() {
        LayoutInflater inflater = LayoutInflater.from(_mActivity);
        View v = inflater.inflate(R.layout.dialog_help, null);// ????????????view
        LinearLayout layout = v.findViewById(R.id.dialog_view);// ????????????

        loadingDialog = new Dialog(_mActivity, R.style.loading_dialog);// ?????????????????????dialog

        loadingDialog.setCancelable(true);// ?????????????????????????????????
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// ????????????
        loadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }


}
