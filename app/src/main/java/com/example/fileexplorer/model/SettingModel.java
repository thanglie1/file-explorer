package com.example.fileexplorer.model;

import com.example.fileexplorer.enums.SortType;

public class SettingModel {
    private int sortType;
    private boolean ascending;

    public SettingModel(int sortType, boolean ascending) {
        this.sortType = sortType;
        this.ascending = ascending;
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
