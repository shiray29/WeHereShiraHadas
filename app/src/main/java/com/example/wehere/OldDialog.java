package com.example.wehere;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class OldDialog extends AppCompatDialogFragment {
    @NonNull
    private TextView textSure;
    private OldDialogListener listener;
    private boolean isSure;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popupsearch, null);
        builder.setView(view).setTitle("מצאת למי לעזור!").setNegativeButton("איני מעוניין", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSure = false;
                listener.isConfirmed(isSure);
            }
        }).setPositiveButton("אשמח לעזור!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSure = true;
                listener.isConfirmed(isSure);
            }
        });

        textSure = view.findViewById(R.id.textView_sure);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OldDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface OldDialogListener{
        void isConfirmed(boolean result);
    }
}
