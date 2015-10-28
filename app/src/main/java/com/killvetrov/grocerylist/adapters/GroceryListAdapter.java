package com.killvetrov.grocerylist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.killvetrov.grocerylist.R;
import com.killvetrov.grocerylist.models.GroceryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Killvetrov on 02-Jul-15.
 */
public class GroceryListAdapter extends BaseAdapter {

    private ArrayList<GroceryModel> list;
    private float budget = 0f;

    private Context context;
    private LayoutInflater layoutInfl;

    private final static Comparator<GroceryModel> groceryNameComparator = new Comparator<GroceryModel>() {
        @Override
        public int compare(GroceryModel gm1, GroceryModel gm2) {
            return gm1.getName().compareToIgnoreCase(gm2.getName());
        }
    };

    private final static Comparator<GroceryModel> groceryTimestampComparator = new Comparator<GroceryModel>() {
        @Override
        public int compare(GroceryModel gm1, GroceryModel gm2) {
            return (int) (gm1.getId() - gm2.getId());
        }
    };

    private final static Comparator<GroceryModel> groceryCheckedComparator = new Comparator<GroceryModel>() {
        @Override
        public int compare(GroceryModel gm1, GroceryModel gm2) {
            if (gm1.isChecked() == gm2.isChecked())
                return 0;
            else if (!gm1.isChecked() && gm2.isChecked())
                return -1;
            else
                return 1;
        }
    };

    private static class ViewHolder {
        ImageView ivPic;
        TextView tvName;
        TextView tvQuantity;
        RelativeLayout flCheckedOverlay;
    }

    public GroceryListAdapter(Context c) {
        this(c, null);
    }

    public GroceryListAdapter(Context c, final ArrayList<GroceryModel> groceriesList) {
        this.context = c;

        if (groceriesList != null)
            list = (ArrayList<GroceryModel>) groceriesList.clone();
        else
            list = new ArrayList<GroceryModel>();

        this.layoutInfl = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        public JSONArray toJSONArray() {
            JSONArray jsonArr;
            jsonArr = new JSONArray();

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("budget", budget);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArr.put(jsonObj);

            for (int i = 0; i < list.size(); i++)
                jsonArr.put(list.get(i).toJSONObject());
            return jsonArr;
        }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public float calcRemainingBudget() {
        float remainder = budget;
        for (GroceryModel g : list) {
            if (g.isChecked()) remainder -= g.getCost();
        }

        return remainder;
    }

    public void addItem(final GroceryModel grocery) {
        list.add(new GroceryModel(grocery));
    }

    public void clear() {
        list.clear();
    }

    public int getCheckedItemCount() {
        int count = 0;

        for (int i = 0; i < list.size(); i++)
            if (list.get(i).isChecked()) count++;
        return count;
    }

    public void removeItemByIndex(int index) {
        list.remove(index);
    }

    public void sortByName() {
        Collections.sort(list, groceryNameComparator);
    }

    public void sortByTimestamp() {
        Collections.sort(list, groceryTimestampComparator);
    }

    public void sortByChecked() {
        Collections.sort(list, groceryCheckedComparator);
    }

    private String getGroceyQuantityAsText(GroceryModel grocery) {
        int unitResId;
        int formatResId;

        switch (grocery.getQtyUnit()) {
            case QUANTITY_KG:
                unitResId = R.string.unit_kilogram_short;
                formatResId = R.string.unit_format_float;
                break;
            case QUANTITY_LITRE:
                unitResId = R.string.unit_litre_short;
                formatResId = R.string.unit_format_float;
                break;
            case QUANTITY_PIECE:
                unitResId = R.string.unit_piece_short;
                formatResId = R.string.unit_format_integer;
                break;
            case QUANTITY_TEN:
                unitResId = R.string.unit_ten_short;
                formatResId = R.string.unit_format_integer;
                break;
            default:
                unitResId = R.string.unit_piece_short;
                formatResId = R.string.unit_format_integer;
        }

        return context.getString(formatResId, grocery.getQuantity(), context.getString(unitResId));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GroceryModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        GroceryModel grocery;

        if (convertView != null) {
            v = convertView;
        } else {
            v = layoutInfl.inflate(R.layout.item_list_groceries, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.ivPic = (ImageView) v.findViewById(R.id.iv_grocery_pic);
            holder.tvName = (TextView) v.findViewById(R.id.tv_grocery_name);
            holder.tvQuantity = (TextView) v.findViewById(R.id.tv_grocery_quantity);
            holder.flCheckedOverlay = (RelativeLayout) v.findViewById(R.id.fl_checked_overlay);
            v.setTag(holder);
        }

        grocery = list.get(position);
        holder.ivPic.setImageResource(grocery.getImageResId() != 0 ? grocery.getImageResId() : R.drawable.grocery_unknown);
        holder.tvName.setText(grocery.getName());
        holder.tvQuantity.setText(getGroceyQuantityAsText(grocery));
        holder.flCheckedOverlay.setVisibility(grocery.isChecked() ? View.VISIBLE : View.INVISIBLE);

        return v;
    }
}
