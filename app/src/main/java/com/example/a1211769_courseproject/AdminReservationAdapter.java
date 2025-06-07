package com.example.a1211769_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.AdminReservationViewHolder> {
    
    private List<Reservation> reservationList;
    private SimpleDateFormat dateFormat;
    
    public AdminReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public AdminReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_reservation, parent, false);
        return new AdminReservationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AdminReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bind(reservation);
    }
    
    @Override
    public int getItemCount() {
        return reservationList.size();
    }
    
    class AdminReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvCustomerEmail, tvPropertyTitle, tvCheckIn, tvCheckOut, tvTotalPrice, tvGuests, tvReservationDate;
        
        public AdminReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerEmail = itemView.findViewById(R.id.tv_customer_email);
            tvPropertyTitle = itemView.findViewById(R.id.tv_property_title);
            tvCheckIn = itemView.findViewById(R.id.tv_check_in);
            tvCheckOut = itemView.findViewById(R.id.tv_check_out);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvGuests = itemView.findViewById(R.id.tv_guests);
            tvReservationDate = itemView.findViewById(R.id.tv_reservation_date);
        }
        
        public void bind(Reservation reservation) {
            tvCustomerName.setText(reservation.getCustomerName());
            tvCustomerEmail.setText(reservation.getUserEmail());
            tvPropertyTitle.setText(reservation.getPropertyTitle());
            tvCheckIn.setText(reservation.getCheckInDate());
            tvCheckOut.setText(reservation.getCheckOutDate());
            tvTotalPrice.setText("$" + String.format("%.2f", reservation.getTotalPrice()));
            tvGuests.setText(String.valueOf(reservation.getGuests()));
            
            // Format reservation date
            Date reservationDate = new Date(reservation.getReservationDate());
            tvReservationDate.setText(dateFormat.format(reservationDate));
            
            // Add animation
            itemView.setAlpha(0f);
            itemView.animate().alpha(1f).setDuration(300).setStartDelay(getAdapterPosition() * 50).start();
        }
    }
}
