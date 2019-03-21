package com.example.somename;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.somename.R;

import java.util.ArrayList;

public class VehicleListAdapter extends ArrayAdapter<String> {
//ss
    private ArrayList<Vehicle> vehicles;
    private LayoutInflater inflater;




    public VehicleListAdapter(Context context, ArrayList vehicles){
        super(context, -1, vehicles);

        this.vehicles = vehicles;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View vehicleList = inflater.inflate(R.layout.custom_vehicle_list_layout, parent, false);

        Vehicle currentVehicle = vehicles.get(position);

        TextView nameView = vehicleList.findViewById(R.id.vehicleName);
        ImageView vehicleLogo = vehicleList.findViewById(R.id.vehicleLogo);


        nameView.setText(currentVehicle.getName());
        vehicleLogo.setImageResource(currentVehicle.getLogo());






        return vehicleList;

    }


}
