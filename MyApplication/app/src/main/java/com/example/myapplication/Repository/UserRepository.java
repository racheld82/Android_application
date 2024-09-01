package com.example.myapplication.Repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;


import com.example.myapplication.Database.UserDao;
import com.example.myapplication.Database.UserDatabase;
import com.example.myapplication.Model.User;
import com.example.myapplication.Network.ApiService;
import com.example.myapplication.Network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<User>> allUsers;

    public UserRepository(Application application) {
        UserDatabase database = UserDatabase.getInstance(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
    }

    public void insert(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    public void update(User user) {
        new UpdateUserAsyncTask(userDao).execute(user);
    }

    public void delete(User user) {
        new DeleteUserAsyncTask(userDao).execute(user);
    }

    public void deleteAllUsers() {
        new DeleteAllUsersAsyncTask(userDao).execute();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void fetchDataFromApi() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body();
                    insertUsers(userList); // שמירה ב-Room
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // טיפול בשגיאות
            }
        });
    }
    private void insertUsers(List<User> users) {
        User[] usersArray = users.toArray(new User[0]);
        new InsertUserAsyncTask(userDao).execute(usersArray);
    }
    // AsyncTasks for database operations
    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;
        private InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;
        private UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;
        private DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;
        private DeleteAllUsersAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            return null;
        }
    }
}
