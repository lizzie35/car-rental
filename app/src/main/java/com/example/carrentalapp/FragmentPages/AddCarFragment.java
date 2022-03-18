package com.example.carrentalapp.FragmentPages;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.carrentalapp.AddVehicleCategory;
import com.example.carrentalapp.LoginActivity;
import com.example.carrentalapp.LoginActivity;
import com.example.carrentalapp.R;
import com.example.carrentalapp.Session.Session;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 *
 */
public class AddCarFragment extends Fragment {


    /**
     * A simple {@link Fragment} subclass.
     */

        private Button logout;

        public AddCarFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_add_car, container, false);
            initComponents(view);
            listenHandler();
            return view;
        }

        private void listenHandler() {

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session.close(getContext());
                    Intent loginPage = new Intent(getActivity(), AddVehicleCategory.class);
                    loginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginPage);
                }
            });

        }

        private void initComponents(View view) {
            logout = view.findViewById(R.id.logout);
        }

    }
