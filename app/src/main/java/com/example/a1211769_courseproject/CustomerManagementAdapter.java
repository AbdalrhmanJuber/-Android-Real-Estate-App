package com.example.a1211769_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerManagementAdapter extends RecyclerView.Adapter<CustomerManagementAdapter.CustomerViewHolder> {
    
    private List<User> customerList;
    private OnDeleteClickListener deleteClickListener;
    
    public interface OnDeleteClickListener {
        void onDeleteClick(User user);
    }
    
    public CustomerManagementAdapter(List<User> customerList, OnDeleteClickListener deleteClickListener) {
        this.customerList = customerList;
        this.deleteClickListener = deleteClickListener;
    }
    
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_management, parent, false);
        return new CustomerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customerList.get(position);
        holder.bind(customer);
    }
    
    @Override
    public int getItemCount() {
        return customerList.size();
    }
    
    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmail, tvName, tvGender, tvCountry;
        private Button btnDelete;
        
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_customer_email);
            tvName = itemView.findViewById(R.id.tv_customer_name);
            tvGender = itemView.findViewById(R.id.tv_customer_gender);
            tvCountry = itemView.findViewById(R.id.tv_customer_country);
            btnDelete = itemView.findViewById(R.id.btn_delete_customer);
        }
        
        public void bind(User customer) {
            tvEmail.setText(customer.getEmail());
            tvName.setText(customer.getFirstName() + " " + customer.getLastName());
            tvGender.setText(customer.getGender());
            tvCountry.setText(customer.getCountry());
            
            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(customer);
                }
            });
            
            // Add animation
            itemView.setAlpha(0f);
            itemView.animate().alpha(1f).setDuration(300).start();
        }
    }
}
