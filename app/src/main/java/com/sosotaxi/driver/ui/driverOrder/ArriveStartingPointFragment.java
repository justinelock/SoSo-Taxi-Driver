/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.ui.navigation.NavigationActivity;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 到达上车地点界面
 */
public class ArriveStartingPointFragment extends Fragment {

    private BNRoutePlanNode mStartNode;

    private BNRoutePlanNode mEndNode;

    private String mStartPlace;

    private String mCurrentPlace;

    private String mEndPlace;

    private String mStartCity;

    private String mCurrentCity;

    private String mEndCity;

    private double mCurrentLatitude;

    private double mCurrentLongitude;

    private LocationManager mLocationManager;

    private LocationListener mLocationListener;

    private RoutePlanSearch mSearch;

    private BaiduMap mBaiduMap;

    private MapView mBaiduMapView;
    private ConstraintLayout mConstraintLayoutNavigation;
    private TextView mTextViewNavigation;
    private ImageButton mImageButtonNavigation;
    private ImageButton mImageButtonText;
    private ImageButton mImageButtonPhone;
    private TextView mTextViewFrom;
    private TextView mTextViewTo;
    private SlideButton mSlideButton;


    public ArriveStartingPointFragment() {
        // 所需空构造器
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCurrentLatitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();

                // DEBUG
                Toast.makeText(getContext(), mCurrentLatitude + ", " + mCurrentLongitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充布局
        return inflater.inflate(R.layout.fragment_arrive_starting_point, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取控件
        mBaiduMapView = getActivity().findViewById(R.id.baiduMapViewDriverOrderArriveStartingPoint);
        mImageButtonNavigation = getActivity().findViewById(R.id.imageButtonDriverOrderArriveStartingPointNavigation);
        mImageButtonText=getActivity().findViewById(R.id.buttonDriverOrderArriveStartingPointText);
        mImageButtonPhone=getActivity().findViewById(R.id.buttonDriverOrderArriveStartingPointPhone);
        mConstraintLayoutNavigation = getActivity().findViewById(R.id.constraintLayoutArriveStartingPointNavigation);
        mTextViewNavigation=getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointNavigation);
        mSlideButton = getActivity().findViewById(R.id.slideButtonArriveStartingPoint);
        mTextViewFrom = getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointDetailFrom);
        mTextViewTo = getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointDetailTo);

        // 不显示地图比例尺及缩放控件
        mBaiduMapView.showZoomControls(false);
        mBaiduMapView.showScaleControl(false);

        // 获取百度地图对象
        mBaiduMap = mBaiduMapView.getMap();

        // 获取路径规划对象
        mSearch = RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(listener);

        mStartCity = "北京";
        mCurrentCity = "北京";
        mEndCity = "北京";

        mStartPlace = mTextViewFrom.getText().toString();
        mCurrentPlace = "西二旗地铁站";
        mEndPlace = mTextViewTo.getText().toString();

        // 设置短信按钮监听器
        mImageButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Manifest.permission.SEND_SMS)==false) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constant.PERMISSION_SEND_SMS_REQUEST);
                        return;
                    }
                }
                String phone="+86 10086";
                String content="您好，我已接单，预计在5分01秒内到达上车点，请做好上车准备。";
                ContactHelper.sendMessage(getContext(),phone,content);
            }
        });

        // 设置电话按钮监听器
        mImageButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Manifest.permission.CALL_PHONE)==false) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constant.PERMISSION_CALL_PHONE_REQUEST);
                        return;
                    }
                }
                String phone="+86 10086";
                ContactHelper.makeCall(getContext(),phone);
            }
        });

        // 设置滑动按钮监听器
        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转接到乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new PickUpPassengerFragment(),null);
                fragmentTransaction.commit();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Constant.AUTH_ARRAY_NAVIGATION) == false) {
                        requestPermissions(Constant.AUTH_ARRAY_NAVIGATION, Constant.PERMISSION_SEND_SMS_REQUEST);
                        return;
                    }

                }

                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
//                    if (mCurrentLatitude == 0 && mCurrentLongitude == 0) {
//                        return;
//                    }
                    mStartNode = new BNRoutePlanNode.Builder()
                            .latitude(40.05087)
                            .longitude(116.30142)
                            .name("百度大厦")
                            .description("百度大厦")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();
                     mEndNode = new BNRoutePlanNode.Builder()
                             .latitude(39.98340)
                             .longitude(116.42532)
                             .name("奥体中心")
                             .description("奥体中心")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();

                    NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
                }
            }
        };

        mConstraintLayoutNavigation.setOnClickListener(onClickListener);
        mImageButtonNavigation.setOnClickListener(onClickListener);
        mTextViewNavigation.setOnClickListener(onClickListener);

        NavigationHelper.init();
        initLocation();
        initRoutePlan();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBaiduMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBaiduMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBaiduMapView.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String phone="+86 10086";
        String content="您好，我已接单，预计在5分01秒内到达上车点，请做好上车准备。";
        switch (requestCode){
            case Constant.PERMISSION_SEND_SMS_REQUEST:
                if(PermissionHelper.hasBaseAuth(getContext(),Manifest.permission.SEND_SMS)==false){
                    Toast.makeText(getContext(), R.string.hint_permission_send_message_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                ContactHelper.sendMessage(getContext(),phone,content);
                break;
            case Constant.PERMISSION_CALL_PHONE_REQUEST:
                if(PermissionHelper.hasBaseAuth(getContext(),Manifest.permission.CALL_PHONE)==false){
                    Toast.makeText(getContext(), R.string.hint_permission_call_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                ContactHelper.makeCall(getContext(),phone);
                break;
            case Constant.PERMISSION_NAVIGATION_REQUEST:
                if (PermissionHelper.hasBaseAuth(getContext(),Constant.AUTH_ARRAY_NAVIGATION) == false) {
                    Toast.makeText(getContext(), R.string.hint_permission_navigation_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
                break;

        }
    }

    private void initRoutePlan() {
        Toast.makeText(getContext(), R.string.hint_route_planning, Toast.LENGTH_SHORT).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(mCurrentCity, mCurrentPlace);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(mStartCity, mStartPlace);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, mLocationListener);
        }
    }



    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //创建DrivingRouteOverlay实例
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            // 清除原有路线
            overlay.removeFromMap();
            List<DrivingRouteLine> routes = drivingRouteResult.getRouteLines();
            if (routes != null && routes.size() > 0) {
                //获取路径规划数据
                //为DrivingRouteOverlay实例设置数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                //在地图上绘制路线
                overlay.addToMap(true);
                overlay.zoomToSpanPaddingBounds(100, 100, 100, 400);
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };
}