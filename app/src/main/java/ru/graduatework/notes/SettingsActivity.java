package ru.graduatework.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import ru.graduatework.notes.databinding.ActivitySettingsBinding;

import static ru.graduatework.notes.MainActivity.PIN_KEY;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // установим кнопку "назад" на actionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        onClickVisibilityButton();
        onClickSaveButton();

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
            String pin = binding.newPinCodeEditText.getText().toString();
            if (pin.length() == 4) {
                SharedPreferences preferences = getSharedPreferences(MainActivity.PIN_SHARED_PREF_NAME, MODE_PRIVATE);
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

}