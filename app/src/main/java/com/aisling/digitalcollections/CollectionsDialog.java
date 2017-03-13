package com.aisling.digitalcollections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.ArrayList;

/**
 * Created by Ais on 13/02/2017.
 */

public class CollectionsDialog extends DialogFragment {

    private ArrayList<Integer> mSelectedItems = new ArrayList();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final User u = WelcomeActivity.u;
        final ArrayList<Folder> f = u.folders;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.collection_dialog,null));
        if(!u.folders.isEmpty()){
            builder.setTitle(R.string.dialog_add)
                    .setMultiChoiceItems(u.getFolderNames(),null, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked)
                            {
                                mSelectedItems.add(which);
                            }

                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int id){
                            clear(u.selectedFolders);
                            for(int i : mSelectedItems)
                            {
                                u.selectedFolders[i] = true;
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    });
        }

        // Create the AlertDialog object and return it
        return builder.create();

    }

    public void clear(boolean[] array)
    {
        for(int i=0;i<array.length;i++)
        {
            array[i] = false;
        }
    }

}