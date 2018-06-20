package ru.mail.park.awesome_android;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {
    private List<String> ingredients;

    RecyclerAdapter(ArrayList<String> list) {
        this.ingredients = list;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        private final TextView text;
        private final Button removeButton;

        MyHolder(CardView card) {
            super(card);
            text = card.findViewById(R.id.text);
            removeButton = card.findViewById(R.id.remove_button);

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
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.text.setText(ingredients.get(position));
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredients.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), ingredients.size());
                setAnimation(view);
            }
        });

    }

    private void setAnimation(View viewToAnimate) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            animation.setDuration(R.integer.recycler_duration);
            viewToAnimate.startAnimation(animation);
    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
