/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

/**
 *
 */
public class ConfirmBillFragment extends Fragment {

    private TextView mTextViewAmount;
    private EditText mEditTextRoadToll;
    private EditText mEditTextParkingRate;

    private SlideButton mSlideButton;

    public ConfirmBillFragment() {
        // 所需空构造器
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充布局
        return inflater.inflate(R.layout.fragment_confirm_bill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextViewAmount=getActivity().findViewById(R.id.textViewDriverOrderBillAmount);
        mEditTextRoadToll=getActivity().findViewById(R.id.editTextDriverOrderBillItemRoadToll);
        mEditTextParkingRate=getActivity().findViewById(R.id.editTextDriverOrderBillItemParkingRate);

        mSlideButton=getActivity().findViewById(R.id.slideButtonConfirmBill);

        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 获取账单项金额
                double amount=0;
                double roadToll=0;
                double parkingRate=0;
                String amountString=mTextViewAmount.getText().toString();
                String roadTollString=mEditTextRoadToll.getText().toString();
                String parkingRateString=mEditTextParkingRate.getText().toString();
                if(amountString.isEmpty()==false){
                    amount=Double.parseDouble(amountString);
                }
                if(roadTollString.isEmpty()==false){
                    roadToll=Double.parseDouble(roadTollString);
                }
                if(parkingRateString.isEmpty()==false){
                    parkingRate=Double.parseDouble(parkingRateString);
                }

                // 计算账单金额
                double total=amount+roadToll+parkingRate;


                Bundle bundle=new Bundle();
                bundle.putDouble(Constant.EXTRA_TOTAL,total);

                RankPassengerFragment rankPassengerFragment=new RankPassengerFragment();
                rankPassengerFragment.setArguments(bundle);

                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();

                // 跳转评价乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,rankPassengerFragment,null);
                fragmentTransaction.commit();
            }
        });
    }
}