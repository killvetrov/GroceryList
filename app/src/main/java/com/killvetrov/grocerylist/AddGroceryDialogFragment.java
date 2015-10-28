package com.killvetrov.grocerylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.killvetrov.grocerylist.models.GroceryModel;

/**
 * Created by Killvetrov on 06-Jul-15.
 */
public class AddGroceryDialogFragment extends DialogFragment {

    private EditText edtName;
    private EditText edtQuantity;

    public interface AddGroceryDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    private AddGroceryDialogListener myListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_grocery, null))
                .setTitle(R.string.dialog_add_title)
                .setPositiveButton(R.string.dialog_add_btn_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myListener.onDialogPositiveClick(AddGroceryDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.dialog_add_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dlg = builder.create();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.unit_kilogram_short));
        adapter.add(getString(R.string.unit_litre_short));
        adapter.add(getString(R.string.unit_piece_short));
        adapter.add(getString(R.string.unit_ten_short));

        ((Spinner) dlg.findViewById(R.id.unit_spinner)).setAdapter(adapter);

        return dlg;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myListener = (AddGroceryDialogListener) activity;
    }
}
