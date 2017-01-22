package com.ifridgeTeam.ifridge.network;

import com.ifridgeTeam.ifridge.models.FridgeServiceResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Abel on 1/21/2017.
 */

public class FridgeServiceBuilder {

    public final String BASE_URL = "";

    public Call<String> buildCall() {
        return null;
    }

    public interface FridgeService {
        @GET("")
        Call<FridgeServiceResponse> getResponse();
    }

}
