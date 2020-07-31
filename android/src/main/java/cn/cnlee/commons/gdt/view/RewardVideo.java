package cn.cnlee.commons.gdt.view;

import android.util.Log;
import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Callback;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.util.HashMap;
import java.util.Map;

public class RewardVideo implements RewardVideoADListener {

    private static final String TAG = RewardVideo.class.getSimpleName();

    private static RewardVideo mInstance;
    private RewardVideoAD iad;
    private String posID;
    private Callback mOnReward;

    private ReactApplicationContext mContext;

    public static synchronized RewardVideo getInstance(ReactApplicationContext reactContext, Callback onReward) {
        if (mInstance == null) {
            mInstance = new RewardVideo(reactContext, onReward);
        }
        mInstance.mOnReward = onReward;
        return mInstance;
    }

    private RewardVideo(ReactApplicationContext reactContext, Callback onReward){
        this.mContext = reactContext;
        this.mOnReward = onReward;
    }

    @Override
    public void onADLoad() {
        Log.i(TAG,"onADLoad");
        if (!iad.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
            long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
            //广告展示检查3：展示广告前判断广告数据未过期
            if (SystemClock.elapsedRealtime() < (iad.getExpireTimestamp() - delta)) {
                iad.showAD();
            } else {
                Log.i(TAG, "激励视频广告已过期，请再次请求广告后进行广告展示！");
            }
        } else {
            Log.i(TAG, "此条广告已经展示过，请再次请求广告后进行广告展示！");
        }
    }

    @Override
    public void onVideoCached() {
        Log.i(TAG, "onVideoCached");
    }

    @Override
    public void onADShow() {
        Log.i(TAG,"onADShow");
    }

    @Override
    public void onADExpose() {
        Log.i(TAG,"onADExpose");
    }

    @Override
    public void onReward() {
        Log.i(TAG,"onADReward");
        if (this.mOnReward != null) {
            this.mOnReward.invoke("");
        }
    }

    @Override
    public void onADClose() {
        Log.i(TAG,"onADClose");
    }

    @Override
    public void onVideoComplete() {
        Log.i(TAG, "onVideoComplete");
    }

    @Override
    public void onADClick() {
        Log.i(TAG,"onADClick");
    }

    /**
     * 广告流程出错
     */
    @Override
    public void onError(AdError adError) {
        String msg = String.format("onError, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.i(TAG, "onError, adError=" + msg);
        if (this.mOnReward != null) {
            this.mOnReward.invoke(msg);
        }
    }

    public void showRewardVideoAD(String posID) {
        Log.i(TAG, "showRewardVideoAD");
        getIAD(posID).loadAD();
    }

    private RewardVideoAD getIAD(String posID) {
        if (iad != null && this.posID.equals(posID)) {
            Log.i(TAG,"======相同IAD无需创建新的======");
            return iad;
        }
        this.posID = posID;
        iad = new RewardVideoAD(mContext, posID, this, true);
        return iad;
    }
}
