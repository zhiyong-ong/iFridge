package com.ifridgeTeam.ifridge.models;

/**
 * Created by Abel on 1/21/2017.
 */

public class FridgeItem {

    public String itemName;
    public int itemCount;

    public FridgeItem(String name, int itemCount) {
        this.itemName = name;
        this.itemCount = itemCount;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

}
