package com.ifridgeTeam.ifridge.network;

import com.ifridgeTeam.ifridge.models.RecipeResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Abel on 1/22/2017.
 */

public class RecipeNetworkHelper {

    private static final String BASE_URL = "http://food2fork.com/api/search/";

    public static Call<RecipeResponse> buildCall(String[] ingredientNames) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RecipeService service = retrofit.create(RecipeService.class);
        Map<String, String> params = getParams(ingredientNames);
        return service.getRecipes(params);
    }

    private static Map<String, String> getParams(String[] ingredientNames) {
        Map<String, String> params = new HashMap<>();
        String q = "";
        for (String ingredientName : ingredientNames) {
            q = q + ingredientName + ",";
        }

        params.put("q", q);
        params.put("key", "505c904cc6a11d2597eb45fde9cf4967");
        return params;
    }

    interface RecipeService {
        @GET("./")
        Call<RecipeResponse> getRecipes(@QueryMap Map<String, String> options);
    }
}
