package com.app.video.videoapps.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.video.videoapps.MainActivity;
import com.app.video.videoapps.MyApplication;
import com.app.video.videoapps.R;
import com.app.video.videoapps.adapter.video.PlayStarAdapter;
import com.app.video.videoapps.adapter.video.RandomVideoAdapter;
import com.app.video.videoapps.base.BaseBackFragment;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.bean.CollectBean;
import com.app.video.videoapps.bean.DownLoadBean;
import com.app.video.videoapps.bean.DownVideoEvent;
import com.app.video.videoapps.bean.Secondotype;
import com.app.video.videoapps.bean.SetVideoEvent;
import com.app.video.videoapps.bean.StarItemBean;
import com.app.video.videoapps.bean.VideoBean;
import com.app.video.videoapps.bean.VideoItemBean;
import com.app.video.videoapps.bean.VideoUrlBean;
import com.app.video.videoapps.event.CollectMVEvent;
import com.app.video.videoapps.event.CollectVideoEvent;
import com.app.video.videoapps.event.ConsumeTimeEvent;
import com.app.video.videoapps.event.VideoProgressEvent;
import com.app.video.videoapps.fragment.classify.ClassifyDetailFragment;
import com.app.video.videoapps.fragment.collect.StarDetailFragment;
import com.app.video.videoapps.fragment.my.VIPFragment;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.AppConfig;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.DialogTransformer;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.utils.DialogUtils;
import com.app.video.videoapps.utils.DownLoadSqlUtils;
import com.app.video.videoapps.utils.GlideUtils;
import com.app.video.videoapps.utils.HeadSpaceItemDecoration;
import com.app.video.videoapps.utils.IntentUtils;
import com.app.video.videoapps.utils.KeyboardUtils;
import com.app.video.videoapps.utils.SPUtils;
import com.app.video.videoapps.utils.SizeUtils;
import com.app.video.videoapps.utils.SpaceItemDecoration;
import com.app.video.videoapps.utils.ToastUtils;
import com.app.video.videoapps.view.FlowLayout1;
import com.app.video.videoapps.view.LabelsView;
import com.app.video.videoapps.view.MyJZVideoPlayerStandard;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.nex3z.flowlayout.FlowLayout;
import com.umeng.commonsdk.internal.crash.UMCrashManager;
import com.yaoxiaowen.download.DownloadHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_PLAYING;
import static com.app.video.videoapps.http.AppConfig.COMPLETE;
import static com.app.video.videoapps.http.AppConfig.SP_NAME;
import static com.app.video.videoapps.http.AppConfig.SP_SB1NONBER;
import static com.app.video.videoapps.http.AppConfig.SP_SB2NONBER;
import static com.app.video.videoapps.http.AppConfig.SP_SB3NONBER;
import static com.app.video.videoapps.http.AppConfig.TOKEN;
import static com.app.video.videoapps.http.AppConfig.WAIT;
import static com.app.video.videoapps.http.AppConfig.sDownLoadBeans;

/**
 * ????????????
 */
public class VideoPlayFragment extends BaseBackFragment {

    @BindView(R.id.paddingView)
    View paddingView;
    @BindView(R.id.jzVideoPlayer)
    MyJZVideoPlayerStandard jzVideoPlayer;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_line)
    TextView tvLine;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.tv_definition)
    TextView tvDefinition;
    @BindView(R.id.tv_problem)
    TextView tvProblem;
    @BindView(R.id.tv_zan_des)
    TextView tvZanDes;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.flow)
    FlowLayout flow;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_alert)
    View rlAlert;
    @BindView(R.id.ll_alert_2)
    View llAlert2;
    @BindView(R.id.ll_alert_1)
    View llAlert1;

    @BindView(R.id.iv_zan)
    ImageView ivZan;
    @BindView(R.id.iv_cai)
    ImageView ivCai;
    @BindView(R.id.labelsView)
    LabelsView labelsView;

    @BindView(R.id.video_bqian)
    RelativeLayout video_bqian;
