package com.datn.smarttray.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.datn.smarttray.model.History;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private static final String PREF_NAME = "history_pref";

    private static final String KEY_HISTORY = "history_list";

    private static List<History> historyList =
            new ArrayList<>();

    public static void init(Context context){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        String json =
                sharedPreferences.getString(
                        KEY_HISTORY,
                        ""
                );

        if(!json.isEmpty()){

            Gson gson = new Gson();

            Type type =
                    new TypeToken<List<History>>(){}.getType();

            historyList =
                    gson.fromJson(json, type);
        }

        if(historyList == null){

            historyList = new ArrayList<>();
        }
    }

    // SAVE LOCAL
    private static void saveToLocal(Context context){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        Gson gson = new Gson();

        String json =
                gson.toJson(historyList);

        sharedPreferences
                .edit()
                .putString(KEY_HISTORY, json)
                .apply();
    }

    public static List<History> getHistoryList() {
        return historyList;
    }

    public static void addHistory(Context context,History history) {

        historyList.add(0, history);
        saveToLocal(context);

    }
    public static History findHistoryById(String id){
        for(History history:historyList){
            if(history.getId().equals(id))
                return history;
        }
        return null;
    }
    public static void deleteHistory(Context context,History history) {

        historyList.remove(history);
        saveToLocal(context);

    }
}
