package com.tencent.example.batloccompar.loc;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class BaiduLocationManagerImpl extends AbsLocationManager {
    private LocationClient mLocationClient;
    private ILocationListener mUserLisener;

    BaiduLocationManagerImpl(Context context) {
        super(context);
        mLocationClient = new LocationClient(context);

        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(2000);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);

        BDAbstractLocationListener listener = new BDAbstractLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (mUserLisener != null) {
                    mUserLisener.onLocationChanged(transferLocation(bdLocation));
                }
            }

            private Location transferLocation(BDLocation bdLocation) {
                Location location = new Location(bdLocation.getLocTypeDescription());
                location.setAccuracy(bdLocation.getRadius());
                location.setAltitude(bdLocation.getAltitude());
                location.setLatitude(bdLocation.getLatitude());
                location.setLongitude(bdLocation.getLongitude());
                location.setBearing(bdLocation.getDirection());
                location.setSpeed(bdLocation.getSpeed());
                Bundle bundle = new Bundle();
                int locType = bdLocation.getLocType();
                if (locType == 61 || locType == 161) {
                    bundle.putInt("code", 0);
                }
                bundle.putString("reason", bdLocation.getLocTypeDescription());
                location.setExtras(bundle);
                if (locType == 61) {
                    location.setProvider("gps");
                } else if (locType == 161) {
                    location.setProvider("network");
                }
                return location;
            }
        };
        mLocationClient.registerLocationListener(listener);
    }

    @Override
    public void startLocation(ILocationListener listener) {
        mUserLisener = listener;
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    public void stopLocation() {
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }
}
