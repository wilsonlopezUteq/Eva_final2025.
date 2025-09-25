package com.example.saltos_loor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fetchUsers();
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
                    // Nueva frase de Log:
                    Log.d("DATA_LOAD", "Datos de usuarios cargados satisfactoriamente. Total de ítems: " + users.size());
                } else {
                    // Nueva frase de Toast:
                    Toast.makeText(MainActivity.this, "La fuente de datos devolvió un resultado inválido", Toast.LENGTH_SHORT).show();
                    // Nueva frase de Log:
                    Log.e("API_STATUS", "Respuesta recibida correctamente (2xx), pero el cuerpo de la respuesta es ilegible o vacío.");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Nueva frase de Toast:
                Toast.makeText(MainActivity.this, "Conexión interrumpida. No se pudo obtener la información.", Toast.LENGTH_SHORT).show();
                // Nueva frase de Log:
                Log.e("NETWORK_FAIL", "Error de conectividad al intentar obtener el listado de usuarios", t);
            }
        });
    }

    @Override
    public void onUserClick(User user) {
        // Nueva frase de Log:
        Log.d("ACTIVITY_JUMP", "Lanzando vista de detalle para: " + user.getName().getFirst());

        Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }
}