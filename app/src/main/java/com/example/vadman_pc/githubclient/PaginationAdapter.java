package com.example.vadman_pc.githubclient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vadman_pc.githubclient.controller.DetailActivity;
import com.example.vadman_pc.githubclient.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PaginationAdapter.class.getSimpleName();
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Item> items;
    private Context context;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        Log.d(TAG, "createViewHolder");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                Log.d(TAG, "createViewHolder ITEM");
                viewHolder = getViewHolder(viewGroup, inflater);
                break;

            case LOADING:
                Log.d(TAG, "createViewHolder LOADING");
                View v2 = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new LoadingViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.row_user, parent, false);
        viewHolder = new FilledViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final FilledViewHolder filledViewHolder = (FilledViewHolder) holder;

                Log.d(TAG, "puting DAta Inside");
                filledViewHolder.title.setText(item.getLogin());
                filledViewHolder.gitHubLink.setText(item.getHtmlUrl());

                Log.d(TAG, "Picasso");
                Picasso.with(context)
                        .load(item.getAvatarUrl())
                        .placeholder(R.drawable.load)
                        .into(filledViewHolder.imageView);

                break;

            case LOADING:
//                Do nothing
                break;

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Item i) {
        items.add(i);
        notifyItemInserted(items.size() - 1);
    }


    public void addAll(List<Item> moveResults) {
        for (Item item : moveResults) {
            add(item);
        }
    }


    public void remove(Item r) {
        int position = items.indexOf(r);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Item());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = items.size() - 1;
        Item result = getItem(position);

        if (result != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void setFilter(List<Item> newList) {
        items = new ArrayList<>();
        items.addAll(newList);

        notifyDataSetChanged();
    }

    private class FilledViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView gitHubLink;
        private ImageView imageView;

        public FilledViewHolder(View view) {
            super(view);
            title = (TextView) itemView.findViewById(R.id.title);
            gitHubLink = (TextView) itemView.findViewById(R.id.gitHub_link1);
            imageView = (ImageView) itemView.findViewById(R.id.cover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Item clikedDataItem = items.get(pos);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(DetailActivity.LOGIN_KEY, items.get(pos).getLogin());
                        intent.putExtra(DetailActivity.HTML_URL_KEY, items.get(pos).getHtmlUrl());
                        intent.putExtra(DetailActivity.AVATAR_URL_KEY, items.get(pos).getAvatarUrl());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent);

                        Toast.makeText(view.getContext(), "You clicked " + clikedDataItem.getLogin(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
            Log.d(TAG, "inside LoadingViewHolder ");
        }
    }


}