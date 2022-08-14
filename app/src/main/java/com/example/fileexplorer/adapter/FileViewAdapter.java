package com.example.fileexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.databinding.FileItemBinding;
import com.example.fileexplorer.databinding.FileHeaderBinding;
import com.example.fileexplorer.enums.SortType;
import com.example.fileexplorer.view.FileFragment;
import com.example.fileexplorer.model.FileModel;
import com.example.fileexplorer.model.SettingModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CustomClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<FileModel> fileModelList;
    private SettingModel settingModel;
    private Context context;
    private FragmentManager fragmentManager;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public FileItemBinding binding;

        public ItemViewHolder(FileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public FileHeaderBinding binding;

        public HeaderViewHolder(FileHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void itemClicked(FileModel f) {
        if (f.getType() == 1)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("path", f.getPath());
        FileFragment fileFragment = new FileFragment();
        fileFragment.setArguments(bundle);

        String tag = fileFragment.getClass().getName();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, fileFragment, tag).addToBackStack(tag).commit();
    }

    @Override
    public void headerClicked(View view) {
        //Do something
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                settingModel.setSortType(item.getOrder());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putInt("sortType", item.getOrder());
                prefEditor.commit();

                sort(settingModel.getSortType(), settingModel.isAscending());
                notifyDataSetChanged();
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public void orderClicked(SettingModel sett) {
        sett.setAscending(!sett.isAscending());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean("ascending", sett.isAscending());
        prefEditor.commit();

        sort(settingModel.getSortType(), settingModel.isAscending());
        notifyDataSetChanged();
    }

    public FileViewAdapter(FragmentManager fragmentManager, Context context, List<FileModel> fileModelList, SettingModel settingModel) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.fileModelList = fileModelList;
        this.settingModel = settingModel;
        sort(this.settingModel.getSortType(), settingModel.isAscending());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                FileHeaderBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_header, parent, false);
                return new HeaderViewHolder(headerBinding);
            case TYPE_ITEM:
                FileItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_item, parent, false);
                return new ItemViewHolder(itemBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            FileModel fileModel = fileModelList.get(position - 1);
            ((ItemViewHolder) holder).binding.setModel(fileModel);
            ((ItemViewHolder) holder).binding.setItemClickListener(this);
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).binding.setModel(settingModel);
            ((HeaderViewHolder) holder).binding.setItemClickListener(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (fileModelList.size() == 0)
            return 0;
        return fileModelList.size() + 1;
    }

    public void sort(int sortType, boolean ascending) {
        Collections.sort(fileModelList, new Comparator<FileModel>() {
            public int compare(FileModel obj1, FileModel obj2) {
                int result = 0;
                switch (SortType.valueOf(sortType)) {
                    // ## Ascending order
                    case NAME:
                        result = obj1.getFileName().compareToIgnoreCase(obj2.getFileName());
                        break;
                    case DATE:
                        result = Long.valueOf(obj1.getDateModified()).compareTo(Long.valueOf(obj2.getDateModified()));
                        break;
                    case TYPE:
                        result = Integer.valueOf(obj1.getType()).compareTo(Integer.valueOf(obj2.getType()));
                        break;
                    case SIZE:
                        result = Long.valueOf(obj1.getSize()).compareTo(Long.valueOf(obj2.getSize()));
                        break;
                }
                if (!ascending) {
                    result = result *(-1);
                }
                return result;
            }
        });
    }
}
