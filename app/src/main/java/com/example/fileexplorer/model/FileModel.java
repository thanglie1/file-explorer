package com.example.fileexplorer.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileModel {
    private String fileName;
    private int numberFiles;
    private String fileSize;
    private long size ,dateModified;
    private int type;
    String path;

    public String getInfo() {
        if (type == 0) {
            return numberFiles + " Files";
        }
        return fileSize;
    }

    public String getLastModified(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
        return sdf.format(new Date(dateModified));
    }

    public FileModel(int type, String path, String fileName, long dateModified, int numberFiles, long size, String fileSize) {
        this.type = type;
        this.path = path;
        this.fileName = fileName;
        this.dateModified = dateModified;
        this.numberFiles = numberFiles;
        this.fileSize = fileSize;
        this.size = size;
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

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public int getNumberFiles() {
        return numberFiles;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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
