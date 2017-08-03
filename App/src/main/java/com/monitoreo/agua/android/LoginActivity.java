package com.monitoreo.agua.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    LoginButton fblogin_button;
    CallbackManager callbackManager;
    EditText etEmail;
    EditText etPassword;
    TextView bRegistrar;
    Button bLogin;
    GoogleApiClient googleApiClient;
    SignInButton signInGoogle_Button;

    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        initializaControls();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();



        bRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                                editor.putString("correo", jsonResponse.getString("correo"));
                                editor.putString("password", jsonResponse.getString("password"));
                                editor.apply();
                                LoginActivity.this.startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(jsonResponse.getString("mensaje")).setNegativeButton("Retry", null).create().show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Map<String, String> params;
                params = new HashMap<>();
                params.put("correo", email);
                String passwordC = "";
                try {
                    byte[] buffer           =   password.getBytes();
                    MessageDigest md        =   MessageDigest.getInstance("SHA-512");
                    md.update(buffer);
                    byte[] digest           =   md.digest();

                    for(int i = 0 ; i < digest.length ; i++) {
                        int b               =   digest[i] & 0xff;
                        if(Integer.toHexString(b).length() == 1)
                            passwordC    =   passwordC + "0";
                        passwordC        =   passwordC + Integer.toHexString(b);
                    }
                } catch(NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                params.put("password", passwordC);


                //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/login.php"

                String direccion = getString(R.string.server)+"login.php";

                MongoRequest loginMongoRequest = new MongoRequest(params, direccion, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginMongoRequest);



            }
        });


        fblogin_button.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object,GraphResponse response) {

                                    final JSONObject json = response.getJSONObject();
                                    try {
                                        if(json != null){
                                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                                            editor.putString("correo",json.getString("email"));
                                            editor.apply();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        goMainScreen();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                    }
                });

        signInGoogle_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
            
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount cuenta = result.getSignInAccount();
            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
            editor.putString("correo",cuenta.getEmail());
            editor.putBoolean("google",true);
            editor.apply();
            goMainScreen();
        }else{
            Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
        }

    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initializaControls() {
        signInGoogle_Button = (SignInButton) findViewById(R.id.signInGoogle_Button);
        signInGoogle_Button.setSize(SignInButton.SIZE_WIDE);
        signInGoogle_Button.setColorScheme(SignInButton.COLOR_DARK);

        callbackManager = CallbackManager.Factory.create();
        etEmail = (EditText)findViewById(R.id.email);
        etPassword = (EditText)findViewById(R.id.password);
        bLogin = (Button)findViewById(R.id.login_button);

        bRegistrar = (TextView)findViewById(R.id.registrarse);
        fblogin_button = (LoginButton)findViewById(R.id.fblogin_button);
        fblogin_button.setReadPermissions(Arrays.asList("email"));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


}
