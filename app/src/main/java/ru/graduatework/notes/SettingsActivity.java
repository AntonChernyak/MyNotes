package ru.graduatework.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import ru.graduatework.notes.databinding.ActivitySettingsBinding;

import static ru.graduatework.notes.PinCodeActivity.PIN_KEY;

public class SettingsActivity extends AppCompatActivity {


    private ActivitySettingsBinding binding;
    private boolean flag = true;
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
        onClickVisibilityButton();
        onClickSaveButton();

        getDataFromSharedPref();
    }

    private void onClickVisibilityButton() {
        binding.visibilityImageButton.setOnClickListener(v -> {
            if (flag) {
                // символы видны
                // устанавливаем иконку
                binding.visibilityImageButton.setImageResource(R.drawable.ic_visibility_black_24dp);
                // устанавливаем тип вводимых данных (вид, клавиатура)
                binding.newPinCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            } else {
                binding.visibilityImageButton.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                binding.newPinCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
            flag = !flag;
            // переносим курсор в конец
            binding.newPinCodeEditText.setSelection(binding.newPinCodeEditText.length());
        });
    }

    private void onClickSaveButton() {
        binding.saveButton.setOnClickListener(v -> {

            // Установить язык
            Configuration config = new Configuration();
            config.setLocale(localeLang);
            getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            recreate();

            int oldLang = mySpinnersSharedPref.getInt(LANG_SPINNER_VALUE, 0);

            // сохраняем значения со спиннера языка
            SharedPreferences.Editor mySpinnersEditor = mySpinnersSharedPref.edit();
            int lang = binding.languageSpinner.getSelectedItemPosition();
            mySpinnersEditor.putInt(LANG_SPINNER_VALUE, lang);
            mySpinnersEditor.putInt(OLD_LANG_SPINNER_VALUE, oldLang);
            mySpinnersEditor.apply();

            // установить пароль
            String pin = binding.newPinCodeEditText.getText().toString();
            if (pin.length() == 4) {
                SharedPreferences preferences = getSharedPreferences(PinCodeActivity.PIN_SHARED_PREF_NAME, MODE_PRIVATE);
                preferences.edit().putString(PIN_KEY, pin).apply();
                Toast.makeText(SettingsActivity.this, R.string.password_saved, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingsActivity.this, ListOfNotesActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SettingsActivity.this, R.string.enter_four_digits, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
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