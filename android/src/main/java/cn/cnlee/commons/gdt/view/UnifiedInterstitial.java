package cn.cnlee.commons.gdt.view;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;

import java.util.HashMap;
import java.util.Map;

public class UnifiedInterstitial implements UnifiedInterstitialADListener {

    private static final String TAG = UnifiedInterstitial.class.getSimpleName();

    private static UnifiedInterstitial mInstance;
    private UnifiedInterstitialAD iad;
    private String posID;
    private boolean asPopup;

    private ReactApplicationContext mContext;

    public static synchronized UnifiedInterstitial getInstance(ReactApplicationContext reactContext) {
        if (mInstance == null) {
            mInstance = new UnifiedInterstitial(reactContext);
        }
        return mInstance;
    }

    private UnifiedInterstitial(ReactApplicationContext reactContext){
        this.mContext = reactContext;
    }


    @Override
    public void onNoAD(AdError adError) {
        Log.e(TAG,"onNoAD: eCode=" + adError.getErrorCode() + ",eMsg=" + adError.getErrorMsg());
        WritableMap event = Arguments.createMap();
        event.putString("error", "BannerNoAD，eCode=" + adError);
    }

    @Override
    public void onADReceive() {
        Log.e(TAG,"onADReceive");
        if (this.iad != null){
            if (asPopup){
                this.iad.showAsPopupWindow();
            }else {
                this.iad.show();
            }
        }
    }

    @Override
    public void onVideoCached() {
        // 视频素材加载完成，在此时调用iad.show()或iad.showAsPopupWindow()视频广告不会有进度条。
        Log.i(TAG, "onVideoCached");
    }

    @Override
    public void onADOpened() {
        Log.i(TAG,"onADOpened");
    }

    @Override
    public void onADExposure() {
        Log.i(TAG,"onADExposure");
    }

    @Override
    public void onADClosed() {
        Log.i(TAG,"onADClosed");
    }

    @Override
    public void onADClicked() {
        Log.i(TAG,"onADClicked");
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG,"onADLeftApplication");
    }

    public void showUnifiedInterstitialAD(String appID, String posID, boolean asPopup) {
        Log.i(TAG, "showUnifiedInterstitialAD");
        this.asPopup = asPopup;
        getIAD(appID, posID).loadAD();
    }

    private UnifiedInterstitialAD getIAD(String appID, String posID) {
        if (iad != null && this.posID.equals(posID)) {
            Log.i(TAG,"======相同IAD无需创建新的======");
            return iad;
        }
        this.posID = posID;
        if (this.iad != null) {
            iad.close();
            iad.destroy();
            iad = null;
        }
        Map<String, String> tags = new HashMap<>();
        tags.put("tag_i1", "value_i1");
        tags.put("tag_i2", "value_i2");
        iad = new UnifiedInterstitialAD(mContext.getCurrentActivity(), appID, posID, this, tags);
        return iad;
    }
}
