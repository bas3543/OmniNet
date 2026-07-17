package com.omninet.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.omninet.R;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Status;
import java.util.List;

public class StatusFragment extends Fragment {
    private RecyclerView statusRecyclerView;
    private StatusAdapter adapter;
    private FloatingActionButton fabAddStatus;
    private OmniDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusRecyclerView = view.findViewById(R.id.statusRecyclerView);
        fabAddStatus = view.findViewById(R.id.fabAddStatus);
        database = OmniDatabase.getInstance(requireContext());

        setupRecyclerView();
        loadStatuses();

        fabAddStatus.setOnClickListener(v -> addNewStatus());
    }

    private void setupRecyclerView() {
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StatusAdapter(requireContext());
        statusRecyclerView.setAdapter(adapter);
    }

    private void loadStatuses() {
        new Thread(() -> {
            List<Status> statuses = database.statusDao().getValidStatuses(System.currentTimeMillis());
            requireActivity().runOnUiThread(() -> adapter.setStatuses(statuses));
        }).start();
    }

    private void addNewStatus() {
    }
}
