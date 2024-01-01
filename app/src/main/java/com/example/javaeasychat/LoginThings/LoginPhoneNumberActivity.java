package com.example.javaeasychat.LoginThings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.javaeasychat.R;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText etPhoneNumber;
    Button btnSentOtp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.login_countrycode);
        etPhoneNumber = findViewById(R.id.login_mobile_number);
        btnSentOtp = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(etPhoneNumber);

        btnSentOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryCodePicker.isValidFullNumber();
                if (!countryCodePicker.isValidFullNumber()){
                    etPhoneNumber.setError("Phone number not valid");
                    return;
                }
                Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
                intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
                startActivity(intent);
            }
        });
    }
}