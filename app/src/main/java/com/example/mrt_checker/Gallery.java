package com.example.mrt_checker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private RecyclerView foldersRecyclerView;
    private FolderAdapter folderAdapter;
    private List<String> folderNames = Arrays.asList("Glioma", "Meningioma", "No Tumor", "Pituitary");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        foldersRecyclerView = findViewById(R.id.foldersRecyclerView);
        foldersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        folderAdapter = new FolderAdapter(this, folderNames);
        foldersRecyclerView.setAdapter(folderAdapter);
    }
}