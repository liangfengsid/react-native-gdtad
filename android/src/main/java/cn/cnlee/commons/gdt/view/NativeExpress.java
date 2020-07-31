package cn.cnlee.commons.gdt.view;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressAD.NativeExpressADListener;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.cfg.VideoOption;

import cn.cnlee.commons.gdt.R;


public class NativeExpress extends FrameLayout {

    private NativeExpressADView mNative;
    private NativeExpressAD mAd;
    private Runnable mLayoutRunnable;

    public NativeExpress(Context context, String posID, NativeExpressADListener listener) {
        this(context, null, posID, listener);
    }

    public NativeExpress(Context context, AttributeSet attrs, String posID, NativeExpressADListener listener) {
        this(context, attrs, 0, posID, listener);
    }

    public NativeExpress(Context context, AttributeSet attrs, int defStyleAttr, String posID, NativeExpressADListener listener) {
        super(context, attrs, defStyleAttr);
        // 把布局加载到这个View里面
        inflate(context, R.layout.layout_banner,this);
        initView(posID, listener);
    }

    /**
     * 初始化View
     */
    private void initView(String posID, NativeExpressADListener listener) {
        closeNative();
        try {
            /**
             *  如果选择支持视频的模版样式，请使用{@link PositionId#NATIVE_EXPRESS_SUPPORT_VIDEO_POS_ID}
             */
            ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
            mAd = new NativeExpressAD((Activity) this.getContext(), adSize, posID, listener); // 这里的Context必须为Activity
            VideoOption option = NativeExpress.getVideoOption();
            if(option != null){
                // setVideoOption是可选的，开发者可根据需要选择是否配置
                mAd.setVideoOption(option);
            }
            /**
             * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
             * 如果广告位仅支持图文广告，则无需调用
             */

            /**
             * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
             *
             * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
             *
             * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
             */
            mAd.setVideoPlayPolicy(getVideoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI, this.getContext()));  // 本次拉回的视频广告，在用户看来是否为自动播放的
            mAd.loadAD(1);
        } catch (NumberFormatException e) {
            Log.w("GDTNativeExpress", "ad size invalid.");
        }
    }

    public void closeNative() {
        removeAllViews();
        if (mNative != null) {
            mNative.destroy();
            mNative = null;
            Log.e("GDTNativeExpress","关闭广告");
        }
        if (mLayoutRunnable != null){
            removeCallbacks(mLayoutRunnable);
        }
    }

    public NativeExpressADView getNative() {
        return mNative;
    }

    public void setNative(NativeExpressADView nativ) {
        mNative = nativ;
    }

    @Override
    protected void onDetachedFromWindow() {
        closeNative();
        super.onDetachedFromWindow();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mLayoutRunnable != null){
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

    public static VideoOption getVideoOption() {
        VideoOption.Builder builder = new VideoOption.Builder();

        builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
        builder.setAutoPlayMuted(true);
        builder.setDetailPageMuted(false);

        VideoOption videoOption = builder.build();
        return videoOption;
    }

    public static int getVideoPlayPolicy(int autoPlayPolicy, Context context){
        if(autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS){
            return VideoOption.VideoPlayPolicy.AUTO;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                    : VideoOption.VideoPlayPolicy.MANUAL;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER){
            return VideoOption.VideoPlayPolicy.MANUAL;
        }
        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }
}
