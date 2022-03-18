package com.example.carrentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleCategoryDao;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.Model.VehicleCategory;
import com.example.carrentalapp.R;
import com.example.carrentalapp.Session.Session;

public class LoginActivity extends AppCompatActivity {

    private TextView register;
    private TextView forgotPass;
    private Button login;

    private EditText email;
    private EditText password;

    private Project_Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //IF USER ALREADY LOGGED IN => REDIRECT TO HOME PAGE
        Boolean isLoggedIn = Boolean.valueOf(Session.read(LoginActivity.this, "isLoggedIn", "false"));
        if (isLoggedIn) {
            Intent homePage = new Intent(LoginActivity.this, UserViewActivity.class);
            startActivity(homePage);
        }

        initComponents();
        clickListenHandler();

    }

    //This will initialize all the clickable components in Login page
    private void initComponents() {

        //Register Button
        register = findViewById(R.id.register);

        //Login Button
        login = findViewById(R.id.login);

        //Forgot Password Button
        forgotPass = findViewById(R.id.forgot_password);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        db = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries().build();
    }


    //This will handle all the click events on the login page
    private void clickListenHandler() {

        //Register Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registerPage);
            }
        });

        //Login Listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = findViewById(R.id.email);
                password = findViewById(R.id.password);

                CustomerDao customerDao = db.customerDao();
                Customer check = customerDao.findUser(email.getText().toString().trim(), password.getText().toString().trim());
                if (check != null) {
                    Session.save(LoginActivity.this, "customerID", check.getCustomerID() + "");
                    Session.save(LoginActivity.this, "isLoggedIn", "true");

                    Intent homePage = new Intent(LoginActivity.this, UserViewActivity.class);
                    homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homePage);
                } else {
                    toast("Unsuccessful");
                }
            }
        });

        //Forgot Password Listener
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.vehicleCategoryDao().updateQuantity("Sedan");
                db.vehicleCategoryDao().updateQuantity("Suv");
                db.vehicleCategoryDao().updateQuantity("Coupe");
                toast("Updated All");
            }
        });




    }

    //DEBUGING
    private void toast(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getAllCustomers() {
        CustomerDao customerDao = db.customerDao();
        for (Customer c : customerDao.getAll()) {
            Log.d("MainActivity", "CUSTOMER => " + c.toString());
        }
    }

    private void getAllVehicleCategories(){
        VehicleCategoryDao vehicleCategoryDao = db.vehicleCategoryDao();
        for(VehicleCategory c: vehicleCategoryDao.getAllCategory()){
            Log.d("MainActivity", "VEHICLE CATEGORY => " + c.toString());
        }
    }

    private void getAllVehicles(){
        VehicleDao vehicleDao = db.vehicleDao();
        for (Vehicle c : vehicleDao.getAll()) {
            Log.d("MainActivity", "VEHICLE => " + c.toString());
        }
    }

}
