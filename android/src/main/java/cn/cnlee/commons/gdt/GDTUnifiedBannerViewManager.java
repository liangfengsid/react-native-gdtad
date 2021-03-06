package cn.cnlee.commons.gdt;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.gson.Gson;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.comm.util.AdError;

import java.util.Map;

import javax.annotation.Nullable;

import cn.cnlee.commons.gdt.view.UnifiedBanner;

public class GDTUnifiedBannerViewManager extends SimpleViewManager implements UnifiedBannerADListener {

    private static final String TAG = "GDTUnifiedBanner";

    // 重写getName()方法, 返回的字符串就是RN中使用该组件的名称
    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TAG,"onNoAD: eCode=" + adError.getErrorCode() + ",eMsg=" + adError.getErrorMsg());
        WritableMap event = Arguments.createMap();
        event.putString("error", new Gson().toJson(adError));
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_FAIL_TO_RECEIVED.toString(), event);
    }

    @Override
    public void onADReceive() {
        Log.i(TAG,"onADReceive");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_RECEIVED.toString(), null);
    }

    @Override
    public void onADExposure() {
        Log.i(TAG,"onADExposure");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_EXPOSURE.toString(), null);
    }

    @Override
    public void onADClosed() {
        Log.i(TAG,"onADClosed");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_CLOSE.toString(), null);
    }

    @Override
    public void onADClicked() {
        Log.i(TAG,"onADClicked");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_ON_CLICK.toString(), null);
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG,"onADLeftApplication");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_LEAVE_APP.toString(), null);
    }

    @Override
    public void onADOpenOverlay() {
        Log.i(TAG,"onADOpenOverlay");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_OPEN_FULL_SCREEN.toString(), null);
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_DID_OPEN_FULL_SCREEN.toString(), null);
    }

    @Override
    public void onADCloseOverlay() {
        Log.i(TAG,"onADCloseOverlay");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_CLOSE_FULL_SCREEN.toString(), null);
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_DID_CLOSE_FULL_SCREEN.toString(), null);
    }

    public enum Events {
        EVENT_FAIL_TO_RECEIVED("onFailToReceived"),
        EVENT_RECEIVED("onReceived"),
        EVENT_WILL_LEAVE_APP("onViewWillLeaveApplication"),
        EVENT_WILL_CLOSE("onViewWillClose"),
        EVENT_WILL_EXPOSURE("onViewWillExposure"),
        EVENT_ON_CLICK("onClicked"),
        EVENT_WILL_OPEN_FULL_SCREEN("onViewWillPresentFullScreenModal"),
        EVENT_DID_OPEN_FULL_SCREEN("onViewDidPresentFullScreenModal"),
        EVENT_WILL_CLOSE_FULL_SCREEN("onViewWillDismissFullScreenModal"),
        EVENT_DID_CLOSE_FULL_SCREEN("onViewDidDismissFullScreenModal");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private FrameLayout mContainer;
    private RCTEventEmitter mEventEmitter;
    private ThemedReactContext mThemedReactContext;
    private UnifiedBanner mBanner;

    @Override
    protected View createViewInstance(ThemedReactContext reactContext) {
        mThemedReactContext = reactContext;
        mEventEmitter = reactContext.getJSModule(RCTEventEmitter.class);
        FrameLayout container = new FrameLayout(reactContext);
        mContainer = container;
        return container;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    // 其中，可以通过@ReactProp（或@ReactPropGroup）注解来导出属性的设置方法。
    // 该方法有两个参数，第一个参数是泛型View的实例对象，第二个参数是要设置的属性值。
    // 方法的返回值类型必须为void，而且访问控制必须被声明为public。
    // 组件的每一个属性的设置都会调用Java层被对应ReactProp注解的方法
    @ReactProp(name = "appInfo")
    public void setAppInfo(FrameLayout view, final ReadableMap appInfo) {
        String appID = appInfo.getString("appId");
        String posID = appInfo.getString("posId");
        UnifiedBanner banner = new UnifiedBanner(mThemedReactContext.getCurrentActivity(), appID, posID, this);
        mBanner = banner;
        view.addView(banner);
    }

    @ReactProp(name = "interval")
    public void setInterval(FrameLayout view, int interval) {
        if (mBanner != null) mBanner.setInterval(interval);
    }

}