//    @BindView(R.id.img_touming)
//    ImageView img_touming;

    ImageView iv_banner;

    private PopupWindow window;
    private boolean flowLineShowing, flowPShowing;
    private TextView selectedTextView;

    private RandomVideoAdapter adapter;
    private PlayStarAdapter headAdapter;
    private List<VideoItemBean> data;
    private List<StarItemBean> headData;

    private String vid;
    private VideoBean videoBean;

    private TextView tvStarNum;


    private Dialog loadingDialog;
    private Dialog setDialog;
    private int SB1NONBER;
    private int SB2NONBER;
    private int SB3NONBER;

    private DownLoadSqlUtils mSqlUtils;

    public static VideoPlayFragment newInstance(String vid) {
        VideoPlayFragment fragment = new VideoPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("vid", vid);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.biao_chao = 0;
//        Bitmap  bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.mipmap.qidong);
//
//        Bitmap finalBitmap = Fuzzy_Background.with(getContext())
//                .bitmap(bitmap) //??????????????????
//                .radius(20)//????????????
//                .blur();
//        img_touming.setImageBitmap(finalBitmap);


        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            vid = bundle.getString("vid");
        }
        //??????????????????
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSqlUtils = new DownLoadSqlUtils();

    }

    @Override
    protected boolean statusBarDarkFont() {
        return false;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_video_play;
    }

    @Override
    protected View setStatusBarView() {
        paddingView.setBackgroundColor(getResources().getColor(R.color.black));
        return paddingView;
    }

    @Override
    protected int statusBarColor() {
        return R.color.transparent;
    }


    @Override
    protected void initView() {
        super.initView();

        //????????????
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;  //??????
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;  //??????

        setRefreshLayout();

        data = new ArrayList<>();
        headData = new ArrayList<>();

        recycler.setLayoutManager(new GridLayoutManager(_mActivity, 2));
        recycler.addItemDecoration(new HeadSpaceItemDecoration(15, 0));
        adapter = new RandomVideoAdapter(data);
        recycler.setAdapter(adapter);

        View headView = View.inflate(_mActivity, R.layout.head_video_play, null);
        RecyclerView hedaRecycler = headView.findViewById(R.id.heda_recycler);
        tvStarNum = headView.findViewById(R.id.tv_star_num);
        iv_banner = headView.findViewById(R.id.iv_banner);

        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hedaRecycler.addItemDecoration(new SpaceItemDecoration(15, 0));
        hedaRecycler.setLayoutManager(manager);


        headAdapter = new PlayStarAdapter(headData);
        hedaRecycler.setAdapter(headAdapter);
        headAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start(StarDetailFragment.newInstance(headData.get(position).getSid() + ""));
            }
        });

        adapter.addHeaderView(headView);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start(VideoPlayFragment.newInstance(data.get(position).getVid() + ""));
            }
        });

        SB1NONBER = new SPUtils(SP_NAME).getInt(SP_SB1NONBER, 10);
        SB2NONBER = new SPUtils(SP_NAME).getInt(SP_SB2NONBER, 10);
        SB3NONBER = new SPUtils(SP_NAME).getInt(SP_SB3NONBER, 10);
    }

    private void setRefreshLayout() {
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(false);
        refreshLayout.setHeaderHeight(55);
    }

    @Override
    protected void initData() {
        getData();

    }

    private void getData() {
        Client.getApiService().getVideoDetails(TOKEN, vid).flatMap(new Function<BaseBean<VideoBean>, ObservableSource<BaseBean<List<VideoItemBean>>>>() {
            @Override
            public ObservableSource<BaseBean<List<VideoItemBean>>> apply(BaseBean<VideoBean> bean) throws Exception {
                if (bean != null && bean.getData() != null) {
                    videoBean = bean.getData();
                }
                return Client.getApiService().getRandomVideoList(TOKEN);
            }
        })
                .compose(RxsRxSchedulers.<BaseBean<List<VideoItemBean>>>io_main())
                .compose(new DialogTransformer(_mActivity).transformer())
                .subscribe(new ApiServiceResult<List<VideoItemBean>>(getComposite()) {
                    @Override
                    public void onNext(BaseBean<List<VideoItemBean>> bean) {
                        super.onNext(bean);
                        if (videoBean != null)
                            setData(videoBean);
                        if (bean != null && bean.getData() != null && !bean.getData().isEmpty()) {
                            data.addAll(bean.getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    /**
     * ?????????????????????
     */
    private String[] mediaName = {"??????", "??????"};

    private void initPlayerUrl(String url) {
        Object[] objects = new Object[2];
        LinkedHashMap map = new LinkedHashMap();
        for (VideoUrlBean videoUrlBean : videoBean.getM3u8()){
            map.put(videoUrlBean.getRes(), videoUrlBean.getUrl());

        }
        objects[0] = map;
        objects[1] = false;
        jzVideoPlayer.setUp(objects, videoBean.getM3u8_default(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "");
        jzVideoPlayer.startVideo();

    }


    private void setData(VideoBean data) {
        jzVideoPlayer.setData(videoBean);

        JZVideoPlayerStandard.WIFI_TIP_DIALOG_SHOWED = true;//??????????????????dialog
        initPlayerUrl(data.getUrl());
//        jzVideoPlayer.setUp(data.getUrl(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "");
        Glide.with(_mActivity).load(data.getPic()).into(jzVideoPlayer.thumbImageView);
        Log.e("cjn",""+data.getBanner_url());
        GlideUtils.loadImagView(_mActivity, data.getBanner_pic(), iv_banner);

        iv_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent action = new Intent(Intent.ACTION_VIEW);
                action.setData(Uri.parse(videoBean.getBanner_url()));
                startActivity(action);
            }
        });

        tvZanDes.setText(videoBean.getHotcount() + "%?????????????????????????????????");
        //?????? 0???????????? 1????????? 2?????????
        if (data.getIs_flag() == 1) {
            ivZan.setImageResource(R.mipmap.zan_on);
        } else if (data.getIs_flag() == 2) {
            ivCai.setImageResource(R.mipmap.downzan_on);
        }

        tvTitle.setText(data.getTitle());
        if (data.getIs_free() == 1) {
            tvType.setText("????????????");
        } else {
            tvType.setText("???VIP???");
        }
//
//        //???????????? 1????????? 0?????????  ?????????vip 1?????? 0 ??????
//        if (data.getIs_free() == 1 || data.getIs_vip() == 1) {
//            rlAlert.setVisibility(View.GONE);
//        }

        //???????????? 1????????? 0?????????  ?????????vip 1?????? 0 ??????
        if (data.getIs_free() == 1 || data.getIs_vip() == 1) {
            rlAlert.setVisibility(View.GONE);
        } else {
            rlAlert.setVisibility(View.VISIBLE);
            llAlert1.setVisibility(View.VISIBLE);
        }

        //???????????? 1???????????? 0????????????
        if (data.getIs_collect() == 1) {
            setTextCollected();
        } else {
            setTextNoCollected();
        }

        if (data.getStarlist() != null && !data.getStarlist().isEmpty()) {
            headData.addAll(data.getStarlist());
            headAdapter.notifyDataSetChanged();
            tvStarNum.setText(String.format(getString(R.string.star_num), data.getStarlist().size()));
        }

    }

    private void setTextNoCollected() {
        Drawable drawable = getResources().getDrawable(R.drawable.shape_5).mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(_mActivity.getResources().getColor(R.color.c_ebebeb)));
        tvCollect.setBackground(drawable);
        tvCollect.setTextColor(getResources().getColor(R.color.c_646464));
        tvCollect.setText(getString(R.string.collect_video));
    }

    private void setTextCollected() {
        Drawable drawable = getResources().getDrawable(R.drawable.shape_ff6c00_5).mutate();
        tvCollect.setBackground(drawable);
        tvCollect.setTextColor(getResources().getColor(R.color.white));
        tvCollect.setText(getString(R.string.collected_video));
    }


    @OnClick(R.id.rl_alert)
    void alert() {
        rlAlert.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_buy_vip)
    void buyVip() {
        start(VIPFragment.newInstance());
    }

    /**
     * ??????
     */
    @OnClick(R.id.tv_collect)
    void collect() {
        //???????????? 1???????????? 0????????????
        if (videoBean.getIs_collect() == 1) {
            delCollect();
        } else {
            addCollect();
        }
    }

    private void addCollect() {
        Client.getApiService().addCollect(TOKEN, videoBean.getVid() + "", videoBean.getOtype())
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            videoBean.setIs_collect(1);
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(videoBean.getVid());
                            collectBean.setName(videoBean.getTitle());
                            collectBean.setPic(videoBean.getPic());
                            collectBean.setIs_free(videoBean.getIs_free());

                            if (videoBean.getOtype().equals("10"))
                                EventBus.getDefault().post(new CollectVideoEvent("VIDEOPLAY", true, collectBean));
                            else
                                EventBus.getDefault().post(new CollectMVEvent("MVPLAY", true, collectBean));
                            setTextCollected();
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }

    private void delCollect() {
        Client.getApiService().delCollect(TOKEN, videoBean.getVid() + "", videoBean.getOtype())
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new ApiServiceResult() {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null && bean.getCode().equals("0")) {
                            videoBean.setIs_collect(0);
                            CollectBean collectBean = new CollectBean();
                            collectBean.setOid(videoBean.getVid());

                            if (videoBean.getOtype().equals("10"))
                                EventBus.getDefault().post(new CollectVideoEvent("VIDEOPLAY", false, collectBean));
                            else
                                EventBus.getDefault().post(new CollectMVEvent("MVPLAY", false, collectBean));
                            setTextNoCollected();
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }


    /**
     * ?????? 1????????? 2:??????
     */
    @OnClick(R.id.iv_zan)
    void zan() {
        //?????? 0???????????? 1????????? 3???????????????
        doFlag(videoBean.getIs_flag() == 1 ? 3 : 1);
    }

    /**
     * ?????? 2:?????? 4:????????????
     */
    @OnClick(R.id.iv_cai)
    void cai() {
        doFlag(videoBean.getIs_flag() == 2 ? 4 : 2);
    }

    private void doFlag(final int flag) {
        Client.getApiService().doFlag(TOKEN, videoBean.getVid(), flag)
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .subscribe(new ApiServiceResult(getComposite()) {
                    @Override
                    public void onNext(BaseBean bean) {
                        if (bean != null) {
                            videoBean.setIs_flag(flag);
                            ToastUtils.showShortToast(bean.getMsg());
                            if (TextUtils.equals("0", bean.getCode()))
                                if (flag == 1) {
                                    ivZan.setImageResource(R.mipmap.zan_on);
                                } else if (flag == 3) {
                                    ivZan.setImageResource(R.mipmap.zan);
                                }else if (flag == 2) {
                                    ivCai.setImageResource(R.mipmap.downzan_on);
                                }else{
                                    ivCai.setImageResource(R.mipmap.downzan);
                                }
                        }
                    }
                });
    }

    /**
     * ????????????
     */
    @OnClick(R.id.tv_problem)
    void wenti() {
        wentifankui();
    }

    /**
     * ????????????
     */
    private void wentifankui() {
        dialogTransformer = new DialogTransformer(_mActivity);
        View view = View.inflate(_mActivity, R.layout.dialog_input_back, null);
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
                    ToastUtils.showShortToast(getString(R.string.input_back));
                } else {

                    Client.getApiService().subVideoTrouble(TOKEN, etCode.getText().toString().trim(), "" + videoBean.getVid())
                            .compose(RxsRxSchedulers.<BaseBean>io_main())
                            .compose(new DialogTransformer(_mActivity).transformer())
                            .subscribe(new ApiServiceResult(getComposite()) {
                                @Override
                                public void onNext(BaseBean bean) {
                                    super.onNext(bean);
                                    ToastUtils.showShortToast(bean.getMsg());
                                    _mActivity.onBackPressed();
                                }
                            });
//                    ToastUtils.showShortToast(code);
//                    subInviteCode(code);
                }
            }
        });

        final TextView text_1 = view.findViewById(R.id.text_1);
        final TextView text_2 = view.findViewById(R.id.text_2);
        final TextView text_3 = view.findViewById(R.id.text_3);
        final TextView text_4 = view.findViewById(R.id.text_4);
        text_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCode.setText(text_1.getText().toString().trim());
            }
        });
        text_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCode.setText(text_2.getText().toString().trim());
            }
        });
        text_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCode.setText(text_3.getText().toString().trim());
            }
        });
        text_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCode.setText(text_4.getText().toString().trim());
            }
        });


        loadingDialog = new DialogUtils().showDialog(_mActivity, view, false);
    }

    /**
     * ??????
     */
    @OnClick(R.id.tv_label)
    void synopsis() {
        biaoqian();
    }

    /**
     * ??????
     */
    @OnClick(R.id.tv_share)
    void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain"); //????????????????????????
        shareIntent.putExtra(Intent.EXTRA_TEXT, ""+videoBean.getShare_text());//?????????????????????
        startActivity(shareIntent);    //?????????????????????
