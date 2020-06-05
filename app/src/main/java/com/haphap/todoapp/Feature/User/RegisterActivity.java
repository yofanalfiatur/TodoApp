package com.haphap.todoapp.Feature.User;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haphap.todoapp.BaseActivity;
import com.haphap.todoapp.Model.User;
import com.haphap.todoapp.R;
import com.haphap.todoapp.ViewModel.UserViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
//inisiasi firebase
    private FirebaseAuth mAuth;

    private static final String TAG = "REGISTER";

    @BindView(R.id.registration_activity_fname)
    TextInputEditText etFirstName;
    @BindView(R.id.registration_activity_lname)
    TextInputEditText etLastName;
    @BindView(R.id.registration_activity_email)
    TextInputEditText etEmail;
    @BindView(R.id.registration_activity_password)
    TextInputEditText etPassword;
    @BindView(R.id.registration_activity_button)
    Button btReggister;
    @BindView(R.id.registration_activity_login_text)
    TextView tvLogin;
    @BindView(R.id.registration_progress_bar_layout)
    LinearLayout linearLayout;

    private UserViewModel viewModel;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        btReggister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        handler = new Handler();
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registration_activity_button:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                register();
                break;
            case R.id.registration_activity_login_text:
                gotoLoginActivity(this);
                break;
        }
    }

    private void register() {

        String firstName = Objects.requireNonNull(etFirstName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(etLastName.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (!validateInput(firstName, lastName, email, password)) {
            hideProgressbar();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Authentication success.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

            showProgressbar();

            User newUser = new User(firstName, lastName, email, password);
            Long insertedUserId = viewModel.inserUser(newUser);
            newUser.setId(insertedUserId);

            if (insertedUserId != null && insertedUserId != -1) {
                getSharedPrefence().setLoggedUserId(insertedUserId);
                Log.d(TAG, "register: SharedPref LoggedIn UserId " + getSharedPrefence().getUserId());
                openHandler(newUser);
            } else {
                showToast("Register Error !");
                hideProgressbar();
            }
        }

    }

    private void openHandler(final User newUser) {
        handler.postDelayed(() -> {
            hideProgressbar();
            showToast("Registration Succesfull");
            openMainActivity(RegisterActivity.this, newUser);
            finish();
        }, 2000);
    }

    private boolean validateInput(String firstName, String lastName, String email, String password) {
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required");
            return false;
        } else if (password.length() < 6) {
            etPassword.setError("Password too short");
            return false;
        }

        return true;
    }

    private void showProgressbar() {
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        linearLayout.setVisibility(View.GONE);
    }
}
