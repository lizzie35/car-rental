package com.example.carrentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleCategoryDao;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.*;
import com.example.carrentalapp.R;

import java.util.Random;


public class RegistrationActivity extends AppCompatActivity{

    private Button register;
    private TextView login;

    private TextView expiryDate;
    private TextView dob;
    private CustomerDao customerDao;

    private Project_Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initComponents();

        clickListenHandler();

    }

    //Initialize all the components in Register Page
    private void initComponents(){

        //Register Button
        register = findViewById(R.id.register);
        //Login Button
        login = findViewById(R.id.login);
        //Expiry Button
        expiryDate = findViewById(R.id.expiryDate);
        //Date of Birth Button
        dob = findViewById(R.id.dob);

        //database
        db = Project_Database.getInstance(this);

        //Get the Customer Room (table)
        customerDao =db.customerDao();

    }

    //This method handles all the click events
    private void clickListenHandler(){

        //Expiry Date Listener
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(expiryDate);
            }
        });

        //Date of Birth Listener
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(dob);
            }
        });

        //Login Listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(registerPage);
            }
        });

        //Register Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = createCustomerObject();

                if(customerDao != null) {
                    //If customer object is null -> Incomplete form
                    //If customer object not null -> Complete form
                    if(customer != null) {
                        customerDao.insert(customer); //Insert the customer object into database
                        toast("Registration Successful");
                        populateVehicles();
                        finish();
                    }
                }
            }
        });
    }

    //Opening a Calendar Dialog
    private void openCalendar(final TextView dateFieldButton) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + month + "-" + dayOfMonth;
                dateFieldButton.setText(date);
            }
        });

        datePickerDialog.show();
    }

    //Create the customer object from the form
    private Customer createCustomerObject(){

        String firstName = ((EditText)findViewById(R.id.firstName)).getText().toString().trim();
        String middleName = ((EditText)findViewById(R.id.middleName)).getText().toString().trim();
        String lastName = ((EditText)findViewById(R.id.lastName)).getText().toString().trim();

        String email = ((EditText)findViewById(R.id.email)).getText().toString().trim();

        String driverLicense = ((EditText)findViewById(R.id.license)).getText().toString().trim();
        String expiry = expiryDate.getText().toString().trim();
        String dateOfBirth = dob.getText().toString().trim();

        String phoneNumber = ((EditText)findViewById(R.id.phoneNumber)).getText().toString().trim();

        String street = ((EditText)findViewById(R.id.street)).getText().toString().trim();
        String city = ((EditText)findViewById(R.id.city)).getText().toString().trim();
        String postalCode = ((EditText)findViewById(R.id.postalCode)).getText().toString().trim();

        String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        String confirm_password = ((EditText)findViewById(R.id.confirmPassword)).getText().toString().trim();

        boolean success = fieldRequiredCheck(firstName,lastName,email,driverLicense,expiry,dateOfBirth,phoneNumber,street,city,postalCode);

        int id = generateID();

        while(customerDao.exist(id)){
            id = generateID();
        }
        //This is to check if all fields are entered
        if(success){

            //Password and Confirm Password Check
            if(password.length() > 0 && password.equals(confirm_password)) {

                return new Customer(id,firstName, middleName, lastName,
                        email, driverLicense, expiry,
                        dateOfBirth, phoneNumber, street,
                        city, postalCode, password
                );
            }else{
                toast("Password do not match");
            }
        }else{
            toast("Incomplete Form");
        }

        return null;
    }

    private boolean fieldRequiredCheck(String firstName,String lastName, String email, String driverLicense, String expiry, String dateOfBirth, String phoneNumber, String street, String city, String postalCode) {
        return  !firstName.equals("") && !lastName.equals("") &&
                !email.equals("") && !driverLicense.equals("") && !expiry.equals("") &&
                !dateOfBirth.equals("") && !phoneNumber.equals("") && !street.equals("") &&
                !city.equals("") && !postalCode.equals("");
    }


    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

    private int generateID(){
        Random rnd = new Random();
        int id = 202000 + rnd.nextInt(65)+10;
        return id;
    }


    private void populateVehicles(){
        VehicleCategoryDao vehicleCategoryDao = db.vehicleCategoryDao();
        VehicleDao vehicleDao = db.vehicleDao();
        InsuranceDao insuranceDao = db.insuranceDao();

        VehicleCategory vc1 = new VehicleCategory("sedan", 100, -47032, "https://di-uploads-pod12.dealerinspire.com/beavertonhondaredesign/uploads/2017/12/2018-Honda-Accord-Sedan-Sideview.png");
        VehicleCategory vc2 = new VehicleCategory("suv", 101, -13936668, "https://medias.fcacanada.ca//specs/fiat/500X/year-2020/media/images/wheelarizer/2019-fiat-500X-wheelizer-sideview-jelly-WPB_eb45b9d20027fd644f0f273785d919cf-1600x1020.png");
        VehicleCategory vc3 = new VehicleCategory("sports", 102, -4068, "https://images.dealer.com/ddc/vehicles/2019/Lamborghini/Huracan/Coupe/trim_LP5802_b8a819/perspective/side-left/2019_76.png");
        VehicleCategory vc4 = new VehicleCategory("coupe", 103, -3092272, "https://di-uploads-pod12.dealerinspire.com/beavertonhondaredesign/uploads/2017/12/2017-Honda-Accord-Coupe-Sideview.png");
        VehicleCategory vc5 = new VehicleCategory("van", 104, -9539986, "https://st.motortrend.com/uploads/sites/10/2016/12/2017-mercedes-benz-metris-base-passenger-van-side-view.png");

        vehicleCategoryDao.insert(vc1);
        vehicleCategoryDao.insert(vc2);
        vehicleCategoryDao.insert(vc3);
        vehicleCategoryDao.insert(vc4);
        vehicleCategoryDao.insert(vc5);


        Vehicle v1 = new Vehicle(273, 65.5, 5, 6497, "nissan", "altima", 2020, "sedan", true, "https://65e81151f52e248c552b-fe74cd567ea2f1228f846834bd67571e.ssl.cf1.rackcdn.com/ldm-images/2020-Nissan-Altima-Color-Super-Black.png");
        Vehicle v2 = new Vehicle(285, 54.8, 5, 4578, "toyota", "avalon", 2020, "sedan", true, "https://img.sm360.ca/ir/w640h390c/images/newcar/ca/2020/toyota/avalon/limited/sedan/main/2020_toyota_avalon_LTD_Main.png");
        Vehicle v3 = new Vehicle(287, 50.99, 5, 1379, "subaru", "wrx", 2020, "sedan", true, "https://img.sm360.ca/ir/w640h390c/images/newcar/ca/2020/subaru/wrx/base-wrx/sedan/exteriorColors/12750_cc0640_001_d4s.png");

        Vehicle v4 = new Vehicle(265, 58.89, 5, 6490, "kia", "telluride", 2020, "suv", true, "https://www.cstatic-images.com/car-pictures/xl/usd00kis061c021003.png");
        Vehicle v5 = new Vehicle(229, 86.5, 5, 4970, "lincoln", "aviator", 2020, "suv", true, "https://www.cstatic-images.com/car-pictures/xl/usd00lis021b021003.png");
        Vehicle v6 = new Vehicle(219, 95.0, 5, 595, "ford", "explorer", 2020, "suv", true, "https://www.cstatic-images.com/car-pictures/xl/usd00fos102d021003.png");

        Vehicle v7 = new Vehicle(297, 56.0, 2, 200, "chevrolet", "camaro", 2020, "coupe", false, "https://www.cstatic-images.com/car-pictures/xl/usc90chc022b021003.png");

        vehicleDao.insert(v1);
        vehicleDao.insert(v2);
        vehicleDao.insert(v3);
        vehicleDao.insert(v4);
        vehicleDao.insert(v5);
        vehicleDao.insert(v6);
        vehicleDao.insert(v7);

        Insurance i1 = new Insurance("None", 0);
        Insurance i2 = new Insurance("Basic", 15);
        Insurance i3 = new Insurance("Premium", 25);
        insuranceDao.insert(i1);
        insuranceDao.insert(i2);
        insuranceDao.insert(i3);


    }



}