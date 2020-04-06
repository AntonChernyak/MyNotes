package ru.graduatework.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.graduatework.notes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StringBuilder pinBuilder = new StringBuilder();
    public static final String PIN_KEY = "pinKey";
    public static final String PIN_SHARED_PREF_NAME = "PIN_Shared_Pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // Если после двойного нажатия кнопки назад получили ключ, то закрываем приложение
        if (getIntent().getBooleanExtra(ListOfNotesActivity.FINISH_APP_KEY, false)) finish();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Проверка первого входа по наличию пароля
        SharedPreferences preferences = getSharedPreferences(PIN_SHARED_PREF_NAME, MODE_PRIVATE);
        String pinCode = preferences.getString(PIN_KEY, "");
        if ("".equals(pinCode)) {
            // выводим нужную активность
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        buttonsInit();
    }

    private void pinNumberButtonOnclick(Button btn) {
        btn.setOnClickListener(v -> {
            if (pinBuilder.length() <= 3) {
                pinBuilder.append(btn.getText());
                afterTextChanged(pinBuilder);

                if (pinBuilder.length() == 4) {
                    if (checkPin()) {
                        Intent intent = new Intent(MainActivity.this, ListOfNotesActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.invalid_pin_code, Toast.LENGTH_LONG).show();
                    }
                    pinBuilder.setLength(0);
                    afterTextChanged(pinBuilder);
                }

            }
        });
    }

    private void buttonsInit() {

        pinNumberButtonOnclick(binding.buttonOne);
        pinNumberButtonOnclick(binding.buttonTwo);
        pinNumberButtonOnclick(binding.buttonThree);
        pinNumberButtonOnclick(binding.buttonFour);
        pinNumberButtonOnclick(binding.buttonFive);
        pinNumberButtonOnclick(binding.buttonSix);
        pinNumberButtonOnclick(binding.buttonSeven);
        pinNumberButtonOnclick(binding.buttonEight);
        pinNumberButtonOnclick(binding.buttonNine);
        pinNumberButtonOnclick(binding.buttonZero);

        binding.buttonDelete.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                afterTextChanged(pinBuilder);
            }
        });

    }

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


    private boolean checkPin() {
        SharedPreferences preferences = getSharedPreferences(PIN_SHARED_PREF_NAME, MODE_PRIVATE);
        String pinCode = preferences.getString(PIN_KEY, "");
        return pinBuilder.toString().equals(pinCode);
    }
}
