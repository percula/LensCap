package com.ownzordage.chrx.lenscap;

/**
 * CRepresents an InAppPurchaseItem
 */

public class InAppPurchaseItem {
    private String sku;
    private String price;

    public InAppPurchaseItem(String sku, String price) {
        this.sku = sku;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
