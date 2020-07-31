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
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressAD.NativeExpressADListener;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.constants.AdPatternType;

import java.util.Map;
import java.util.List;

import javax.annotation.Nullable;

import cn.cnlee.commons.gdt.view.NativeExpress;

public class GDTNativeExpressViewManager extends SimpleViewManager
        implements NativeExpressADListener, NativeExpressMediaListener {

    private static final String TAG = "GDTNativeExpress";

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
    public void onADLoaded(List<NativeExpressADView> adList) {
        Log.i(TAG,"onADLoaded");
        // 释放前一个展示的NativeExpressADView的资源
        if (nativeExpress != null) {
            nativeExpress.closeNative();
        }

        boolean isPreloadVideo = true;
        NativeExpressADView nativ = adList.get(0);
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        nativ.setAdSize(adSize);
        nativeExpress.setNative(nativ);
        nativeExpress.addView(nativ);
        if (nativ.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            nativ.setMediaListener(this);
            if(isPreloadVideo) {
                // 预加载视频素材，加载成功会回调mediaListener的onVideoCached方法，失败的话回调onVideoError方法errorCode为702。
                nativ.preloadVideo();
            }
        } else {
            isPreloadVideo = false;
        }
        if(!isPreloadVideo) {
            // 广告可见才会产生曝光，否则将无法产生收益。
            nativ.render();
        }
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_LOADED.toString(), null);
    }

    @Override
    public void onRenderFail(NativeExpressADView adView) {
        Log.i(TAG, "onRenderFail");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_RENDER_FAIL.toString(), null);
    }

    @Override
    public void onRenderSuccess(NativeExpressADView adView) {
        Log.i(TAG, "onRenderSuccess");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_RENDER_SUCCESS.toString(), null);
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
        Log.i(TAG,"onADExposure");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_EXPOSURE.toString(), null);
    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        Log.i(TAG,"onADClosed");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_CLOSE.toString(), null);
    }

    @Override
    public void onADClicked(NativeExpressADView adView) {
        Log.i(TAG,"onADClicked");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_ON_CLICK.toString(), null);
    }

    @Override
    public void onADLeftApplication(NativeExpressADView adView) {
        Log.i(TAG,"onADLeftApplication");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_LEAVE_APP.toString(), null);
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView adView) {
        Log.i(TAG,"onADOpenOverlay");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_OPEN_FULL_SCREEN.toString(), null);
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_DID_OPEN_FULL_SCREEN.toString(), null);
    }

    @Override
    public void onADCloseOverlay(NativeExpressADView adView) {
        Log.i(TAG,"onADCloseOverlay");
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_WILL_CLOSE_FULL_SCREEN.toString(), null);
        mEventEmitter.receiveEvent(mContainer.getId(), Events.EVENT_DID_CLOSE_FULL_SCREEN.toString(), null);
    }

    @Override
    public void onVideoInit(NativeExpressADView adView) {
        Log.i(TAG, "onVideoInit");
    }

    @Override
    public void onVideoLoading(NativeExpressADView adView) {
        Log.i(TAG, "onVideoLoading");
    }

    @Override
    public void onVideoCached(NativeExpressADView adView) {
        Log.i(TAG, "onVideoCached");
        // 视频素材加载完成，此时展示视频广告不会有进度条。
//        if(isPreloadVideo && nativeExpressADView != null) {
//            if(mContainer.getChildCount() > 0){
//                container.removeAllViews();
//            }
            // 广告可见才会产生曝光，否则将无法产生收益。
//            mContainer.addView(nativeExpressADView);
        if(adView != null) {
            adView.render();
        }
    }

    @Override
    public void onVideoReady(NativeExpressADView adView, long l) {
        Log.i(TAG, "onVideoReady");
    }

    @Override
    public void onVideoStart(NativeExpressADView adView) {
        Log.i(TAG, "onVideoStart");
    }

    @Override
    public void onVideoPause(NativeExpressADView adView) {
        Log.i(TAG, "onVideoPause");
    }

    @Override
    public void onVideoComplete(NativeExpressADView adView) {
        Log.i(TAG, "onVideoComplete");
    }

    @Override
    public void onVideoError(NativeExpressADView adView, AdError adError) {
        Log.i(TAG, "onVideoError");
    }

    @Override
    public void onVideoPageOpen(NativeExpressADView adView) {
        Log.i(TAG, "onVideoPageOpen");
    }

    @Override
    public void onVideoPageClose(NativeExpressADView adView) {
        Log.i(TAG, "onVideoPageClose");
    }

    public enum Events {
        EVENT_FAIL_TO_RECEIVED("onFailToReceived"),
        EVENT_LOADED("onLoaded"),
        EVENT_RENDER_FAIL("onFailed"),
        EVENT_RENDER_SUCCESS("onSucceeded"),
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
    private NativeExpress nativeExpress;

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
        String posID = appInfo.getString("posId");
        NativeExpress nativ = new NativeExpress(mThemedReactContext.getCurrentActivity(), posID, this);
        nativeExpress = nativ;
        view.addView(nativeExpress);
    }
}
