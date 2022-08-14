package com.example.fileexplorer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.fileexplorer.FileViewAdapter;
import com.example.fileexplorer.FileModel;
import com.example.fileexplorer.R;
import com.example.fileexplorer.databinding.FragmentSecondBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;
    File storage;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String path = getArguments().getString("path");
            File f = new File(path);
            storage = f;
        }

        if (storage == null) {
            String externalStorage = System.getenv("EXTERNAL_STORAGE");
            storage = new File(externalStorage);
        }

        binding.tvHeader.setText(storage.getAbsolutePath());

        if (!storage.getAbsolutePath().equals(System.getenv("EXTERNAL_STORAGE"))) {
            binding.myToolBar.setNavigationIcon(R.drawable.ic_back);
            binding.myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        binding.myToolBar.inflateMenu(R.menu.menu_main);

        runtimePermission();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public List<FileModel> getFiles(File file) {
        List<FileModel> result = new ArrayList<>();
        File[] files = file.listFiles();

        if (files == null)
            return result;

        for (File f : files) {
            if (f.isDirectory()) {
                int countItems = 0;
                File[] listFiles = f.listFiles();
                if (listFiles != null) {
                    for (File f1 : listFiles) {
                        countItems++;
                    }
                }
                result.add(new FileModel(0, f.getAbsolutePath(), f.getName(), new Date(f.lastModified()).toString(), countItems, ""));
            }
            else {
                result.add(new FileModel(1, f.getAbsolutePath(), f.getName(), new Date(f.lastModified()).toString(), 0, Formatter.formatShortFileSize(getContext(), f.length())));
            }
        }

        return result;
    }

    private void displayFiles() {
        List<FileModel> files = new ArrayList<>();
        files.addAll(getFiles(storage));

        FileViewAdapter fileViewAdapter = new FileViewAdapter(getParentFragmentManager(), getContext(), files);
        binding.setCustomAdapter(fileViewAdapter);
    }
}