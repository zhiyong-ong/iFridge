package com.ifridgeTeam.ifridge;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ifridgeTeam.ifridge.adapters.FridgeItemAdapter;
import com.ifridgeTeam.ifridge.adapters.RecipeItemAdapter;
import com.ifridgeTeam.ifridge.models.FridgeItem;
import com.ifridgeTeam.ifridge.models.FridgeServiceResponse;
import com.ifridgeTeam.ifridge.models.Recipe;
import com.ifridgeTeam.ifridge.models.RecipeResponse;
import com.ifridgeTeam.ifridge.network.FridgeNetworkHelper;
import com.ifridgeTeam.ifridge.network.RecipeNetworkHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "IFRIDGE";

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_itemList)
    RecyclerView itemListRv;

    @BindView(R.id.rv_recipes)
    RecyclerView recipesListRv;

    @BindView(R.id.bottom_sheet)
    RelativeLayout bottomSheet;

    @BindView(R.id.overlay_bottom_sheet)
    FrameLayout overlayBottom;

    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressbar_bottom_sheet)
    ProgressBar progressBottom;

    @BindString(R.string.placeholder_text_item_name)
    String placeholderItemName;

    @BindString(R.string.placeholder_text_item_count)
    String placeholderItemCount;

    @BindColor(R.color.colorPrimaryDark)
    int primaryColorDark;

    @BindColor(R.color.refresh1)
    int refresh1;

    @BindColor(R.color.refresh2)
    int refresh2;

    @BindColor(R.color.refresh3)
    int refresh3;

    private BottomSheetBehavior mBottomSheetBehavior;

    // Fridge Item
    private RecyclerView.Adapter mItemAdapter;
    private List<FridgeItem> mFridgeItems;
    // Recipe item
    private RecyclerView.Adapter mRecipeAdapter;
    private List<Recipe> mRecipes;

    private FridgeNetworkHelper mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupToolbar();
        getNewFridgeClient();
        setupItemRv();
        setupBottomSheet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshItemList();
                swipeRefreshLayout.setRefreshing(true);
                return true;
            default:
                return false;
        }

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupItemRv() {
        mFridgeItems = getItemList();
        mItemAdapter = new FridgeItemAdapter(this, mFridgeItems);
        itemListRv.setAdapter(mItemAdapter);
        itemListRv.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setColorSchemeColors(refresh1, refresh2, refresh3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItemList();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        refreshItemList();
    }

    private void refreshItemList() {
        Log.d(LOG_TAG, "refreshing normally");
        mClient.execute();
    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mBottomSheetBehavior.getState()) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    default:
                        break;
                }

            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        overlayBottom.setVisibility(View.VISIBLE);
                        progressBottom.setVisibility(View.VISIBLE);
                        findRecipes();
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.BLACK);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(primaryColorDark);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mRecipes = new ArrayList<>();
        mRecipeAdapter = new RecipeItemAdapter(this, mRecipes);
        recipesListRv.setAdapter(mRecipeAdapter);
        recipesListRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void findRecipes() {
        Call<RecipeResponse> call = RecipeNetworkHelper.buildCall(getIngredientNames());
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (!response.isSuccessful() || response.body() == null){
                    return;
                }
                RecipeResponse recipeResponse = response.body();
                updateRecipeList(recipeResponse.getRecipes());
                overlayBottom.setVisibility(View.GONE);
                progressBottom.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {

            }
        });
    }

    private String[] getIngredientNames() {
        String[] result = new String[mFridgeItems.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = mFridgeItems.get(i).getItemName();
        }

        return result;
    }

    private void getNewFridgeClient() {
        mClient = new FridgeNetworkHelper(new FridgeNetworkHelper.FridgeOnResponseListener() {
            @Override
            public void onResponse(String responseString) {
                if (responseString == null) {
                    Toast.makeText(MainActivity.this, "Connectivity issues", Toast.LENGTH_SHORT).show();
                } else {
                    FridgeServiceResponse response = deserialize(responseString);
                    updateItemList(response.getFridgeItems());
                }

                getNewFridgeClient();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private FridgeServiceResponse deserialize(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        FridgeServiceResponse response = gson.fromJson(json,
                FridgeServiceResponse.class);
        return response;
    }

    private void updateRecipeList(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
        mRecipeAdapter.notifyDataSetChanged();
    }

    private void updateItemList(List<FridgeItem> fridgeItems) {
        mFridgeItems.clear();
        mFridgeItems.addAll(fridgeItems);
        mItemAdapter.notifyDataSetChanged();
    }

    private List<FridgeItem> getItemList() {
        List<FridgeItem> list = new ArrayList<>();
        list.add(new FridgeItem(placeholderItemName,
                                Integer.valueOf(placeholderItemCount)));
        list.add(new FridgeItem("Orange", 1));
        return list;
    }

}
