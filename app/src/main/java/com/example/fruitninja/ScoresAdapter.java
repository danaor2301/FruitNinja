package com.example.fruitninja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    private ArrayList<User> scoresList;

    public ScoresAdapter(ArrayList<User> scoresList) {
        this.scoresList = scoresList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = scoresList.get(position);
        holder.tvUser.setText(user.getName());
        holder.tvPlace.setText(user.getPlace() + "");
        holder.tvScore.setText(user.getBestScore() + "");
    }

    @Override
    public int getItemCount() {
        return scoresList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlace, tvUser, tvScore;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}
