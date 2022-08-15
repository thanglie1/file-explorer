package com.example.fileexplorer.model;

import com.example.fileexplorer.enums.SortType;

public class SettingModel {
    public static final int LIST = 0;
    public static final int GRID = 1;

    private int sortType;
    private boolean ascending;
    private int viewType;

    private static SettingModel settingModel = null;
    public static  SettingModel getInstance() {
        if (settingModel == null) {
            settingModel = new SettingModel();
        }
        return settingModel;
    }

    private SettingModel() {
        this.sortType = 0;
        this.ascending = true;
        this.viewType = 0;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getSortText() {
        switch (SortType.valueOf(sortType)) {
            case DATE:
                return "Date";
            case TYPE:
                return "Type";
            case SIZE:
                return "Size";
            default:
                return "Name";
        }
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }
}
