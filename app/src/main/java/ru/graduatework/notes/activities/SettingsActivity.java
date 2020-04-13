package ru.graduatework.notes.activities;

import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Locale;
import java.util.Objects;

import ru.graduatework.notes.App;
import ru.graduatework.notes.adapters.CustomLanguageAdapter;
import ru.graduatework.notes.R;
import ru.graduatework.notes.databinding.ActivitySettingsBinding;


public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;
    private boolean pinVisibilityChangeLabel = true;
    private Locale localeLang;
    private SharedPreferences mySpinnersSharedPref;
    private Spinner languageSpinner;

    public static final String LANG_SPINNER_VALUE = "lang";
    public static final String OLD_LANG_SPINNER_VALUE = "old_Lang";
    public static final String SHARED_PREF_NAME = "MySpinner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.settings);
        // запрещаем поворот экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // установим кнопку "назад" на actionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initSpinner();
        initButtons();

        getDataFromSharedPref();
    }

    private void initButtons() {
        // Кнопка смены видимости пароля
        binding.visibilityImageButton.setOnClickListener(v -> {
            if (pinVisibilityChangeLabel) {
                // символы видны
                // устанавливаем иконку
                binding.visibilityImageButton.setImageResource(R.drawable.ic_visibility_black_24dp);
                // устанавливаем тип вводимых данных (вид, клавиатура)
                binding.newPinCodeEditText.setTransformationMethod(null);
            } else {
                binding.visibilityImageButton.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                binding.newPinCodeEditText.setTransformationMethod(new PasswordTransformationMethod());
            }
            pinVisibilityChangeLabel = !pinVisibilityChangeLabel;
            // переносим курсор в конец
            binding.newPinCodeEditText.setSelection(binding.newPinCodeEditText.length());
        });

        // Кнопка сохранения пин кода
        binding.savePinButton.setOnClickListener(v -> {
            // установить пароль
            String pin = binding.newPinCodeEditText.getText().toString();
            App.getKeystore().saveNew(pin);
        });

        // Кнопка сохранения языковых настроек
        binding.saveLanguageButton.setOnClickListener(v -> {
            // Установить язык
            Configuration config = new Configuration();
            config.setLocale(localeLang);
            getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            SettingsActivity.this.recreate();

            int oldLang = mySpinnersSharedPref.getInt(LANG_SPINNER_VALUE, 0);

            // сохраняем значения со спиннера языка
            SharedPreferences.Editor mySpinnersEditor = mySpinnersSharedPref.edit();
            int lang = binding.languageSpinner.getSelectedItemPosition();
            mySpinnersEditor.putInt(LANG_SPINNER_VALUE, lang);
            mySpinnersEditor.putInt(OLD_LANG_SPINNER_VALUE, oldLang);
            mySpinnersEditor.apply();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenu(SettingsActivity.this, item);
        return super.onOptionsItemSelected(item);
    }

    private void initSpinner() {
        languageSpinner = binding.languageSpinner;
        mySpinnersSharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String[] spinnerData = {getResources().getString(R.string.russian_lang), getResources().getString(R.string.english_lang)};
        int[] images = {R.drawable.flag_rus, R.drawable.flag_en};

        CustomLanguageAdapter languageAdapter = new CustomLanguageAdapter(getApplicationContext(), images, spinnerData);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        localeLang = new Locale("ru");
                        break;
                    case 1:
                        localeLang = new Locale("en");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // метод выставляет сохранённые значения на спиннер
    private void getDataFromSharedPref() {
        int langSaveData = mySpinnersSharedPref.getInt(LANG_SPINNER_VALUE, 0);
        languageSpinner.setSelection(langSaveData);
    }

}