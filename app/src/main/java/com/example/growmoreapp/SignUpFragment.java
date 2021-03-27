package com.example.growmoreapp;

import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SignUpFragment() {

    }

    private TextView alreadyhaveanaccount;
    private FrameLayout parentframeLayout;
    private EditText name, email, pass, city;

    private Button signup;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FirebaseFirestore firebaseFirestore;

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyhaveanaccount = view.findViewById(R.id.tv_alreadyhaveanaccount);
        parentframeLayout = getActivity().findViewById(R.id.register_layout);

        name = view.findViewById(R.id.reg_name);
        city = view.findViewById(R.id.reg_city);
        email = view.findViewById(R.id.reg_email);
        pass = view.findViewById(R.id.reg_pass);


        signup = view.findViewById(R.id.sigin_button);
        progressBar = view.findViewById(R.id.signup_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailandPassword();
            }
        });
    }

    private void checkEmailandPassword() {
        if (email.getText().toString().matches(emailPattern)) {

            progressBar.setVisibility(View.VISIBLE);
            signup.setEnabled(false);

            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //Store userdata to firestore
                                Map<Object, String> userdata = new HashMap<>();
                                userdata.put("Name", name.getText().toString());
                                userdata.put("City", city.getText().toString());
                                userdata.put("E-mail", email.getText().toString());
                                userdata.put("Password", pass.getText().toString());

                                firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                        .set(userdata)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                                    Map<String, Object> ratingMap = new HashMap<>();
                                                    ratingMap.put("list_size", (long) 0);
//
                                                    Map<String, Object> cartMap = new HashMap<>();
                                                    cartMap.put("list_size", (long) 0);

                                                    List<String> documentNames = new ArrayList<>();
                                                    documentNames.add("MY_RATINGS");
                                                    documentNames.add("MY_CART");

                                                    final List<Map<String, Object>> documentFields = new ArrayList<>();
                                                    documentFields.add(ratingMap);
                                                    documentFields.add(cartMap);

                                                    for (int x = 0; x < documentNames.size(); x++) {
                                                        final int finalX = x;
                                                        userDataReference.document(documentNames.get(x))
                                                                .set(documentFields.get(x))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (finalX == documentFields.size() - 1) {
                                                                                startActivity(new Intent(getActivity(), MainActivity.class));
                                                                                getActivity().finish();
                                                                            }
                                                                        } else {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            signup.setEnabled(true);
                                                                            signup.setTextColor(Color.rgb(255, 255, 255));
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                signup.setEnabled(true);
                                signup.setBackgroundColor(Color.BLACK);
                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            email.setError("Invalid Email!");
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentframeLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(name.getText())) {
            if (!TextUtils.isEmpty(city.getText())) {
                if (!TextUtils.isEmpty(email.getText())) {
                    if (!TextUtils.isEmpty(pass.getText()) && pass.length() >= 8) {
                        signup.setEnabled(true);
                        signup.setBackgroundColor(Color.BLACK);
                    } else {
                        signup.setEnabled(false);
                    }
                } else {
                    signup.setEnabled(false);
                }
            } else {
                signup.setEnabled(false);
            }
        } else {
            signup.setEnabled(false);
        }
    }
}