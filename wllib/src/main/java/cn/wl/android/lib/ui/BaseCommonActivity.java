package cn.wl.android.lib.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import cn.wl.android.lib.R;
import cn.wl.android.lib.ui.internal.IAlertAction;
import cn.wl.android.lib.ui.internal.ILazyLoad;
import cn.wl.android.lib.ui.internal.ILifeCycle;
import cn.wl.android.lib.ui.internal.IRxOption;
import cn.wl.android.lib.ui.internal.IStatusAction;
import cn.wl.android.lib.ui.internal.LifecycleAnn;
import cn.wl.android.lib.utils.ActStack;
import cn.wl.android.lib.utils.FileSaveHelper;
import cn.wl.android.lib.utils.result.RxResult;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by JustBlue on 2019-08-26.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public abstract class BaseCommonActivity extends AppCompatActivity implements ILazyLoad, IStatusAction, ILifeCycle, IAlertAction, IRxOption {

    private EventBus mEventBus;
    private RxResult mRxResult;

    private RxPermissions mPermissions;

    private PublishSubject<Integer> mCycleSubject = PublishSubject.create();

    protected Boolean isLazyLoaded = false;
    protected BaseCommonActivity mActivity;

    public static OnActivityCallback mCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        ActStack.get().addActivity(this);

        if (isFitStatusBar()) {
            ImmersionBar.with(mActivity)
                    .transparentStatusBar()
                    .statusBarDarkFont(true)
                    .statusBarColor(R.color.translucent_real)
                    .navigationBarColor(R.color.md_white)
                    .init();
        }

        beforeCreateView(savedInstanceState);
        initContentView(savedInstanceState);

        internalViewCreated();

        initViewCreated(savedInstanceState);

        publishLifecycle(CYC_CREATE);

        try {
            if (mCall != null) {
                mCall.onCreated(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    protected boolean isFitStatusBar() {
        return true;
    }

    /**
     * ???????????????????????????{@link #initViewCreated(Bundle)}?????????
     */
    @CallSuper
    protected void internalViewCreated() {

    }

    /**
     * ??????{@link RxResult}??????
     *
     * @return
     */
    @Override
    public RxResult getRxResult() {
        if (mRxResult == null) {
            mRxResult = new RxResult(mActivity);
        }

        return mRxResult;
    }

    @Override
    public RxPermissions getRxPermission() {
        if (mPermissions == null) {
            mPermissions = new RxPermissions(mActivity);
        }
        return mPermissions;
    }

    /**
     * ??????{@link EventBus}??????
     *
     * @return
     */
    protected EventBus getEventBus() {
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        return mEventBus;
    }

    @Override
    protected void onStart() {
        super.onStart();

        publishLifecycle(CYC_START_);
    }

    @Override
    protected void onResume() {
        super.onResume();

        publishLifecycle(CYC_RESUME);

        if (!isLazyLoaded) {
            isLazyLoaded = true;

            loadData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        publishLifecycle(CYC_PASUE_);
    }

    @Override
    protected void onStop() {
        super.onStop();

        publishLifecycle(CYC_STOP_X);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mCall != null) {
                mCall.onDestroy(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();

        if (mEventBus != null) {
            try {
                mEventBus.unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ActStack.get().delActivity(this);
        publishLifecycle(CYC_DESTROY);
        mCycleSubject.onComplete();
    }

    @Override
    public Context getOptionContext() {
        return this;
    }

    /**
     * ??????????????????
     *
     * @param cycle
     */
    private void publishLifecycle(@LifecycleAnn int cycle) {
        mCycleSubject.onNext(cycle);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    protected abstract Object getLayoutResource();

    /**
     * ???{@link AppCompatActivity#setContentView(View)}????????????
     * ???????????????????????????
     *
     * @param savedInstanceState
     */
    protected abstract void initViewCreated(@Nullable Bundle savedInstanceState);

    /**
     * ????????????????????????
     *
     * @param savedInstanceState
     */
    protected void beforeCreateView(Bundle savedInstanceState) {

    }

    @CallSuper
    protected void initContentView(Bundle savedInstanceState) {
        Object view = getLayoutResource();

        if (view instanceof Integer) {
            setContentView((int) view);
        } else if (view instanceof View) {
            setContentView((View) view);
        } else {
            throw new IllegalArgumentException("activity must set Content View!");
        }
    }

    @Override
    public <T> ObservableTransformer<T, T> bindDestroy() {
        return upstream -> upstream.takeUntil(mCycleSubject
                .filter(cycle -> cycle == CYC_DESTROY)
                .take(1)
        );
    }

    @Override
    public void doOnDestroy(Runnable runDestroy) {
        mCycleSubject
                .filter(cycle -> cycle == CYC_DESTROY)
                .subscribe(data -> runDestroy.run());
    }

    @Override
    public boolean isLazyLoaded() {
        return isLazyLoaded;
    }

    @Override
    public void retryLoadData() {
        loadData();
    }

    @Override
    public void loadData() {

    }

    /**
     * ???????????????????????????
     * ???????????????????????????????????????????????????
     *
     * @param time
     * @param runDelay
     * @return
     */
    public Disposable timerDelay(int time, Runnable runDelay) {
        return Observable.timer(time, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindDestroy())
                .subscribe(d -> runDelay.run());
    }

    /**
     * ????????????????????????, ???????????????1S
     * ????????????0??????????????????, ???????????????????????????
     *
     * @param time   ?????????????????????
     * @param doDown ?????????????????????
     * @return
     */
    public Disposable countdown(int time, Consumer<Integer> doDown) {
        try {
            doDown.accept(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindDestroy())
                .map(t -> (int) (time - t))
                .takeUntil(t -> t <= 0)
                .subscribe(t -> doDown.accept(t));
    }

    /**
     * ????????????????????????
     *
     * @param tView
     * @param doNext
     */
    protected void doTextChange(@NonNull TextView tView, @NonNull Consumer<CharSequence> doNext) {
        final TextWatcher tw = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    doNext.accept(tView.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        tView.addTextChangedListener(tw);

        doOnDestroy(() -> tView.removeTextChangedListener(tw));
    }

    /**
     * ????????????{@link View#layout(int, int, int, int)}}????????????
     *
     * @param view
     * @param runLayout
     */
    protected void doLayout(@NonNull View view, @NonNull Runnable runLayout) {
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                runLayout.run();
            }
        };

        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        doOnDestroy(() -> view.getViewTreeObserver().removeOnGlobalLayoutListener(listener));
    }

}
