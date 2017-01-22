package com.ifridgeTeam.ifridge.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifridgeTeam.ifridge.R;
import com.ifridgeTeam.ifridge.misc.ScrimTransformation;
import com.ifridgeTeam.ifridge.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abel on 1/21/2017.
 */

public class RecipeItemAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Recipe> mRecipes;

    public RecipeItemAdapter(Context context, List<Recipe> recipes) {
        mContext = context;
        mRecipes = recipes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder itemViewHolder = (RecipeViewHolder) holder;

        // Get corresponding data
        Recipe recipe = mRecipes.get(position);
        String recipeName = recipe.getTitle();
        final String sourceUrl = recipe.getSourceUrl();
        final String recipeImageUrl = recipe.getImageUrl();

        itemViewHolder.recipeNameText.setText(recipeName);
//        itemViewHolder.recipePublisherText.setText("Recipe from: " + recipePublisher);

        Picasso.with(mContext)
                .load(recipeImageUrl)
                .fit()
                .centerCrop()
                .transform(new ScrimTransformation(mContext, itemViewHolder.recipeImage))
                .into(itemViewHolder.recipeImage);

        itemViewHolder.recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(sourceUrl)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_recipe_image)
        ImageView recipeImage;

        @BindView(R.id.item_recipe_name)
        TextView recipeNameText;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
