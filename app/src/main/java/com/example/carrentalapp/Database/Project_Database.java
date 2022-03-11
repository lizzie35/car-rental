package com.example.carrentalapp.Database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.carrentalapp.Converter.Converter;
import com.example.carrentalapp.Model.Administrator;
import com.example.carrentalapp.Model.Billing;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Payment;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.Model.VehicleCategory;


@Database(entities = {Customer.class,       VehicleCategory.class,  Vehicle.class,
        Administrator.class,  Billing.class,          Booking.class,
        Insurance.class,      Payment.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class Project_Database extends RoomDatabase {
    public abstract CustomerDao customerDao();
    public abstract VehicleCategoryDao vehicleCategoryDao();
    public abstract VehicleDao vehicleDao();
    public abstract AdministratorDao administratorDao();
    public abstract BillingDao billingDao();
    public abstract BookingDao bookingDao();
    public abstract InsuranceDao insuranceDao();
    public abstract PaymentDao paymentDao();

    private static final String DATABASE_NAME = "car_rental_db";
    private static final String TAG = "CARRENTALINFO";

    private static volatile Project_Database instance = null;
    private static int counter = 0;

    public static Project_Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context,
                    Project_Database.class,
                    DATABASE_NAME
            )
                    .addCallback(callback)
                    .allowMainThreadQueries()
                    .build();
        }
        instance.getOpenHelper().getWritableDatabase(); //<<<<< FORCE OPEN
        return instance;
    }

    static Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG,"onCreate has been called. For " + this.getClass().getName() + " " + this + " Opencounter = " + counter);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(TAG,"onOpen has been called. For " + this.getClass().getName() + " " + this + " OPenCounter = " + counter++);
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
        }
    };

}

