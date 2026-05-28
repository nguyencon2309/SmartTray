package com.datn.smarttray.repository;

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
        api.getHistorys()
                .enqueue(new Callback<List<History>>() {
                    @Override
                    public void onResponse(
                            Call<List<History>> call,
                            Response<List<History>> response
                    ) {
                        if(response.isSuccessful()
                                && response.body() != null){
                            historyList.clear();
                            historyList.addAll(response.body());
                            callback.onSuccess(historyList );
                            isLoaded = true;
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<History>> call,
                            Throwable t
                    ) {

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

                if(response.isSuccessful()){
                    isLoaded = false;
                    callback.onSuccess();
                }
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
                        isLoaded = false;
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
