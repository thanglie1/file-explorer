package com.example.fileexplorer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.databinding.FileContainerBinding;
import com.example.fileexplorer.fragments.SecondFragment;

import java.util.List;

public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.ViewHolder> implements CustomClickListener {
    private List<FileModel> fileModelList;
    private  Context context;
    private FragmentManager fragmentManager;

    @Override
    public void cardClicked(FileModel f) {
        if (f.getType() == 1)
             return;
        Bundle bundle = new Bundle();
        bundle.putString("path", f.getPath());
        SecondFragment secondFragment = new SecondFragment();
        secondFragment.setArguments(bundle);

        String tag = secondFragment.getClass().getName();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, secondFragment,tag).addToBackStack(tag).commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public FileContainerBinding binding;

        public ViewHolder(FileContainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public FileViewAdapter(FragmentManager fragmentManager, Context context, List<FileModel> fileModelList) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.fileModelList = fileModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileContainerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_container, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileModel fileModel = fileModelList.get(position);
        holder.binding.setModel(fileModel);
        holder.binding.setItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return fileModelList.size();
    }
}
