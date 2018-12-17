package keonheelee.github.io.searchmovie.viewmodel.components;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import keonheelee.github.io.searchmovie.databinding.ListComponentBinding;


public class MovieViewHolder extends RecyclerView.ViewHolder{
    public ListComponentBinding binding;

    public MovieViewHolder(View itemView){
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}