//        ToastUtils.showShortToast("??????");
    }

    @OnClick(R.id.iv_back)
    void back() {
        _mActivity.onBackPressed();
    }

    /**
     * ??????
     */
    @OnClick(R.id.tv_line)
    void line() {

        flowPShowing = false;
        flow.removeAllViews();

        if (!flowLineShowing) {
            visibilityFlow(R.drawable.ic_expand_less_black_24dp, View.VISIBLE);
            if (flow.getChildCount() == 0) {
                TextView textView = buildLabel(tvLine, tvLine.getText().toString(), "????????????");
                flow.addView(textView);

                TextView textView2 = buildLabel(tvLine, tvLine.getText().toString(), "????????????");
                flow.addView(textView2);

//                for (int i = 0; i < 4; i++) {
//                    TextView textView = buildLabel(tvLine, tvLine.getText().toString(), "????????????" + (i + 1));
//                    flow.addView(textView);
//                }
            }
        } else {
            visibilityFlow(R.drawable.ic_expand_more_black_24dp, View.GONE);
        }
        flowLineShowing = !flowLineShowing;
    }

    /**
     * 1080p
     * ????????????
     */
    @OnClick(R.id.tv_definition)
    void p() {
        flowLineShowing = false;
        flow.removeAllViews();

        Drawable rightDrawable = getResources().getDrawable(R.drawable.ic_expand_more_black_24dp);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tvLine.setCompoundDrawables(null, null, rightDrawable, null);


        if (!flowPShowing) {
            flow.setVisibility(View.VISIBLE);
            if (flow.getChildCount() == 0) {
                TextView textView = buildLabel(tvDefinition, tvDefinition.getText().toString(), getString(R.string.standard_definition));
                flow.addView(textView);
                TextView textView1 = buildLabel(tvDefinition, tvDefinition.getText().toString(), getString(R.string.high_definition));
                flow.addView(textView1);
            }
        } else {
            flow.setVisibility(View.GONE);
        }
        flowPShowing = !flowPShowing;
    }


    private void visibilityFlow(int id, int visibility) {
        Drawable rightDrawable = getResources().getDrawable(id);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tvLine.setCompoundDrawables(null, null, rightDrawable, null);
        flow.setVisibility(visibility);
    }

    private TextView buildLabel(final TextView show, String select, final String str) {
        final TextView textView = new TextView(_mActivity);
        textView.setText(str);
        textView.setTextColor(_mActivity.getResources().getColor(R.color.c_646464));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setPadding(SizeUtils.dp2px(5), 0, SizeUtils.dp2px(5), 0);
        textView.setBackgroundResource(R.drawable.selecter_line_box);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(35));
        textView.setLayoutParams(lp);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTextView != null) {
                    if (selectedTextView == textView) {
                        Log.e("cjn", "???????????????????????????");
                        visibilityFlow(R.drawable.ic_expand_more_black_24dp, View.GONE);
                        flowLineShowing = false;
                        flowPShowing = false;
                        return;
                    }
                    selectedTextView.setSelected(false);
                }

                selectedTextView = textView;

                textView.setSelected(true);
                show.setText(textView.getText().toString());
                if (textView.getText().toString().equals("??????")) {
                    jzVideoPlayer.setBiao_chao(1);
                } else if (textView.getText().toString().equals("??????")) {
                    jzVideoPlayer.setBiao_chao(0);
                }

                Log.e("cjn", "???????????????????????????" + textView.getText().toString());
                visibilityFlow(R.drawable.ic_expand_more_black_24dp, View.GONE);
                flowLineShowing = false;
                flowPShowing = false;

                if (TextUtils.equals(getString(R.string.high_definition), str) || TextUtils.equals(getString(R.string.standard_definition), str)) {
                    if (jzVideoPlayer != null && jzVideoPlayer.currentState == CURRENT_STATE_PLAYING) {
                        JZMediaManager.pause();
                        jzVideoPlayer.onStatePause();
                        showLoadingDialog();
                    }
                }
            }
        });

        if (TextUtils.equals(select, str)) {
            textView.setSelected(true);
            selectedTextView = textView;
        }

        return textView;
    }

    private void showLoadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(_mActivity);
        View v = inflater.inflate(R.layout.loading, null);// ????????????view
        LinearLayout layout = v.findViewById(R.id.dialog_view);// ????????????
