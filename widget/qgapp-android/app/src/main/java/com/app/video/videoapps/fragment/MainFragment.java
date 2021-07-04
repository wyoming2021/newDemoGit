package com.app.video.videoapps.fragment;

import android.text.TextUtils;

import com.app.video.videoapps.MyApplication;
import com.app.video.videoapps.R;
import com.app.video.videoapps.base.BaseMainFragment;
import com.app.video.videoapps.fragment.my.DownloadFragment;
import com.app.video.videoapps.utils.SPUtils;
import com.app.video.videoapps.view.BottomBar;
import com.app.video.videoapps.view.BottomBarTab;

import butterknife.BindView;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;

import static com.app.video.videoapps.http.AppConfig.SP_NAME;
import static com.app.video.videoapps.http.AppConfig.SP_TOKEN;
import static com.app.video.videoapps.http.AppConfig.TOKEN;
import static com.app.video.videoapps.http.AppConfig.USERBEAN;

/**
 * 首页
 */
public class MainFragment extends BaseMainFragment {
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    public static final int FIFTH = 4;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private SupportFragment[] mFragments = new SupportFragment[5];

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {
        getUserInfo();

        ((ISupportActivity) _mActivity).setFragmentAnimator(new DefaultNoAnimator());

        SupportFragment firstFragment = findFragment(MVFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = MVFragment.newInstance();
            mFragments[SECOND] = VideoFragment.newInstance();
            mFragments[THIRD] = ClassifyFragment.newInstance();
            mFragments[FOURTH] = CollectFragment.newInstance();
            mFragments[FIFTH] = MyFragment.newInstance();
            if (MyApplication.lx == true) {
                startBrotherFragment(DownloadFragment.newInstance());
                loadMultipleRootFragment(R.id.fl_main_container, FIFTH,
                        mFragments[FIRST],
                        mFragments[SECOND],
                        mFragments[THIRD],
                        mFragments[FOURTH],
                        mFragments[FIFTH]);


            } else {

                loadMultipleRootFragment(R.id.fl_main_container, FIRST,
                        mFragments[FIRST],
                        mFragments[SECOND],
                        mFragments[THIRD],
                        mFragments[FOURTH],
                        mFragments[FIFTH]);
            }

        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(VideoFragment.class);
            mFragments[THIRD] = findFragment(ClassifyFragment.class);
            mFragments[FOURTH] = findFragment(CollectFragment.class);
            mFragments[FIFTH] = findFragment(MyFragment.class);
        }
        bottomBar.addItem(new BottomBarTab(_mActivity, R.mipmap.bottom_mv, R.mipmap.bottom_mv_selected, getResources().getString(R.string.bottom_mv)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.bottom_video, R.mipmap.bottom_video_selected, getResources().getString(R.string.video)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.bottom_classify, R.mipmap.bottom_classify_selected, getResources().getString(R.string.classify)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.bottom_collect, R.mipmap.bottom_collect_selected, getResources().getString(R.string.collect)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.bottom_my, R.mipmap.bottom_my_selected, getResources().getString(R.string.my)));

        if (MyApplication.lx == true) {
            bottomBar.setCurrentItem(4);
        }

        bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
                if (USERBEAN == null) {
                    getUserInfo();
                }
                if (position == 4) {
                    getUserInfo();
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    public void setSelectBar(int position) {
        bottomBar.setCurrentItem(position);
    }


    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    public void startWithPopFragment(SupportFragment targetFragment) {
        startWithPop(targetFragment);
    }


    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    protected void initData() {
        super.initData();
        TOKEN = new SPUtils(SP_NAME).getString(SP_TOKEN);
        if (TextUtils.isEmpty(TOKEN))
            login();
        if (USERBEAN == null)
            getUserInfo();
    }


}
