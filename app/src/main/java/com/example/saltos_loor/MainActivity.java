package com.example.saltos_loor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener, OnMapReadyCallback {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar lista de usuarios
        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fetchUsers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // ← Vista satelital

        LatLng ecuador = new LatLng(-1.8312, -78.1834);
        mMap.addMarker(new MarkerOptions().position(ecuador).title("Ecuador"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecuador, 5));
    }

    private void fetchUsers() {
        RandomUserApiService apiService = UserProfileApp.getRandomUserApiService();
        Call<UserResponse> call = apiService.getUsers(20);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getUsers();
                    userAdapter = new UserAdapter(users, MainActivity.this);
                    recyclerView.setAdapter(userAdapter);
                    Log.d("API_RESPONSE", "Usuarios recibidos: " + users.size());
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Respuesta vacía o no exitosa");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Error al obtener usuarios", t);
            }
        });
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }
}