//        // main.xml??????ImageView
//        ImageView spaceshipImage = v.findViewById(R.id.img);
//        // ????????????
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.loading_animation);
//        // ??????ImageView????????????
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        loadingDialog = new Dialog(_mActivity, R.style.loading_dialog);// ?????????????????????dialog

        loadingDialog.setCancelable(false);// ?????????????????????????????????
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// ????????????
        loadingDialog.show();

        Observable.timer((new Random().nextInt(2) + 2) * 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.cancel();
                        }
                        JZMediaManager.start();
                        jzVideoPlayer.onStatePlaying();

                    }
                });
    }

    public int dip2px(float dip) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    /**
     * ??????
     */
    boolean videbiaoqian = false;

    private void biaoqian() {
        if (!videbiaoqian) {
//            String str = videoBean.getScreenotype();
//            String[] strs = str.split(",");
//            ArrayList<String> strArray = new ArrayList<String>();
//            strArray.add(videoBean.getSecondotype());
//            for (int i = 0, len = videoBean.getSecondotype().size(); i < len; i++) {
//                System.out.println(strs[i].toString());
//                strArray.add(strs[i].toString());
//            }
            labelsView.setLabels(videoBean.getSecondotype(), new LabelsView.LabelTextProvider<Secondotype>() {
                @Override
                public CharSequence getLabelText(TextView label, int position, Secondotype data) {
                    return data.getOtypename();
                }
            });
            labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
                @Override
                public void onLabelClick(TextView label, Object data, int position) {
                    //??????
//                    start(StarDetailFragment.newInstance(headData.get(position).getSid() + ""));
//                    start(StarDetailFragment.newInstance(headData.get(position).getSid() + ""));

                    start(ClassifyDetailFragment.newInstance(videoBean.getSecondotype().get(position).getOtypename() + "", videoBean.getSecondotype().get(position).getOid() + ""));
                }
            });
            video_bqian.setVisibility(View.VISIBLE);
            videbiaoqian = true;
        } else {
            video_bqian.setVisibility(View.GONE);
            videbiaoqian = false;
        }


    }


    private void showPopwindow() {

        // ??????layoutInflater??????View
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(_mActivity).inflate(R.layout.pop_synopsis, null);
        FlowLayout1 flowLayout;
        TextView tvContent = layout.findViewById(R.id.tv_content);
        flowLayout = layout.findViewById(R.id.fl);

        int padding = dip2px(5);
        flowLayout.setPadding(padding, padding, padding, padding);// ???????????????

//        String str = videoBean.getScreenotype();
//        String[] strs = str.split(",");
        ArrayList<String> strArray = new ArrayList<String>();
//        strArray.add(videoBean.getSecondotype());
//        for (int i = 0, len = strs.length; i < len; i++) {
//            System.out.println(strs[i].toString());
//            strArray.add(strs[i].toString());
//        }

        for (int j = 0; j < strArray.size(); j++) {
            final String tag = strArray.get(j);
            TextView tv = new TextView(getActivity());
            tv.setText(tag);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setPadding(padding, padding, padding, padding);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.parseColor("#FFEE79AD"));
            tv.setBackground(getResources().getDrawable(R.drawable.yuan));
            flowLayout.addView(tv);
        }


