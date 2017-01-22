package com.ifridgeTeam.ifridge.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abel on 1/21/2017.
 */

public class FridgeServiceResponse {

    @SerializedName("FridgeItems")
    private List<FridgeItem> fridgeItems;

    public List<FridgeItem> getFridgeItems() {
        return fridgeItems;
    }
}
