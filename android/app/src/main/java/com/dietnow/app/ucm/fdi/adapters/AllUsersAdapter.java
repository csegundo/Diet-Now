package com.dietnow.app.ucm.fdi.adapters;
import com.dietnow.app.ucm.fdi.model.user.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;



import java.util.ArrayList;


public class AllUsersAdapter extends ArrayAdapter<User> {

    public AllUsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
           //----- convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_all_user, parent, false);
        }
        // Lookup view for data population
        //----- TextView Name = (TextView) convertView.findViewById(R.id.names);

        // Populate the data into the template view using the data object
       //----- Name.setText(user.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}