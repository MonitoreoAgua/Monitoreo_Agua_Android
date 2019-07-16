package com.monitoreo.agua.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    boolean arPOIFlag;
    View header;
    TextView nombreUsuario;

    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        header = navigationView.getHeaderView(0);
        nombreUsuario= (TextView) header.findViewById(R.id.nombre_usuario);

        if(verificar_session()){

            menu.findItem(R.id.logout).setVisible(true);
            menu.findItem(R.id.logIn).setVisible(false);
            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            String correo = prefs.getString("correo", "No definido");
            nombreUsuario.setText(correo);

            if(prefs.contains("google")) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                if (googleApiClient == null){
                    googleApiClient = new GoogleApiClient.Builder(this)
                            .enableAutoManage(this, this)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                            .build();
                }


            }

        }else{
            nombreUsuario.setText("");
            menu.findItem(R.id.logout).setVisible(false);
            menu.findItem(R.id.logIn).setVisible(true);

        }





        arPOIFlag=false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.visualizar) {
            finish();
            startActivity(getIntent());
            if(arPOIFlag){
                //Toast.makeText(Navigation.this,getString(R.string.clic_limpiar), Toast.LENGTH_LONG).show();
                arPOIFlag=false;
            }
        } else if(id == R.id.arPOI){
            arPOIFlag=true;
            Toast.makeText(Navigation.this,
                    getString(R.string.clic_dos), Toast.LENGTH_LONG).show();

        } else if (id == R.id.insertar) {
            if(verificar_session()){
                goInsertScreen();
            }else{
                goLoginScreen();
            }
        } else if (id == R.id.modificar) {
            if (verificar_session()) {
                goEditScreen();
            } else {
                goLoginScreen();
            }
        } else if (id == R.id.aforo_nuevo) {
            if (verificar_session()) {
                goToNewAforo();
            } else {
                goLoginScreen();
            }
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.logIn) {
            goLoginScreen();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goEditScreen() {
        Intent intent = new Intent(this, editar_borrar.class);
        startActivity(intent);
    }

    /**
     * Ver si hay una sesion activa
     */
    private boolean verificar_session(){
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String correo = prefs.getString("correo", "No definido");
        if (correo != "No definido") {
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        if(prefs.contains("google")){
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (!opr.isDone()){
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {

                    }
                });
            }
        }


    }

    private void goInsertScreen() {
        Intent intent = new Intent(this, ActivityAgregar.class);
        startActivity(intent);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToNewAforo() {
        Intent intent = new Intent(this, ActivityAgregarAforo.class);
        startActivity(intent);
    }


    private void logout() {
        if(AccessToken.getCurrentAccessToken() == null){
            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            String correo = prefs.getString("correo", "No definido");
            if (correo != "No definido") {
                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
            }
            if(prefs.contains("google")){
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()){
                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                            editor.clear();
                            editor.apply();
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.error_cerrar_sesion), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        }else{
            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            LoginManager.getInstance().logOut();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        menu.findItem(R.id.logout).setVisible(false);
        menu.findItem(R.id.logIn).setVisible(true);
        nombreUsuario.setText(getString(R.string.no_session));



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
        logout();
    }
}