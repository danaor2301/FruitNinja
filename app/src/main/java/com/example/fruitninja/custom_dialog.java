package com.example.fruitninja;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

public class custom_dialog extends AppCompatDialogFragment {

    Custom_DialogInterFace dialogInterFace;
    EditText etNewName;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_changename, null);
        builder.setView(view)
                .setTitle("Choose New Username")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = etNewName.getText().toString();
                        dialogInterFace.applyText(newName);
                    }
                });

        etNewName = view.findViewById(R.id.etNewName);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        dialogInterFace = (Custom_DialogInterFace)context;
    }

    public interface Custom_DialogInterFace
    {
        void applyText(String newName);
    }
}
