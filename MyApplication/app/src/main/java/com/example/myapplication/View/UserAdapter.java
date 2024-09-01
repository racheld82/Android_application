package com.example.myapplication.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<User> users = new ArrayList<>();

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User currentUser = users.get(position);
        holder.textViewName.setText(currentUser.getName());
        holder.textViewEmail.setText(currentUser.getEmail());
        // אם יש תמונה, ניתן להשתמש בספריות כמו Glide או Picasso
        // Glide.with(holder.imageView.getContext()).load(currentUser.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    // מתודה להחזרת רשימת המשתמשים הנוכחית
    public List<User> getUsers() {
        return users;
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewEmail;
        private ImageView imageView;

        public UserHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewUserName);
            textViewEmail = itemView.findViewById(R.id.textViewUserEmail);
            imageView = itemView.findViewById(R.id.imageViewUser);
        }
    }
}
