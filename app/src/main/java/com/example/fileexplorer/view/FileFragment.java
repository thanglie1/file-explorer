package com.example.fileexplorer.view;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.fileexplorer.adapter.FileViewAdapter;
import com.example.fileexplorer.databinding.FragmentFileBinding;
import com.example.fileexplorer.model.FileModel;
import com.example.fileexplorer.R;
import com.example.fileexplorer.model.SettingModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {
    private FragmentFileBinding binding;
    private File storage;
    List<FileModel> fileModelList;
    private SettingModel settingModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        binding = FragmentFileBinding.inflate(inflater, container, false);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sortType = preferences.getInt("sortType", 0);
        int viewType = preferences.getInt("viewType", 0);
        boolean ascending = preferences.getBoolean("ascending", true);
        settingModel = new SettingModel(sortType, ascending, viewType);

        binding.tvHeader.setText(storage.getAbsolutePath());

        ((MainActivity) getActivity()).setSupportActionBar(binding.myToolBar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!storage.getAbsolutePath().equals(System.getenv("EXTERNAL_STORAGE"))) {
            binding.myToolBar.setNavigationIcon(R.drawable.ic_back);
            binding.myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        runtimePermission();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_setting, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_list || item.getItemId() == R.id.action_grid) {
            settingModel.setViewType(item.getOrder());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putInt("viewType", item.getOrder());
            prefEditor.commit();
            filter("");
        }

        return super.onOptionsItemSelected(item);
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
                result.add(new FileModel(0, f.getAbsolutePath(), f.getName(), f.lastModified(),
                        countItems, f.length(), Formatter.formatShortFileSize(getContext(), f.length())));
            } else {
                result.add(new FileModel(1, f.getAbsolutePath(), f.getName(), f.lastModified(),
                        0, f.length(), Formatter.formatShortFileSize(getContext(), f.length())));
            }
        }

        return result;
    }

    private void displayFiles() {
        fileModelList = new ArrayList<>();
        fileModelList.addAll(getFiles(storage));

        updateAdapter(fileModelList);
    }

    private void filter(String newText) {
        List<FileModel> filteredList = new ArrayList<>();
        for (FileModel fm : fileModelList) {
            if (fm.getFileName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(fm);
            }
        }
        updateAdapter(filteredList);
    }

    public void updateAdapter(List<FileModel> fileModels) {
        FileViewAdapter fileViewAdapter = new FileViewAdapter(getParentFragmentManager(), getContext(), fileModels, settingModel);
        ((GridLayoutManager) binding.recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (settingModel.getViewType() == SettingModel.LIST)
                    return 3;
                return position == 0 ? 3 : 1;
            }
        });
        binding.setCustomAdapter(fileViewAdapter);
    }
}