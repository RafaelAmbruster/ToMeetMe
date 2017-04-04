package com.app.tomeetme.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.util.FontTypefaceUtils;
import com.app.tomeetme.model.UserRegistrationResponse;
import com.app.tomeetme.rest.task.account.RegistrationTask;
import com.app.tomeetme.rest.task.account.ResponseRegisterCallBack;
import com.dd.processbutton.iml.ActionProcessButton;

public class ActivitySignup extends AppCompatActivity implements ResponseRegisterCallBack {

    EditText _nameText;
    EditText _lastText;
    EditText _emailText;
    EditText _mobileText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    ActionProcessButton _signupButton;
    TextView _loginLink;
    TextInputLayout nameWrapper, lastWrapper, emailWrapper, mobileWrapper, passwordWrapper, repasswordWrapper;
    View parent_view;
    SwitchCompat owner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        parent_view = findViewById(android.R.id.content);

        _nameText = (EditText) findViewById(R.id.input_name);
        _nameText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _lastText = (EditText) findViewById(R.id.input_last);
        _lastText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _emailText = (EditText) findViewById(R.id.input_email);
        _emailText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _mobileText = (EditText) findViewById(R.id.input_mobile);
        _mobileText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        _reEnterPasswordText.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _signupButton = (ActionProcessButton) findViewById(R.id.btn_signup);
        _signupButton.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        _loginLink = (TextView) findViewById(R.id.link_login);

        nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        nameWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        lastWrapper = (TextInputLayout) findViewById(R.id.lastWrapper);
        lastWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        emailWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        mobileWrapper = (TextInputLayout) findViewById(R.id.mobileWrapper);
        mobileWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        passwordWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        repasswordWrapper = (TextInputLayout) findViewById(R.id.repasswordWrapper);
        repasswordWrapper.setTypeface(FontTypefaceUtils.getRobotoCondensedLight(ActivitySignup.this));
        owner = (SwitchCompat) findViewById(R.id.is_owner);
        _signupButton.setMode(ActionProcessButton.Mode.ENDLESS);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public boolean validatePhone(String phoneNo) {
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }

    public void signup() {

        if (!validate()) {
            return;
        }

        String name = _nameText.getText().toString();
        String last = _lastText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();

        _signupButton.setEnabled(false);
        _signupButton.setProgress(1);
        new RegistrationTask(this).CallService(email, password, mobile, owner.isActivated(), name, last);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String last = _lastText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameWrapper.setError("at least 3 characters");
            valid = false;
        } else {
            nameWrapper.setError(null);
        }

        if (last.isEmpty() || last.length() < 3) {
            lastWrapper.setError("at least 3 characters");
            valid = false;
        } else {
            lastWrapper.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailWrapper.setError("enter a valid email address");
            valid = false;
        } else {
            emailWrapper.setError(null);
        }

        if (mobile.isEmpty() || !validatePhone(mobile)) {
            mobileWrapper.setError("invalid phone number must be in format xxx-xxx-xxxx");
            valid = false;
        } else {
            mobileWrapper.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 10) {
            passwordWrapper.setError("between 8 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordWrapper.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            repasswordWrapper.setError("Password do not match");
            valid = false;
        } else {
            repasswordWrapper.setError(null);
        }

        return valid;
    }

    @Override
    public void onResponseRegisterCallBack(final UserRegistrationResponse response) {

        _signupButton.setEnabled(true);
        _signupButton.setProgress(100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK, null);
                finish();
            }
        }, 1000);
    }

    @Override
    public void onError(String message, Integer code) {
        _signupButton.setProgress(-1);
        _signupButton.setText("Error!");
        showMessage(message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _signupButton.setEnabled(true);
                _signupButton.setProgress(0);
            }
        }, 1000);
    }

    private void showMessage(String message) {
        Snackbar snack = Snackbar.make(parent_view, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }
}