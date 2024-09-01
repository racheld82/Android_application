package com.example.myapplication.Network;

import com.example.myapplication.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/users?page=2")
    Call<List<User>> getUsers();
}