//        tvContent.setText(videoBean.getSecondotype() + "\n" + videoBean.getScreenotype());

        layout.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (window != null && window.isShowing())
                    window.dismiss();
            }
        });
        WindowManager manager = _mActivity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        window = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT, height - width * 9 / 16);

        // ??????popupWindow?????????????????????
        window.setBackgroundDrawable(new BitmapDrawable(null, ""));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getWindow().setAttributes(lp);

        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.showAsDropDown(jzVideoPlayer, 0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoProgressEvent(VideoProgressEvent event) {
        rlAlert.setVisibility(View.VISIBLE);
        llAlert1.setVisibility(View.GONE);
        llAlert2.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void consumeTimeEvent(ConsumeTimeEvent event) {
        consumeTime("1", event.getFlag());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setVideoEvent(SetVideoEvent event) {
        View view = View.inflate(_mActivity, R.layout.dialog_invitation_code, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog.dismiss();
                setDialog = null;
            }
        });

        SeekBar sb1 = view.findViewById(R.id.sb_1);
        SeekBar sb2 = view.findViewById(R.id.sb_2);
        SeekBar sb3 = view.findViewById(R.id.sb_3);
        sb1.setProgress(SB1NONBER);
        sb2.setProgress(SB2NONBER);
        sb3.setProgress(SB3NONBER);
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SB1NONBER = i;
                new SPUtils(SP_NAME).putInt(SP_SB1NONBER, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SB2NONBER = i;
                new SPUtils(SP_NAME).putInt(SP_SB2NONBER, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SB3NONBER = i;
                new SPUtils(SP_NAME).putInt(SP_SB3NONBER, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        setDialog = new DialogUtils().showDialog(_mActivity, view, false);
    }

    /**
     * ????????????
     *
     * @param event
     */
    //??????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downVideoEvent(DownVideoEvent event) {
        Log.e("cjn", "????????????????????????");
//        downLoad();
        Client.getApiService().doDown(TOKEN, videoBean.getVid(), "1")
                .compose(RxsRxSchedulers.<BaseBean>io_main())
                .subscribe(new ApiServiceResult(getComposite()) {
                    @Override
                    public void onNext(BaseBean bean) {
                        Log.e("cjn", "doDown????????????" + bean.toString());
                        if (bean.getCode().equals("0")) {
                            downLoad();
                        } else {
                            ToastUtils.showShortToast(bean.getMsg());
                        }
                    }
                });
    }

    private void downLoad() {
        String url = videoBean.getUrl();
        if (!url.contains("http")){
            url = AppConfig.getUrl() + url;
        }
        Log.e("cjn", "downLoadAAAA" + url);
        Log.e("cjn", "downLoadBBBB" + videoBean.getTitle());
        List<DownLoadBean> beans = mSqlUtils.getBean(url);
        if (beans.size() > 0) {//?????????
            if (beans.get(0).getStatus() != COMPLETE) {//?????????
                ToastUtils.showShortToast("?????????????????????");
            } else if (beans.contains(url)) {
                ToastUtils.showShortToast("???????????????");
            }
        } else {

            File file = new File(getDir(), videoBean.getTitle().toString() + ".mp4");
            Log.e("cjn", "downLoadCCCC" + getDir());
            DownLoadBean bean = new DownLoadBean(null, url, videoBean.getTitle(), file.getPath(), videoBean.getPic(), WAIT);
            sDownLoadBeans.add(bean);
            mSqlUtils.insertBean(bean);
            DownloadHelper mDownloadHelper = DownloadHelper.getInstance();
            mDownloadHelper.addTask(url, file, url)
                    .submit(_mActivity);
            ToastUtils.showShortToast("????????????");

            ((MainActivity) _mActivity).regist();
        }
    }


    public static File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// ??????sd???????????????
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// ???????????????
        }
        return sdDir;
    }

    private File dir;

    private File getDir() {
        if (dir != null && dir.exists()) {
            return dir;
        }

        dir = new File(_mActivity.getCacheDir(), "download");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * ??????????????????
     */
    private void consumeTime(String looktime, String flag) {
        if (videoBean != null){
            Client.getApiService().doLook(TOKEN, looktime, videoBean.getVid(), flag)
                    .compose(RxsRxSchedulers.<BaseBean>io_main())
                    .subscribe();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        consumeTime(MyJZVideoPlayerStandard.position + "", "1");
        JZVideoPlayer.releaseAllVideos();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }
        if (setDialog != null && setDialog.isShowing()) {
            setDialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            JZVideoPlayer.goOnPlayOnResume();
        }catch (IllegalStateException e){
            Throwable throwable = new Throwable("VideoPlayFragment onResume error");
            UMCrashManager.reportCrash(_mActivity,throwable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            JZVideoPlayer.goOnPlayOnPause();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }
        if (setDialog != null && setDialog.isShowing()) {
            setDialog.cancel();
        }
    }
}
