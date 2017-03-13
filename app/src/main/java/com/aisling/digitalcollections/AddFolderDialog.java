package com.aisling.digitalcollections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * Created by Ais on 13/03/2017.
 */

public class AddFolderDialog extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        User u = WelcomeActivity.u;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.collection_dialog,null));
        builder.setTitle(R.string.dialog_add_folder)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //EditText edit = (EditText)dialog.findViewById();
                    }

                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                });
        return builder.create();

    }


}
