package com.app.video.videoapps.fragment.collect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.video.videoapps.MyApplication;
import com.app.video.videoapps.R;
import com.app.video.videoapps.adapter.collect.CollectMVFragmentAdapter;
import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.base.MySupportFragment;
import com.app.video.videoapps.bean.CollectBean;
import com.app.video.videoapps.event.CollectMVEvent;
import com.app.video.videoapps.event.UserLoginOther;
import com.app.video.videoapps.fragment.MainFragment;
import com.app.video.videoapps.fragment.VideoPlayFragment;
import com.app.video.videoapps.http.ApiServiceResult;
import com.app.video.videoapps.http.Client;
import com.app.video.videoapps.http.RxsRxSchedulers;
import com.app.video.videoapps.utils.SpaceItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.video.videoapps.http.AppConfig.TOKEN;

public class CollectMVFragment extends MySupportFragment {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.lx_text)
    TextView lx_text;



    private Unbinder unbinder;

    private CollectMVFragmentAdapter adapter;
    private List<CollectBean> data;

    private GridLayoutManager gridLayoutManager;

    public static CollectMVFragment newInstance() {
        CollectMVFragment fragment = new CollectMVFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.recyclerlayout, container, false);
            unbinder = ButterKnife.bind(this, mRootView);
            initView();
        } else {
            unbinder = ButterKnife.bind(this, mRootView);
        }

        return mRootView;
    }

    private void initView() {

        if (MyApplication.lx == true) {
            lx_text.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            lx_text.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
        setRefreshLayout();
        data = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(_mActivity, 2);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.addItemDecoration(new SpaceItemDecoration(15, 0));

        adapter = new CollectMVFragmentAdapter(data);
        View emptyView = View.inflate(_mActivity, R.layout.empty2, null);
        emptyView.findViewById(R.id.tv_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment) getParentFragment().getParentFragment()).setSelectBar(0);
            }
        });

        adapter.setEmptyView(emptyView);
        recycler.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(VideoPlayFragment.newInstance(data.get(position).getOid() + ""));

            }
        });

        initData();
    }


    private void initData() {
        Client.getApiService().getCollectList(TOKEN, "5", page + "")
                .compose(RxsRxSchedulers.<BaseBean<List<CollectBean>>>io_main())
                .subscribe(new ApiServiceResult<List<CollectBean>>(getComposite()) {
                    @Override
                    public void onNext(BaseBean<List<CollectBean>> bean) {
                        if (bean != null && bean.getData() != null && !bean.getData().isEmpty())
                            setData(bean);

                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefreshing();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefreshing();
                    }
                });
    }

    private void setData(BaseBean<List<CollectBean>> bean) {
        if (page == 1) {
            data.clear();
            refreshLayout.setEnableLoadmore(true);
        }
        page++;
        data.addAll(bean.getData());
        adapter.notifyDataSetChanged();
    }

    private void setRefreshLayout() {
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableOverScroll(false);
        refreshLayout.setHeaderHeight(55);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                initData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectEvent(CollectMVEvent event) {
        if (TextUtils.equals(event.getFrom(), "MV") || TextUtils.equals(event.getFrom(), "MVPLAY")) {
            if (event.isAdd()) {
                data.add(event.getCollectBean());
                adapter.notifyItemInserted(data.size());
            } else {

                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getOid() == event.getCollectBean().getOid()) {
                        data.remove(i);
//                        adapter.notifyItemRemoved(i); ????????? ?????????  Called attach on a child which is not detached: ViewHolder
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userInfoEvent(UserLoginOther event) {
        page = 1;
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
