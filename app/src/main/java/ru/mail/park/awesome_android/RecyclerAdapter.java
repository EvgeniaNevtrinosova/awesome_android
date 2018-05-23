package ru.mail.park.awesome_android;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {
    private List<String> list;
    private OnItemClickListener listener;

    RecyclerAdapter(ArrayList<String> list) {
        this.list = list;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        private final CardView card;
        private final TextView text;
        private final Button removeButton;

        MyHolder(CardView card) {
            super(card);
            text = card.findViewById(R.id.text);
            removeButton = card.findViewById(R.id.remove_button);
            this.card = card;
        }

        public TextView getText() {
            return text;
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_ingredients, parent, false);
        return new MyHolder(card);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        holder.text.setText(list.get(position));
        holder.card.setOnClickListener(new CardView.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(holder.card, holder);
                }
            }
        });
    }


    interface OnItemClickListener {
        void onClick(CardView view, MyHolder holder);
    }

    void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
