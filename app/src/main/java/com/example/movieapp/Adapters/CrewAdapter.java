package com.example.movieapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import models.FilmCrew;

import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {
    private List<FilmCrew> filmCrews;

    public CrewAdapter(List<FilmCrew> filmCrews) {
        this.filmCrews = filmCrews;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        FilmCrew filmCrew = filmCrews.get(position);
        holder.name.setText(filmCrew.getName());
        holder.role.setText(filmCrew.getRole());
        Glide.with(holder.itemView.getContext()).load(filmCrew.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return filmCrews.size();
    }

    public static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, role;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.crewImage);
            name = itemView.findViewById(R.id.crewName);
            role = itemView.findViewById(R.id.crewRole);
        }
    }
}
