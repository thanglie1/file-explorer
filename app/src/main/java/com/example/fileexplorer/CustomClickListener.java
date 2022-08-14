package com.example.fileexplorer;

import android.view.View;

import com.example.fileexplorer.model.FileModel;
import com.example.fileexplorer.model.SettingModel;

public interface CustomClickListener {
    void itemClicked(FileModel f);
    void headerClicked(View view);
    void orderClicked(SettingModel sett);
}
