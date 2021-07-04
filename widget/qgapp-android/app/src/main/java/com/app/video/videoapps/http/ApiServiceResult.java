package com.app.video.videoapps.http;

import android.util.Log;

import com.app.video.videoapps.base.BaseBean;
import com.app.video.videoapps.utils.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * 网络请求回调解析
 * Created by Administrator on 2017/12/15.
 */

public abstract class ApiServiceResult<T> implements Observer<BaseBean<T>> {
    CompositeDisposable composite;

    public ApiServiceResult(CompositeDisposable composite) {
        this.composite = composite;
    }

    public ApiServiceResult() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        if (composite != null)
            composite.add(d);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("onError: ", throwable.toString());
        ToastUtils.showShortToastSafe(throwable.toString());
    }


    @Override
    public void onNext(BaseBean<T> bean) {
        if (!bean.getCode().equals("0"))
            ToastUtils.showShortToast(bean.getMsg());
    }
}
