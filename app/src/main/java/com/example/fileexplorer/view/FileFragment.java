package com.example.fileexplorer.view;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.SearchManager;
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
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.example.fileexplorer.FileViewAdapter;
import com.example.fileexplorer.model.FileModel;
import com.example.fileexplorer.R;
import com.example.fileexplorer.model.SettingModel;
import com.example.fileexplorer.databinding.FragmentSecondBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {
    private FragmentSecondBinding binding;
    File storage;

    SettingModel settingModel;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sortType = preferences.getInt("sortType", 0);
        boolean ascending = preferences.getBoolean("ascending", true);
        settingModel = new SettingModel(sortType, ascending);

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

        binding.myToolBar.inflateMenu(R.menu.menu_secondary);
        binding.myToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
//                ActionProvider actionProvider = MenuItemCompat.getActionProvider(item);
                return true;
//                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
        });

        runtimePermission();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
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
        List<FileModel> files = new ArrayList<>();
        files.addAll(getFiles(storage));

        FileViewAdapter fileViewAdapter = new FileViewAdapter(getParentFragmentManager(), getContext(), files, settingModel);
        binding.setCustomAdapter(fileViewAdapter);
    }
}