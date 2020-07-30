
import { NativeModules } from 'react-native';
import Banner from './libs/GDTBanner';
import UnifiedBanner from './libs/GDTUnifiedBanner';
import NativeExpress from './libs/GDTNativeExpress';
import Splash from './libs/GDTSplash';
const Module = NativeModules.GDTModule;

const GDT = {
    Banner,
    UnifiedBanner,
    NativeExpress,
    Splash,
    Module
};
module.exports = GDT;