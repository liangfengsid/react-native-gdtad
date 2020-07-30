package cn.cnlee.commons.gdt;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;

import cn.cnlee.commons.gdt.view.Hybrid;
import cn.cnlee.commons.gdt.view.Interstitial;
import cn.cnlee.commons.gdt.view.UnifiedInterstitial;
import cn.cnlee.commons.gdt.view.RewardVideo;
import com.qq.e.comm.managers.GDTADManager;


public class GDTModule extends ReactContextBaseJavaModule {

    private static final String TAG = "GDTModule";
    private ReactApplicationContext mContext;

    public GDTModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void init(String appID) {
        // Init GDT sdk
        GDTADManager.getInstance().initWith(mContext, appID);
    }

    @ReactMethod
    public void showUnifiedInterstitialAD(String appID, String posID, boolean asPopup) {
        UnifiedInterstitial unifiedInterstitial = UnifiedInterstitial.getInstance(mContext);
        unifiedInterstitial.showUnifiedInterstitialAD(appID, posID, asPopup);
    }

    @ReactMethod
    public void showInterstitialAD(String appID, String posID, boolean asPopup) {
        Interstitial interstitial = Interstitial.getInstance(mContext);
        interstitial.showInterstitialAD(appID, posID, asPopup);
    }

    @ReactMethod
    public void showRewardVideoAD(String posID, Callback onReward) {
        RewardVideo rewardVideo = RewardVideo.getInstance(mContext, onReward);
        rewardVideo.showRewardVideoAD(posID);
    }

    /***
     *
     * @param appID
     * @param url
     * @param settings ["titleBarHeight", "titleBarColor", "title", "titleColor", "titleSize", "backButtonImage", "closeButtonImage", "separatorColor", "backSeparatorLength"]
     * titleColor='#ff0000ff',titleBarHeight=45 dp, titleSize=20 sp, backButtonImage='gdt_ic_back'
     */
    @ReactMethod
    public void openWeb(String appID, String url, ReadableMap settings) {
        Hybrid hybrid = Hybrid.getInstance(mContext);
        hybrid.openWeb(appID, url, settings);
    }
}
