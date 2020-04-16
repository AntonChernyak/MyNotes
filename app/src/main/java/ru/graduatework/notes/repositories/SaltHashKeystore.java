package ru.graduatework.notes.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import ru.graduatework.notes.R;

public class SaltHashKeystore implements Keystore {

    //Чем больше число итераций, тем дороже вычисление хэша для нас, а также для злоумышленника.
    private static final int iterations = 20 * 500;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    private static final String PIN_SHARED_PREF_NAME = "PIN_Shared_Pref";
    private static final String PIN_KEY = "pinKey";

    private Context mContext;

    public SaltHashKeystore(Context context) {
        this.mContext = context;
    }

    private SharedPreferences getSharedPref() {
        return mContext.getSharedPreferences(PIN_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean checkPin(String enteredPin)  {
        String saltHash = getSharedPref().getString(PIN_KEY, "");
        try {
            return check(enteredPin, saltHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saltHash.isEmpty();
    }

    @Override
    public void saveNew(String pin) {
        try {
            if (pin.length() == 4) {
                String hashPin = getSaltedHash(pin);
                getSharedPref().edit().putString(PIN_KEY, hashPin).apply();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.password_saved), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.enter_four_digits), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Вычисляет соленый хеш PBKDF2 данного незашифрованного пароля, пригодный для хранения в базе данных.
     * Пустые пароли не поддерживаются.
     */
    private String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
        }
        return "";
    }

    /**
     * Проверяет, соответствует ли данный обычный текстовый пароль
     * сохраненному в соленый хеш паролю.
     */
    private boolean check(String password, String stored) throws Exception {
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException(
                    "The stored password must have the form 'salt$hash'");
        }
        String hashOfInput = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hashOfInput = hash(password, Base64.getDecoder().decode(saltAndHash[0]));
        }
        if (hashOfInput != null) {
            return hashOfInput.equals(saltAndHash[1]);
        }
        return false;
    }

    // используя PBKDF2 от Sun, альтернативой является https://github.com/wg/scrypt
    // сравнение http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }
        return "";
    }


}