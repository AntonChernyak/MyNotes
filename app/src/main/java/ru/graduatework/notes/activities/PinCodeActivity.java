package ru.graduatework.notes.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import ru.graduatework.notes.App;
import ru.graduatework.notes.R;
import ru.graduatework.notes.databinding.ActivityMainBinding;

public class PinCodeActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private StringBuilder pinBuilder = new StringBuilder();
    private boolean firstPinEnteredLabel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // запрещаем поворот
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // выставим язык
        onActivityCreateSetLocale();
        this.setTitle(R.string.notes);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        deleteButtonInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Проверка первого входа по наличию пароля
        if (App.getKeystore().checkPin("")) {
            if (firstPinEnteredLabel) {
                // выводим нужную активность
                firstPinEnteredLabel = false;
                Intent intent = new Intent(PinCodeActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else {
                // если пользователь вернулся на этот экран и пароль до сих пор не введён, то закрываем приложение
                finish();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        languageChange(PinCodeActivity.this);
    }

    // Логика кнопок с цифрами
    public void pinNumberButtonOnClick(View view) {
        Button btn = (Button) view;
        if (pinBuilder.length() <= 3) {
            pinBuilder.append(btn.getText());
            afterTextChanged(pinBuilder);

            if (pinBuilder.length() == 4) {
                if (App.getKeystore().checkPin(pinBuilder.toString())) {
                    Intent intent = new Intent(PinCodeActivity.this, ListOfNotesActivity.class);
                    startActivity(intent);
                    PinCodeActivity.this.finish();
                } else {
                    Toast.makeText(PinCodeActivity.this, getResources().getString(R.string.invalid_pin_code), Toast.LENGTH_LONG).show();
                    pinBuilder.setLength(0);
                    afterTextChanged(pinBuilder);
                }
            }

        }
    }

    // Логика кнопки "удалить"
    private void deleteButtonInit() {
        binding.buttonDelete.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                afterTextChanged(pinBuilder);
            }
        });
    }

    // метод изменяет отображение кружков при вводе пароля, в зависимости от количества символов на экране
    public void afterTextChanged(StringBuilder builder) {
        switch (builder.length()) {
            case 4:
                binding.pinCircleFourth.setImageResource(R.drawable.circle_full);
                break;
            case 3:
                binding.pinCircleFourth.setImageResource(R.drawable.circle_empty);
                binding.pinCircleThird.setImageResource(R.drawable.circle_full);
                break;
            case 2:
                binding.pinCircleSecond.setImageResource(R.drawable.circle_full);
                binding.pinCircleThird.setImageResource(R.drawable.circle_empty);
                break;
            case 1:
                binding.pinCircleSecond.setImageResource(R.drawable.circle_empty);
                binding.pinCircleFirst.setImageResource(R.drawable.circle_full);
                break;
            default:
                binding.pinCircleFirst.setImageResource(R.drawable.circle_empty);
                binding.pinCircleSecond.setImageResource(R.drawable.circle_empty);
                binding.pinCircleThird.setImageResource(R.drawable.circle_empty);
                binding.pinCircleFourth.setImageResource(R.drawable.circle_empty);
        }
    }

}