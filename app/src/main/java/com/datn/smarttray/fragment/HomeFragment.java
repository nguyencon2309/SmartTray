package com.datn.smarttray.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datn.smarttray.R;
import com.datn.smarttray.enums.ModelType;
import com.datn.smarttray.manager.AppConfigManager;
import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.manager.ModelManager;
import com.google.android.material.switchmaterial.SwitchMaterial;


public class HomeFragment extends Fragment {

    SwitchMaterial switchModel;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupSwitch();
        return view;
    }
    public void initViews(View view){
    switchModel = view.findViewById(R.id.switchModel);
    }
    public void setUpListener(){
        switchModel.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if(isChecked){
                        AppConfigManager.setCurrentModel(
                                ModelType.FOOD101_MODEL
                        );
                        Toast.makeText(
                                requireContext(),
                                "Đã chuyển sang Food 101",
                                Toast.LENGTH_SHORT
                        ).show();
                    }else{
                        AppConfigManager.setCurrentModel(
                                ModelType.FOOD40_MODEL
                        );
                        Toast.makeText(
                                requireContext(),
                                "Đã chuyển sang Food 40",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    setTextSwitch(isChecked);
                    reloadAllData();
                }
        );
    }
    private void setupSwitch(){
        boolean isFood101 =
                AppConfigManager.getCurrentModel()
                        == ModelType.FOOD101_MODEL;
        switchModel.setOnCheckedChangeListener(null);
        switchModel.setChecked(isFood101);
        setTextSwitch(isFood101);
        setUpListener();
    }
    private void setTextSwitch(boolean status){
        if(status){
            switchModel.setText("Food 101 ");
        }
        else{
            switchModel.setText("Food 40 ");
        }
    }
    private void reloadAllData(){
        ModelManager.reloadAll(
                requireContext(),
                new ModelManager.ReloadCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                requireContext(),
                                "Reload thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    @Override
                    public void onError(
                            String error
                    ) {
                        Toast.makeText(
                                requireContext(),
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

}