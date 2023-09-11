package comp3350.group9.theauctionhouse.presentation.components.account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.Field;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding bindings;
    private Button registrationButton;
    TextInputLayout usernameLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindings = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(bindings.getRoot());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        registrationButton = bindings.registerButton;
        usernameLayout = bindings.registerUsernameLayout;
        emailLayout = bindings.registerEmailLayout;
        passwordLayout = bindings.registerPasswordLayout;

        registrationButton.setOnClickListener(v -> {
            clearErrors();
            String username = usernameLayout.getEditText().getText().toString();
            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();
            try {
                UserAccountManager.of().registerUser(username, email, password);
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_LONG).show();
                finish();
            } catch (UserRegistrationException e) {
                if (e.hasErrorInField(Field.USERNAME)) usernameLayout.setError(e.getMessageForField(Field.USERNAME));
                if (e.hasErrorInField(Field.EMAIL)) emailLayout.setError(e.getMessageForField(Field.EMAIL));
                if (e.hasErrorInField(Field.PASSWORD)) passwordLayout.setError(e.getMessageForField(Field.PASSWORD));
                if (e.hasErrorInField(Field.UNKNOWN)) {
                    usernameLayout.setError("");
                    emailLayout.setError("");
                    passwordLayout.setError("");
                    Toast.makeText(getApplicationContext(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                usernameLayout.setError("");
                emailLayout.setError("");
                passwordLayout.setError("");
                Toast.makeText(getApplicationContext(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void clearErrors() {
        usernameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
    }
}