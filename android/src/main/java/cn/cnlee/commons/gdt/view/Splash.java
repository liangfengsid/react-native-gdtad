package cn.cnlee.commons.gdt.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.cnlee.commons.gdt.R;

public class Splash extends RelativeLayout {

    private static final String SKIP_TEXT = "点击跳过 %d";
    private ViewGroup container;
    private TextView skipView;
    private View logoView;
    private ImageView splashHolder;

    private SplashAD mSplashAD;
    private Runnable mLayoutRunnable;

    public Splash(Context context, String posID, SplashADListener listener, int fetchDelay) {
        super(context);
        // 把布局加载到这个View里面
        inflate(context, R.layout.layout_splash, this);
        container = findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        logoView = findViewById(R.id.app_logo);
        splashHolder = findViewById(R.id.splash_holder);
        initView(posID, listener, fetchDelay);
    }


    /**
     * 初始化View
     */
    private void initView(String posID, SplashADListener listener, int fetchDelay) {
        mSplashAD = new SplashAD((Activity) this.getContext(), skipView, posID, listener, fetchDelay);
    }

    public void showLogo(boolean show) {
        if (logoView != null) {
            logoView.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public void hideSplashHolder(boolean hide) {
        if (splashHolder != null) {
            splashHolder.setVisibility(hide ? INVISIBLE : VISIBLE);
        }
    }

    public void setTickTxt(long timeLeft) {
        if (skipView != null) {
            skipView.setText(String.format(Locale.CHINA, SKIP_TEXT, Math.round(timeLeft / 1000f)));
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mLayoutRunnable != null) {
            removeCallbacks(mLayoutRunnable);
        }
        mLayoutRunnable = new Runnable() {
            @Override
            public void run() {
                measure(
                        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
                layout(getLeft(), getTop(), getRight(), getBottom());
            }
        };
        post(mLayoutRunnable);
    }

}
