package com.app.video.videoapps;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.app.video.videoapps.base.BaseActivity;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.bean.DownLoadBean;
import com.app.video.videoapps.bean.LoginBean;
import com.app.video.videoapps.bean.UserBean;
import com.app.video.videoapps.fragment.MainFragment;
import com.app.video.videoapps.fragment.ShowPSWFragment;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.AppConfig;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.http.UpdateAppHttpUtil;
import com.app.video.videoapps.utils.AppUtils;
import com.app.video.videoapps.utils.DownLoadSqlUtils;
import com.app.video.videoapps.utils.SPUtils;
import com.app.video.videoapps.utils.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.yaoxiaowen.download.DownloadConstant;
import com.yaoxiaowen.download.DownloadStatus;
import com.yaoxiaowen.download.FileInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

import static com.app.video.videoapps.http.AppConfig.PSW_SWITCH;
import static com.app.video.videoapps.http.AppConfig.SP_NAME;
import static com.app.video.videoapps.http.AppConfig.SP_TOKEN;
import static com.app.video.videoapps.http.AppConfig.TOKEN;
import static com.app.video.videoapps.http.AppConfig.USERBEAN;
import static com.app.video.videoapps.http.AppConfig.sDownLoadBeans;


public class MainActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        TOKEN = new SPUtils(SP_NAME).getString(SP_TOKEN);
        if (TextUtils.isEmpty(TOKEN)) {
            Client.getApiService().randomUser("2")
                    .flatMap(new Function<BaseBean<LoginBean>, ObservableSource<BaseBean<UserBean>>>() {
                        @Override
                        public ObservableSource<BaseBean<UserBean>> apply(BaseBean<LoginBean> bean) throws Exception {
                            if (bean != null && bean.getData() != null) {
                                new SPUtils(SP_NAME).putString(SP_TOKEN, bean.getData().getToken());
                                TOKEN = bean.getData().getToken();
                                return Client.getApiService().getUserInfo(TOKEN);
                            } else {
                                ToastUtils.showShortToast(bean.getMsg());
                                return null;
                            }
                        }
                    })
                    .compose(RxsRxSchedulers.<BaseBean<UserBean>>io_main())
                    .subscribe(new ApiServiceResult<UserBean>(getComposite()) {
                        @Override
                        public void onNext(BaseBean<UserBean> bean) {
                            super.onNext(bean);
                            if (bean != null && bean.getData() != null) {
                                USERBEAN = bean.getData();
                                if (findFragment(MainFragment.class) == null) {
                                    Log.e("cjn", "?????????????????????");
                                    loadRootFragment(R.id.fl_container, MainFragment.newInstance());
                                }
                                subInviteCode();
                            }
                        }
                    });
        } else {
            if (new SPUtils(SP_NAME).getBoolean(PSW_SWITCH)) {


                loadRootFragment(R.id.fl_container, ShowPSWFragment.newInstance());
            } else if (findFragment(MainFragment.class) == null) {
                //????????????
                loadRootFragment(R.id.fl_container, MainFragment.newInstance());
            }
        }

        getVersion();

    }


    @Override
    public void onBackPressedSupport() {
        // ?????? 4???????????????Fragment????????????back??????,????????????onBackPressedSupport??????????????????
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // ????????????(?????????4.x????????????)
        return new DefaultHorizontalAnimator();
    }

    /**
     * ?????????????????? ????????? jar
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
                .transparentStatusBar()
                .navigationBarColor(R.color.transparent)
                .keyboardEnable(true); //?????????????????????????????????????????????
        mImmersionBar.init();
    }

    //QQ????????????????????????Activity?????????????????????QQ?????????????????????Activity???????????????
    //??????onActivityResult?????????fragment?????????????????????fragment????????????????????????????????????fragment?????????Activity?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    public void regist() {
        IntentFilter filter = new IntentFilter();
        for (DownLoadBean bean : sDownLoadBeans) {
            filter.addAction(bean.getUrl());
        }
        registerReceiver(receiver, filter);
    }

    public void unregist() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                for (DownLoadBean bean : sDownLoadBeans) {
                    if (intent.getAction().equals(bean.getUrl())) {
                        FileInfo firstFileInfo = (FileInfo) intent.getSerializableExtra(DownloadConstant.EXTRA_INTENT_DOWNLOAD);

                        Log.e("cjn", "????????????????????????fileinfo" + firstFileInfo.toString());
                        if (firstFileInfo.getDownloadStatus() == DownloadStatus.COMPLETE) {//???????????? ??? ???????????????????????????
                            mSqlUtils.updataBean(bean.getUrl(), DownloadStatus.COMPLETE);
                        } else if (firstFileInfo.getDownloadStatus() == DownloadStatus.PAUSE) {//???????????? ??? ???????????????????????????

                        } else {//???????????? ???5s ?????????????????????

                        }
                    }
                }
            }
        }
    };
    private DownLoadSqlUtils mSqlUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqlUtils = new DownLoadSqlUtils();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregist();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void getVersion() {
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String appVersion = AppUtils.getAppInfo(this).getVersionName();
        Map<String, String> params = new HashMap<String, String>();

        params.put("os", "1");
        params.put("curversion", appVersion);

        new UpdateAppManager
                .Builder()
                //?????????????????????Activity
                .setActivity(this)
                //?????????????????????httpManager???????????????
                .setHttpManager(new UpdateAppHttpUtil())
                //???????????????????????????
                .setUpdateUrl(AppConfig.getUrl() + "api/v1/app/getVersion")

                //???????????????????????????
                //???????????????????????????get
                .setPost(false)
                //??????????????????????????????version=1.0.0???app???versionName??????apkKey=??????????????????AndroidManifest.xml?????????
                .setParams(params)
                //?????????????????????????????????????????????????????????????????????????????????????????????
//                .hideDialogOnDownloading()
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                .setTopPic(R.mipmap.top_8)
                //????????????????????????????????????????????????????????????????????????
                //.setThemeColor(ColorUtil.getRandomColor())
                //??????apk????????????????????????????????????sd??????/Download/1.0.0/test.apk
//                .setTargetPath(path)
                //??????appKey????????????AndroidManifest.xml?????????????????????????????????????????????????????????
                //.setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                //???????????????????????????
                .dismissNotificationProgress()
                //??????????????????
                //.showIgnoreVersion()
                .setIgnoreDefParams(true)
                .build()
                //????????????????????????
                .checkNewApp(new UpdateCallback() {
                    /**
                     * ??????json,???????????????
                     *
                     * @param json ??????????????????json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data == null){
                                return updateAppBean;
                            }
                            String new_version = data.optString("new_version");
                            String update = "No";
                            try {
                                String[] newVersions = new_version.split("\\.");
                                String[] appVersions = appVersion.split("\\.");
                                int a = Integer.valueOf(newVersions[0]);
                                int b = Integer.valueOf(newVersions[1]);
                                int c = Integer.valueOf(newVersions[2]);
                                int a1 = Integer.valueOf(appVersions[0]);
                                int b1 = Integer.valueOf(appVersions[1]);
                                int c1 = Integer.valueOf(appVersions[2]);
                                if (a > a1) {
                                    update = "Yes";
                                } else if (a == a1) {
                                    if (b > b1) {
                                        update = "Yes";
                                    } else if (b == b1) {
                                        if (c > c1) {
                                            update = "Yes";
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            updateAppBean
                                    //????????????????????????Yes,No
                                    .setUpdate(update)
                                    //???????????????????????????
                                    .setNewVersion(data.optString("new_version"))
                                    //????????????????????????
                                    .setApkFileUrl(data.optString("download_url"))
                                    //????????????????????????
                                    .setUpdateLog(data.optString("update_content"))
                                    //???????????????????????????????????????????????????
//                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //????????????????????????????????????
                                    .setConstraint(data.optInt("is_force") == 1)
                                    //??????md5??????????????????
//                                    .setNewMd5(data.optString("new_md51"))
                                    ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    /**
                     * ??????????????????
                     */
                    @Override
                    public void onBefore() {
//                        CProgressDialogUtils.showProgressDialog(JavaActivity.this);
                    }

                    /**
                     * ??????????????????
                     */
                    @Override
                    public void onAfter() {
//                        CProgressDialogUtils.cancelProgressDialog(JavaActivity.this);
                    }

                    /**
                     * ???????????????
                     */
                    @Override
                    protected void noNewApp(String error) {
                        super.noNewApp(error);
                    }

                });

    }

    private void subInviteCode() {
        try{
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = cm.getPrimaryClip();
            String code = mClipData.getItemAt(0).getText().toString();
            Log.e("cjn",""+code);

            if (TextUtils.isEmpty(TOKEN)||TextUtils.isEmpty(code)||!code.contains("qgtv=")){
                return;
            }
            Log.e("cjn",""+code.split("qgtv=")[1]);
            Client.getApiService().subInviteCode(TOKEN, code.split("qgtv=")[1])
                    .compose(RxsRxSchedulers.<BaseBean>io_main())
                    .subscribe(new ApiServiceResult(getComposite()) {
                        @Override
                        public void onNext(BaseBean bean) {
                            Log.e("cjn", "??????????????????" + bean.toString());
                            if (bean != null) {

                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}