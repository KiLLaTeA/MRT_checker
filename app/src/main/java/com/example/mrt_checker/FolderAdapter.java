package com.example.mrt_checker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context context;
    private List<String> folderNames;

    public FolderAdapter(Context context, List<String> folderNames) {
        this.context = context;
        this.folderNames = folderNames;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        String folderName = folderNames.get(position);
        holder.folderName.setText(folderName);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GalleryActivity.class);
            intent.putExtra("folderName", folderName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderNames.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
        }
    }
}