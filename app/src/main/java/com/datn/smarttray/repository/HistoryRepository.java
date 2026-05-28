package com.datn.smarttray.repository;

import android.util.Log;

import com.datn.smarttray.api.ApiClient;
import com.datn.smarttray.api.HistoryApiService;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {

    private static final List<History> historyList = new ArrayList<>();
    private static boolean isLoaded = false;
    private static final HistoryApiService api =
            ApiClient.getHistoryApi();

    public static void getHistorys(
            HistoryCallback callback
    ) {
        if(isLoaded){
            callback.onSuccess(historyList);
            return;
        }
        Log.d("API_DEBUG", "CALL API START");

        api.getHistorys()
                .enqueue(new Callback<List<History>>() {
                    @Override
                    public void onResponse(
                            Call<List<History>> call,
                            Response<List<History>> response
                    ) {
                        Log.d(
                                "API_DEBUG",
                                "CODE: " + response.code()
                        );

                        Log.d(
                                "API_DEBUG",
                                "BODY NULL: " + (response.body() == null)
                        );
                        if(response.isSuccessful()
                                && response.body() != null){
                            Log.d(
                                    "API_DEBUG",
                                    "SIZE: " + response.body().size()
                            );

                            isLoaded = true;
                            historyList.clear();
                            historyList.addAll(response.body());
                            callback.onSuccess(historyList );
                        }
                        else{
                            Log.e(
                                    "API_DEBUG",
                                    "RESPONSE FAIL"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<History>> call,
                            Throwable t
                    ) {
                        Log.e(
                                "API_DEBUG",
                                "FAIL: " + t.getMessage()
                        );
                        callback.onError(
                                t.getMessage()
                        );
                    }
                });
    }
    public static void refreshHistory(HistoryCallback callback){
        isLoaded = false;
        getHistorys(callback);
    }
    public static void getHistoryById(
            String id,
            SingleHistoryCallback callback
    ){

        api.getHistoryById(id)
                .enqueue(new Callback<History>() {

                    @Override
                    public void onResponse(
                            Call<History> call,
                            Response<History> response
                    ) {

                        if(response.isSuccessful()
                                && response.body() != null){

                            callback.onSuccess(
                                    response.body()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<History> call,
                            Throwable t
                    ) {

                        callback.onError(
                                t.getMessage()
                        );
                    }
                });
    }
    public static History getHistoryLocalById( String id )
    {
        for(History history : historyList)
        {
            if(history.getId().equals(id))
            {
                return history;
            }
        }
        return null;
    }

    public static List<History> getCachedHistory()
    {
        return historyList;
    }
    public static void addHistory(

            MultipartBody.Part filePart,

            RequestBody dataBody,

            SimpleCallback callback
    ){

        api.addHistory(
                filePart,
                dataBody
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {
                Log.d(
                        "API_AD",
                        "CODE: " + response.code()
                );

                if(response.isSuccessful()){
                    refreshHistory(new HistoryCallback() {
                        @Override
                        public void onSuccess(List<History> historys) {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    });
                    isLoaded = false;
                    callback.onSuccess();
                }
                else{
                    try {

                        Log.e(
                                "API_ADD",
                                "ERROR BODY: "
                                        + response.errorBody().string()
                        );

                    } catch (Exception e) {

                        Log.e(
                                "API_ADD",
                                e.getMessage()
                        );
                    }

                    callback.onError(
                            "Response fail: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {
                Log.e(
                        "API_ADD",
                        "FAIL: " + t.toString()
                );

                callback.onError(
                        t.getMessage()
                );
            }
        });
    }

    public static void deleteHistory(
            String id,
            SimpleCallback callback
    ){

        api.deleteHistory(id)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        callback.onError(
                                t.getMessage()
                        );
                    }
                });
    }

    public interface HistoryCallback {

        void onSuccess(List<History> historys);

        void onError(String error);
    }

    public interface SingleHistoryCallback {

        void onSuccess(History history);

        void onError(String error);
    }

    public interface SimpleCallback {

        void onSuccess();

        void onError(String error);
    }


}
