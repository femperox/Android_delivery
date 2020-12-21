package com.example.gui_android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gui_android.R;

public class DialogClass extends DialogFragment  {
    private EditText etUsername;
    private EditText etPassword;
    DialogClassListener listener;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        etUsername = view.findViewById(R.id.edt_username);
        etPassword = view.findViewById(R.id.edt_password);

        builder.setView(view)
                .setTitle("Введите ваши данные")
                .setNegativeButton("Отмена", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {                    }})
                .setPositiveButton("Войти", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    { String username = etUsername.getText().toString();
                      String password = etPassword.getText().toString();

                      boolean result;
                      if ( (username.equals("123")) && (password.equals("123")))  result = true;
                      else result = false;

                      listener.getTextFromDialog(result,username);


                    }
                });



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogClassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ "надо импелментировать класс слушателя");
        }
    }

    public interface DialogClassListener
    { void getTextFromDialog(boolean result, String username);

    }



}
