package com.example.carrentalapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrentalapp.Database.BillingDao;
import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.PaymentDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Billing;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Payment;
import com.example.carrentalapp.Model.Vehicle;
import com.github.ybq.android.spinkit.style.Wave;
import com.squareup.picasso.Picasso;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Random;

public class BookingSummary extends AppCompatActivity {

    private Button back, book, payNow;

    //DRIVER DETAILS
    private TextView name, email, phoneNumber;

    //BOOKING SUMMARY
    private TextView vehicleName, rate, totalDays, _pickup, _return, insurance, insuranceRate, totalCost;

    //VEHICLE IMAGE
    private ImageView vehicleImage;

    //DATABASE TABLE
    private CustomerDao customerDao;
    private VehicleDao vehicleDao;
    private BookingDao bookingDao;
    private InsuranceDao insuranceDao;
    private BillingDao billingDao;
    private PaymentDao paymentDao;

    //BOOKING
    private Booking booking;
    //INSURANCE
    private Insurance chosenInsurance;
    //VEHICLE
    private Vehicle vehicle;

    private ProgressBar paidLoading;

    private Project_Database db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        initComponents();

        Wave wave = new Wave();
        paidLoading.setIndeterminateDrawable(wave);

        listenHandler();
        displayCustomerInformation();
        displaySummary();
        displayTotalCost();

    }

    private void initComponents() {
        back = findViewById(R.id.back);
        book = findViewById(R.id.book);
        payNow = findViewById(R.id.payNow);

        //DRIVER DETAILS
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);

        //BOOKING SUMMARY
        vehicleName = findViewById(R.id.vehicleName);
        rate = findViewById(R.id.rate);
        totalDays = findViewById(R.id.totalDays);
        _pickup = findViewById(R.id.pickup);
        _return = findViewById(R.id.dropoff);

        //INSURANCE TYPE
        insurance = findViewById(R.id.insurance);
        insuranceRate = findViewById(R.id.insuranceRate);

        //TOTAL COST
        totalCost = findViewById(R.id.totalCost);

        //VEHICLE IMAGE
        vehicleImage = findViewById(R.id.vehicleImage);

        //database instance
        db = Project_Database.getInstance(this);
        //DATABASE TABLE
        customerDao = db.customerDao();
        vehicleDao = db.vehicleDao();
        bookingDao = db.bookingDao();
        insuranceDao = db.insuranceDao();
        billingDao  = db.billingDao();
        paymentDao = db.paymentDao();
        //GET BOOKING OBJECT WHICH WAS PASSED FROM PREVIOUS PAGE
        booking = (Booking) getIntent().getSerializableExtra("BOOKING");
        chosenInsurance = insuranceDao.findInsurance(booking.getInsuranceID());
        vehicle = vehicleDao.findVehicle(booking.getVehicleID());

        paidLoading = findViewById(R.id.paidLoading);
        paidLoading.setVisibility(View.INVISIBLE);
    }
    //GOING BACK BUTTON
    private void listenHandler() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //CONFIRM AND BOOK
        book.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if(!book.isEnabled()){
                    toast("Payment must be done");
                    return;
                }
                generateBilling_Payment();
                Intent bookingCompletePage = new Intent(BookingSummary.this,BookingCompleteActivity.class);
//                bookingCompletePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bookingCompletePage.putExtra("BOOKING",booking);
                startActivity(bookingCompletePage);
            }//            @RequiresApi(api = Build.VERSION_CODES.O)

        });

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paidLoading.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paidLoading.setVisibility(View.INVISIBLE);
                        payNow.setText("Paid");
                        payNow.setEnabled(false);
                        book.setEnabled(true);
                    }
                },7000);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateBilling_Payment() {

        //GENERATE PAYMENT ID
        int paymentID = generateID(600,699);
        while(paymentDao.exist(paymentID)){
            paymentID=generateID(600,699);
        }

        //GENRATE BILLING ID
        int billingID = generateID(500,599);
        while(billingDao.exist(billingID)){
            billingID=generateID(500,599);
        }

        Calendar currentDate = Calendar.getInstance();

        Payment payment = new Payment(paymentID,"Credit",calculateTotalCost(),0);
        Billing billing = new Billing(billingID,"Paid",currentDate,0,paymentID);
        booking.setBillingID(billingID);
        booking.setBookingStatus("Waiting for approval");

        new Thread(() -> {
            bookingDao.insert(booking);
            billingDao.insert(billing);
            paymentDao.insert(payment);
            vehicle.setAvailability(false);
            vehicleDao.update(vehicle);
        });


    }

    private void displayCustomerInformation() {
        Customer customer = customerDao.findUser(booking.getCustomerID());
        //DISPLAY DRIVER INFO
        name.setText(customer.getFullName());
        email.setText(customer.getEmail());
        phoneNumber.setText(customer.getPhoneNumber());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displaySummary(){

        vehicleName.setText(vehicle.fullTitle());
        rate.setText("$"+vehicle.getPrice()+"/Day");
        totalDays.setText(getDayDifference(booking.getPickupDate(),booking.getReturnDate())+" Days");
        _pickup.setText(booking.getPickupTime());
        _return.setText(booking.getReturnTime());

        insurance.setText(chosenInsurance.getCoverageType());
        insuranceRate.setText("$"+chosenInsurance.getCost());

        Picasso.get().load(vehicle.getVehicleImageURL()).into(vehicleImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayTotalCost(){
        double cost = calculateTotalCost();
        totalCost.setText("$"+cost);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getDayDifference(Calendar start, Calendar end){
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant())+2;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double calculateTotalCost(){
        long _days = getDayDifference(booking.getPickupDate(),booking.getReturnDate());
        double _vehicleRate = vehicle.getPrice();
        double _insuranceRate = chosenInsurance.getCost();

        return (_days*_vehicleRate) + _insuranceRate;
    }

    private int generateID(int start, int end){
        Random rnd = new Random();
        int bound = end%100;
        int id = rnd.nextInt(bound)+start;
        return id;
    }

    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

}