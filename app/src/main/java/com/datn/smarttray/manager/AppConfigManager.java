package com.datn.smarttray.manager;

import com.datn.smarttray.enums.ModelType;

public class AppConfigManager {

    private static ModelType currentModel =
            ModelType.FOOD40_MODEL;

    public static void setCurrentModel(
            ModelType modelType
    ){
        currentModel = modelType;
    }

    public static ModelType getCurrentModel(){
        return currentModel;
    }

}