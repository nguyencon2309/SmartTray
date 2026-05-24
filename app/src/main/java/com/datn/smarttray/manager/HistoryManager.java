package com.datn.smarttray.manager;

import com.datn.smarttray.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private static final List<History> historyList =
            new ArrayList<>();

    public static List<History> getHistoryList() {
        return historyList;
    }

    public static void addHistory(History history) {
        int sizeList = historyList.size();
        historyList.add(sizeList, history);

    }
    public static History findHistoryById(String id){
        for(History history:historyList){
            if(history.getId().equals(id))
                return history;
        }
        return null;
    }
    public static void deleteHistory(History history) {

        historyList.remove(history);

    }
}
