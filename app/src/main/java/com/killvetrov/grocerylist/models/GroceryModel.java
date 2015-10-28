package com.killvetrov.grocerylist.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Killvetrov on 01-Jul-15.
 */
public class GroceryModel {

    public enum QuantityUnit {
        QUANTITY_KG,
        QUANTITY_LITRE,
        QUANTITY_PIECE,
        QUANTITY_TEN
    }

    public static float PRICE_UNKNOWN = -0.99f;

    private static int instanceCounter = 0;

    protected int id;
    protected int imageResId;
    protected String name;
    protected float quantity;
    protected float price;
    protected QuantityUnit qtyUnit;
    protected long timestamp;
    protected boolean checked;

    public GroceryModel() {
        this(0, null, 0f);
    };

    public GroceryModel(int imageResId, String name, float quantity) {
        this(imageResId, name, quantity, QuantityUnit.QUANTITY_PIECE, PRICE_UNKNOWN);
    }

    public GroceryModel(GroceryModel gm) {
        this.id = gm.getId();
        this.imageResId = gm.getImageResId();
        this.name = gm.getName();
        this.quantity = gm.getQuantity();
        this.qtyUnit = gm.getQtyUnit();
        this.price = gm.getPrice();
        this.timestamp = gm.getTimestamp();
        this.checked = gm.isChecked();
    }

    public GroceryModel(int imageResId, String name, float quantity, QuantityUnit qtyUnit, float price) {
        instanceCounter++;
        this.id = instanceCounter;
        this.imageResId = imageResId;
        this.name = name;
        this.quantity = quantity;
        this.qtyUnit = qtyUnit;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
        this.checked = false;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject();

            for (Field f : this.getClass().getDeclaredFields()) {
                if ( (f.getModifiers() & Modifier.PROTECTED) == 0 ) continue;
                jsonObj.put(f.getName(), f.get(this).toString());
            }

            Log.d("mytag", jsonObj.toString());

//            jsonObj.put("id", this.id);
//            jsonObj.put("imageResId", this.imageResId);
//            jsonObj.put("name", this.name);
//            jsonObj.put("quantity", (double) this.quantity);
//            jsonObj.put("qtyUnit", this.qtyUnit);
//            jsonObj.put("price", this.price);
//            jsonObj.put("timestamp", this.timestamp);
//            jsonObj.put("checked", this.checked);
        } catch (JSONException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

        return jsonObj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public QuantityUnit getQtyUnit() {
        return qtyUnit;
    }

    public void setQtyUnit(QuantityUnit qtyUnit) {
        this.qtyUnit = qtyUnit;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCost() {
        return this.price * this.quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        this.checked = !this.checked;
    }
}
