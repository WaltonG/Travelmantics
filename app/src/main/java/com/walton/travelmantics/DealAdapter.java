package com.walton.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{
    private FirebaseDatabase mFirebaseDatabase;
    ArrayList<TravelDeal> travelDeals;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;
    private ImageView dealImageView;

    public DealAdapter() {
        //FirebaseUtility.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtility.mFireBaseDatabase;
        mDatabaseReference =FirebaseUtility.mDatabaseReference;
        travelDeals = FirebaseUtility.mTravelDeals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal: ", travelDeal.getTitle());
                travelDeal.setiD(dataSnapshot.getKey());
                travelDeals.add(travelDeal);
                notifyItemInserted(travelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.deals_recycler_view_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal travelDeal = travelDeals.get(position);
        holder.bindData(travelDeal);

    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        TextView travelDealTitle;
        TextView travelDealDescription;
        TextView travelDealPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            travelDealTitle = itemView.findViewById(R.id.travel_deal_title_textView);
            travelDealDescription = itemView.findViewById(R.id.travel_deal_description_textView);
            travelDealPrice = itemView.findViewById(R.id.travel_deal_price_textView);
            dealImageView = itemView.findViewById(R.id.travel_deal_imageView);
            itemView.setOnClickListener(this);
        }

        public void bindData(TravelDeal travelDeal){
            travelDealTitle.setText(travelDeal.getTitle());
            travelDealDescription.setText(travelDeal.getDescription());
            travelDealPrice.setText(travelDeal.getPrice());
            showImage(travelDeal.getImageURL());
        }

        private void showImage(String imageURL) {
            if (imageURL!= null && imageURL.isEmpty()==false) {
                Picasso.get()
                        .load(imageURL)
                        .resize(160, 160)
                        .centerCrop()
                        .into(dealImageView);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            TravelDeal selectedDeal = travelDeals.get(position);
            view.getContext().
                    startActivity(new Intent(view.getContext(), AdminActivity.class).putExtra("TravelDeal", selectedDeal));
        }
    }
}
