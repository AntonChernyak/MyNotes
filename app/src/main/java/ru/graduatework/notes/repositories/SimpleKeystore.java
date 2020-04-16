package ru.graduatework.notes.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import ru.graduatework.notes.R;

public class SimpleKeystore implements Keystore {

    private static final String PIN_SHARED_PREF_NAME = "PIN_Shared_Pref";
    private static final String PIN_KEY = "pinKey";

    private Context mContext;

    public SimpleKeystore(Context context) {
        this.mContext = context;
    }

    private SharedPreferences getSharedPref() {
        return mContext.getSharedPreferences(PIN_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean hashPin() {
        return false;
    }

    // Проверяем соответсвтвие введёноого текста паролю, сохранённому в памяти
    @Override
    public boolean checkPin(String enteredPin) {
        String pinCode = getSharedPref().getString(PIN_KEY, "");
        return enteredPin.equals(pinCode);
    }

    @Override
    public void saveNew(String pin) {

        if (pin.length() == 4) {
            getSharedPref().edit().putString(PIN_KEY, pin).apply();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.password_saved), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.enter_four_digits), Toast.LENGTH_LONG).show();
        }
    }

}