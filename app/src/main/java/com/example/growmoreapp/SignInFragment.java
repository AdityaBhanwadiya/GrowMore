package com.example.growmoreapp;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Map;

import io.paperdb.Paper;

import static com.example.growmoreapp.RegisterActivity.onResetPasswordFragment;

public class SignInFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SignInFragment() {
    }


    private TextView dontHaveAccount;
    private FrameLayout parentframeLayout;

    private EditText email,pass;
    private Button login;

    private  FirebaseAuth firebaseAuth;

    private  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private ProgressBar progressBar;


    private com.rey.material.widget.CheckBox chkBoxRememberMe;

    private TextView forgotpass;

    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAccount = view.findViewById(R.id.tv_donthaveaccount);
        parentframeLayout = getActivity().findViewById(R.id.register_layout);

        email = view.findViewById(R.id.signin_email);
        pass = view.findViewById(R.id.signin_pass);

        login = view.findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.sigin_progressbar);


        forgotpass = view.findViewById(R.id.forgot_pass);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view,savedInstanceState);
        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new Choice());
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailandpassword();
            }
        });
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentframeLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {

        if(!TextUtils.isEmpty(email.getText())) {
            if(!TextUtils.isEmpty(pass.getText())){
                login.setEnabled(true);
                login.setBackgroundColor(Color.BLACK);
            }else {
                login.setEnabled(false);
            }
        }else{
            login.setEnabled(false);

        }

    }

    private void checkEmailandpassword()
    {
        if(email.getText().toString().matches(emailPattern)){
            if(pass.length()>=8){

                progressBar.setVisibility(View.VISIBLE);
                login.setEnabled(true);
                login.setBackgroundColor(Color.BLACK);

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent mainintent = new Intent(getActivity(), MainActivity.class);
                            startActivity(mainintent);
                            getActivity().finish();
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            login.setEnabled(true);
                            login.setBackgroundColor(Color.BLACK);
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getActivity(),"Incorrect password",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(),"Incorrect email",Toast.LENGTH_SHORT).show();
        }
    }
}