package com.example.fileexplorer;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class FileModel {
    private String fileName, dateModified;
    private int numberFiles;
    private String fileSize;
    private int type;
    String path;

    public String getInfo() {
        if (type == 0) {
            return numberFiles + " Files";
        }
        return fileSize;
    }

    public FileModel(int type, String path, String fileName, String dateModified, int numberFiles, String fileSize) {
        this.type = type;
        this.path = path;
        this.fileName = fileName;
        this.dateModified = dateModified;
        this.numberFiles = numberFiles;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public int getNumberFiles() {
        return numberFiles;
    }

    public void setNumberFiles(int numberFiles) {
        this.numberFiles = numberFiles;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
