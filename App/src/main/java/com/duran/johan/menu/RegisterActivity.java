package com.duran.johan.menu;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final EditText etNombre = (EditText)findViewById(R.id.etNombre);
        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);

        final Button bRegistrar = (Button)findViewById(R.id.bRegistrar);

        bRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String nombre = etNombre.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                RegisterActivity.this.startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(jsonResponse.getString("mensaje")).setNegativeButton("Retry", null).create().show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Map<String, String> params;
                params = new HashMap<>();
                params.put("nombre", nombre);
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

                //Viejo = "http://192.168.138.1:8081/proyectoJavier/android/registro.php"

                String direccion = getString(R.string.server)+"registro.php";


                MongoRequest registerMongoRequest = new MongoRequest(params, direccion, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerMongoRequest);
            }
        });



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
