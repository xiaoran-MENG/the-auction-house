package comp3350.group9.theauctionhouse.presentation.components.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.Field;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.databinding.ActivityLoginBinding;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;
import comp3350.group9.theauctionhouse.presentation.utils.LocalApplicationSetup;

public class LoginActivity extends AppCompatActivity {
    static boolean finishedLocalDbSetup = false;
    private ActivityLoginBinding bindings;
    private Button loginButton;
    private Button registrationButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindings = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bindings.getRoot());

        try {
            if (!finishedLocalDbSetup) {
                LocalApplicationSetup.copyDatabaseToDevice(getApplicationContext(), getAssets(), false);
                finishedLocalDbSetup = true;
            }
        } catch (final IOException ioe) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Unable to access application data: " + ioe.getMessage());
            alertDialog.show();
        } catch (ConfigsAccessException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Unable to check app version: " + e.getMessage());
            alertDialog.show();
        }
        setupUi();
    }

    private void setupUi() {
        // get view components that user interacts with
        loginButton = bindings.loginButton;
        registrationButton = bindings.loginRegistrationButton;
        rememberMeCheckBox = bindings.loginCheckbox;
        usernameLayout = bindings.loginUsernameLayout;
        passwordLayout = bindings.loginPasswordLayout;

        usernameEditText = usernameLayout.getEditText();
        passwordEditText = passwordLayout.getEditText();

        rememberMeAutoFill();

        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            try {
                clearErrors();
                User loggedInUser = UserAccountManager.of().loginUser(username, password);
                // Save info for future sessions
                rememberMeSaveInfo(username, password);

                Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                intent.putExtra("UserId", loggedInUser.id());
                startActivity(intent);
                finish();
            } catch (UserLoginException e) {
                if (e.hasErrorInField(Field.USERNAME)) usernameLayout.setError(e.getMessage());
                if (e.hasErrorInField(Field.PASSWORD)) passwordLayout.setError(e.getMessage());
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        registrationButton.setOnClickListener(v ->
        {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        });

        usernameLayout.addOnEditTextAttachedListener(textInputLayout -> textInputLayout.setError(null));

        passwordLayout.addOnEditTextAttachedListener(textInputLayout -> textInputLayout.setError(null));
    }


    // if there exists saved information, use it to populate login fields
    private void rememberMeAutoFill() {
        SharedPreferences prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        boolean check = prefs.getBoolean("remember", false);
        usernameEditText.setText(username);
        passwordEditText.setText(password);
        rememberMeCheckBox.setChecked(check);
    }

    // If remember me checkbox is ticked, save the login information
    private void rememberMeSaveInfo(String username, String password) {
        SharedPreferences prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (rememberMeCheckBox.isChecked()) {
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("remember", true);
            editor.apply();
        } else {
            editor.remove("username");
            editor.remove("password");
            editor.remove("remember");
            editor.apply();

        }
    }

    private void clearErrors() {
        usernameLayout.setError(null);
        passwordLayout.setError(null);
    }
}