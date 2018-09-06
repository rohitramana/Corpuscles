package com.example.anew.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private Button mRequestButton;
    private Button mDonateButton;
    Spinner spinner;
    String bloodType;
    int nothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestButton= (Button) findViewById(R.id.button_request);
        mDonateButton=(Button) findViewById(R.id.button_donor);

        mRequestButton.setOnClickListener(this);
        mDonateButton.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.spin_blood);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if(pos>0){
            bloodType=(String)parent.getItemAtPosition(pos);nothing=0;}
        else
            nothing=1;

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_donor) {
            startActivity(new Intent( MainActivity.this, DonorActivity.class));
        } else if (i == R.id.button_request) {
            if(nothing==1)
            { TextView errorText = (TextView)spinner.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("Blood group required");
                return;}


            System.out.println("MainActivity"+bloodType);

            Intent intent = new Intent(getBaseContext(), RequestActivity.class);
            intent.putExtra("bloodtype",bloodType);
            startActivity(intent);
            //  startActivity(new Intent(this, RequestActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
