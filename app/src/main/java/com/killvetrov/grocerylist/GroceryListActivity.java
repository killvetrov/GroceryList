package com.killvetrov.grocerylist;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.killvetrov.grocerylist.adapters.GroceryListAdapter;
import com.killvetrov.grocerylist.data.PreferencesManager;
import com.killvetrov.grocerylist.models.GroceryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GroceryListActivity extends AppCompatActivity
        implements View.OnClickListener,
        View.OnTouchListener,
        AdapterView.OnItemClickListener,
        AddGroceryDialogFragment.AddGroceryDialogListener,
        AdapterView.OnItemLongClickListener {

    private Random rnd = new Random();

    private Button btnFloatPlus;
    private GridView grdvGroceries;
    private ImageView ivShadow;
    private ProgressBar pbarBudget;
    private TextView tvSum;
    private TextView tvBudget;
    private DrawerLayout drwlRoot;
    private ListView lvCategories;

    private GroceryListAdapter groceriesAdapter;

    private AddGroceryDialogFragment dlgAddGrocery;

    public static final int groceryResIdsArray[][] = {
            {
                    R.string.grocery_apples,
                    R.string.grocery_bananas,
                    R.string.grocery_beef,
                    R.string.grocery_bread,
                    R.string.grocery_butter,
                    R.string.grocery_icecream,
                    R.string.grocery_oil,
                    R.string.grocery_pears,
                    R.string.grocery_potatoes,
                    R.string.grocery_tomatoes,
                    R.string.grocery_eggs
            },
            {
                    R.drawable.apples,
                    R.drawable.bananas,
                    R.drawable.beef,
                    R.drawable.bread,
                    R.drawable.butter,
                    R.drawable.icecream,
                    R.drawable.oil,
                    R.drawable.pears,
                    R.drawable.potatoes,
                    R.drawable.tomatoes,
                    R.drawable.eggs
            }
    };

    private static final int NOTIF_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        btnFloatPlus = (Button) findViewById(R.id.btn_float_plus);
        grdvGroceries = (GridView) findViewById(R.id.gv_groceries);
        ivShadow = (ImageView) findViewById(R.id.iv_shadow);
        pbarBudget = (ProgressBar) findViewById(R.id.pbar_budget_used);
        tvSum = (TextView) findViewById(R.id.tv_sum);
        tvBudget = (TextView) findViewById(R.id.tv_budget);

        drwlRoot = (DrawerLayout) findViewById(R.id.drwl_root);
        lvCategories = (ListView) findViewById(R.id.lv_categories);

        btnFloatPlus.setClickable(true);
        btnFloatPlus.setOnClickListener(this);
        btnFloatPlus.setOnTouchListener(this);
        ivShadow.setOnClickListener(this);


        groceriesAdapter = new GroceryListAdapter(this);

        if (PreferencesManager.readText(this).isEmpty() || !loadGroceriesDataFromPrefs())
            randomizeGroceryList();

        grdvGroceries.setAdapter(groceriesAdapter);
        grdvGroceries.setOnItemClickListener(this);
        grdvGroceries.setOnItemLongClickListener(this);

        //dlgAddGrocery = new AddGroceryDialogFragment();

        //ArrayList<String> arrStr = new ArrayList<>();
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        categoriesAdapter.add("Все товары");
        categoriesAdapter.add("С высоким приоритетом");
        categoriesAdapter.add("Мясо");
        categoriesAdapter.add("Напитки");
        categoriesAdapter.add("Овощи и фрукты");
        categoriesAdapter.add("Выпечка");

        lvCategories.setAdapter(categoriesAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGroceriesDataToPrefs();

        Intent i = new Intent(this, GroceryListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder nb = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_status_text,
                        groceriesAdapter.getCheckedItemCount(), groceriesAdapter.getCount()))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi);

        Notification n = nb.build();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, n);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIF_ID);
    }

    private void randomizeGroceryList() {
        if (groceriesAdapter == null) return;

        int count = groceryResIdsArray[0].length; //7 + rnd.nextInt(14);
        boolean isIndexUsed[] = new boolean[count];
        for (int i = 0; i < count; i++) isIndexUsed[i] = false;

        int rndGroceryIndex;
        GroceryModel.QuantityUnit qu;

        groceriesAdapter.clear();

        for (int i = 0; i < count; i++) {
            while (isIndexUsed[rndGroceryIndex = rnd.nextInt(groceryResIdsArray[0].length)]);
            isIndexUsed[rndGroceryIndex] = true;

            switch (groceryResIdsArray[0][rndGroceryIndex]) {
                case R.string.grocery_eggs:
                    qu = GroceryModel.QuantityUnit.QUANTITY_TEN;
                    break;
                case R.string.grocery_icecream:
                case R.string.grocery_bread:
                    qu = GroceryModel.QuantityUnit.QUANTITY_PIECE;
                    break;
                case R.string.grocery_oil:
                    qu = GroceryModel.QuantityUnit.QUANTITY_LITRE;
                    break;
                default:
                    qu = GroceryModel.QuantityUnit.QUANTITY_KG;
            }

            groceriesAdapter.addItem(new GroceryModel(groceryResIdsArray[1][rndGroceryIndex],
                    getString(groceryResIdsArray[0][rndGroceryIndex]),
                    (2 + rnd.nextInt(7)) + (rnd.nextInt(2) * 0.50f),
                    qu,
                    (2 + rnd.nextInt(55)) * 0.50f));
        }

        groceriesAdapter.notifyDataSetChanged();
        updateTitle();

    }

    private void updateTitle() {
        getSupportActionBar().setTitle(getString(R.string.app_name) + " (" + groceriesAdapter.getCheckedItemCount() + "/" + groceriesAdapter.getCount() + ")");

        tvSum.setText(getString(R.string.text_budget, groceriesAdapter.getBudget() - groceriesAdapter.calcRemainingBudget()));
        tvBudget.setText(String.valueOf(groceriesAdapter.getBudget()));

        if (groceriesAdapter.calcRemainingBudget() < 0f)
            pbarBudget.setProgress(100);
        else if (groceriesAdapter.getBudget() > 0f)
            pbarBudget.setProgress((int) ((groceriesAdapter.getBudget() - groceriesAdapter.calcRemainingBudget()) / groceriesAdapter.getBudget() * 100));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grocery_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                groceriesAdapter.sortByName();
                groceriesAdapter.notifyDataSetChanged();
                break;

            case R.id.action_sort_by_timestamp:
                groceriesAdapter.sortByTimestamp();
                groceriesAdapter.notifyDataSetChanged();
                break;

            case R.id.action_sort_by_checked:
                groceriesAdapter.sortByChecked();
                groceriesAdapter.notifyDataSetChanged();
                break;

            case R.id.action_clear:
                groceriesAdapter.clear();
                groceriesAdapter.notifyDataSetChanged();
                updateTitle();
                break;

            case R.id.action_randomize:
                randomizeGroceryList();
                break;

            case R.id.action_save:
                saveGroceriesDataToPrefs();
                break;

            case R.id.action_load:
                loadGroceriesDataFromPrefs();
                break;

            case R.id.action_set_budget:
                LayoutInflater li = getLayoutInflater();
                View budgetView = li.inflate(R.layout.dialog_budget, null);
                final EditText edtBudget = (EditText) budgetView.findViewById(R.id.edt_budget);
                if (groceriesAdapter.getBudget() > 0f)
                    edtBudget.setText(String.valueOf(groceriesAdapter.getBudget()));
                AlertDialog.Builder dlgb = new AlertDialog.Builder(this)
                        .setView(budgetView)
                        .setPositiveButton("Задать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groceriesAdapter.setBudget(Float.valueOf(edtBudget.getText().toString()));
                                updateTitle();
                            }
                        })
                        .setNegativeButton(R.string.dialog_add_btn_cancel, null);
                dlgb.create().show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveGroceriesDataToPrefs() {
        PreferencesManager.saveText(this, groceriesAdapter.toJSONArray().toString());
    }

    private boolean loadGroceriesDataFromPrefs() {
        boolean result = true;
        groceriesAdapter.clear();
        try {
            JSONArray jsonArr = new JSONArray(PreferencesManager.readText(this));
            //Toast.makeText(this, jsonArr.toString(), Toast.LENGTH_SHORT).show();

            JSONObject jsonObj1 = jsonArr.getJSONObject(0);
            groceriesAdapter.setBudget((float) jsonObj1.optDouble("budget", 0f));

            for (int i = 1; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                GroceryModel grocery = new GroceryModel();

                for (Field f : grocery.getClass().getDeclaredFields()) {
                    if ( (f.getModifiers() & Modifier.PROTECTED) == 0 ) continue;
                    f.setAccessible(true);
                    if (f.getType().equals(Integer.TYPE)) {
                        Log.d("mytag", "parsing Integer " + jsonObj.getInt(f.getName()) + " for field " + f.getName());
                        f.setInt(grocery, jsonObj.getInt(f.getName()));
                    } else if (f.getType().equals(Long.TYPE)) {
                        Log.d("mytag", "parsing Long " + jsonObj.getLong(f.getName()) + " for field " + f.getName());
                        f.setLong(grocery, jsonObj.getLong(f.getName()));
                    } else if (f.getType().equals(Float.TYPE)) {
                        Log.d("mytag", "parsing Float " + jsonObj.getDouble(f.getName()) + " for field " + f.getName());
                        f.setFloat(grocery, (float) jsonObj.getDouble(f.getName()));
                    } else if (f.getType().equals(Boolean.TYPE)) {
                        Log.d("mytag", "parsing Boolean " + jsonObj.getBoolean(f.getName()) + " for field " + f.getName());
                        f.setBoolean(grocery, jsonObj.getBoolean(f.getName()));
                    } else if (f.getType().equals(GroceryModel.QuantityUnit.class)) {
                        Log.d("mytag", "parsing QtyUnit " + jsonObj.get(f.getName()).toString() + " for field " + f.getName());
                        f.set(grocery,(GroceryModel.QuantityUnit.valueOf(jsonObj.getString(f.getName()))));
                    } else {
                        Log.d("mytag", "actual type: " + f.getType().getName() + " parsing String " + jsonObj.get(f.getName()).toString() + " for field " + f.getName());
                        f.set(grocery, jsonObj.getString(f.getName()));
                    }

                }
//                        grocery.setImageResId(jsonObj.getInt("imageResId"));
//                        grocery.setName(jsonObj.getString("name"));
//                        grocery.setQuantity((float) jsonObj.getDouble("quantity"));
//                        grocery.setQtyUnit((GroceryModel.QuantityUnit.valueOf(jsonObj.getString("qtyUnit"))));
//                        grocery.setId(jsonObj.getInt("id"));
//                        grocery.setTimestamp(jsonObj.getLong("timestamp"));
//                        grocery.setChecked(jsonObj.getBoolean("checked"));

                groceriesAdapter.addItem(grocery);
            }

        } catch (JSONException e) {
            result = false;
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            result = false;
            e.printStackTrace();
        }

        groceriesAdapter.notifyDataSetChanged();
        updateTitle();
        return result;
    }

    @Override
    public void onClick(View v) {


        Toast.makeText(this, "OnClick", Toast.LENGTH_SHORT).show();

        switch (v.getId()) {

            case R.id.btn_float_plus:
                dlgAddGrocery = new AddGroceryDialogFragment();
                dlgAddGrocery.show(getFragmentManager(), "Dlg1");
                Toast.makeText(this, "Starting dlg", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.btn_float_plus:
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    btnFloatPlus.setBackgroundResource(R.drawable.round_button_pressed);
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    dlgAddGrocery = new AddGroceryDialogFragment();
                    dlgAddGrocery.show(getFragmentManager(), "Dlg1");
                    btnFloatPlus.setBackgroundResource(R.drawable.round_button);
                }

                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.gv_groceries) {
            groceriesAdapter.getItem(position).toggleChecked();
            groceriesAdapter.notifyDataSetChanged();
            updateTitle();
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText edtName;
        EditText edtQuantity;
        RadioGroup rgrUnit;

        edtName = (EditText) dialog.getDialog().findViewById(R.id.edt_grocery_name);
        edtQuantity = (EditText) dialog.getDialog().findViewById(R.id.edt_grocery_quantity);
        rgrUnit = (RadioGroup) dialog.getDialog().findViewById(R.id.rg_quantity_unit);

        if (edtName.getText().toString().isEmpty() || Float.valueOf(edtQuantity.getText().toString()) == 0) return;

        GroceryModel.QuantityUnit qu;
        switch (rgrUnit.getCheckedRadioButtonId()) {
            case R.id.rbtn_kg:
                qu = GroceryModel.QuantityUnit.QUANTITY_KG;
                break;
            case R.id.rbtn_litre:
                qu = GroceryModel.QuantityUnit.QUANTITY_LITRE;
                break;
            case R.id.rbtn_piece:
                qu = GroceryModel.QuantityUnit.QUANTITY_PIECE;
                break;
            case R.id.rbtn_ten:
                qu = GroceryModel.QuantityUnit.QUANTITY_TEN;
                break;
            default:
                qu = GroceryModel.QuantityUnit.QUANTITY_KG;
        }

        groceriesAdapter.addItem(new GroceryModel(0, edtName.getText().toString(), Float.valueOf(edtQuantity.getText().toString()), qu, GroceryModel.PRICE_UNKNOWN));
        groceriesAdapter.notifyDataSetChanged();
        updateTitle();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder dlgbuilder = new AlertDialog.Builder(this);
        dlgbuilder.setItems(R.array.context_grocery_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    groceriesAdapter.removeItemByIndex(position);
                    groceriesAdapter.notifyDataSetChanged();
                    updateTitle();
                }
            }
        });

        dlgbuilder.create().show();

        return true;
    }
}
