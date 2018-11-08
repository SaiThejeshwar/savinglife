package ieee.donn.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import ieee.donn.R;
import ieee.donn.Services.MessagingService;

public class ConnectUsers1 extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvData, tvPatientName, tvPatientBlood;
    Button call, message, facebookThem,refresh;
    String name,blood,email,phone,facebook, country;
    int indx;

    private void collectPhoneNumbers(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("phone"));
        }
        //phone=phoneNumbers.toString();

        System.out.println(phoneNumbers.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_users1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        final String[] name1 = getResources().getStringArray(R.array.name);
        final String[] phone1 = getResources().getStringArray(R.array.phone);

        indx=new Random().nextInt(name1.length);
        name = name1[indx];
        phone= phone1[indx];


        tvData = (TextView) findViewById(R.id.data);
        tvPatientName = (TextView) findViewById(R.id.tv_patient_name);
        //tvPatientBlood = findViewById(R.id.tv_patient_blood);
        call = (Button) findViewById(R.id.call);
        facebookThem = (Button) findViewById(R.id.facebook);
        message = (Button) findViewById(R.id.message);
        refresh=(Button) findViewById(R.id.refreshing);


        tvData.setText("Phone :  " + phone + "\nFacebook :  " + name.toLowerCase() + "\n");
        tvPatientName.setText("Name :  " + name);
        //tvPatientBlood.setText("Donation Type :  " + blood);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                indx=new Random().nextInt(name1.length);
                name = name1[indx];
                phone= phone1[indx];



                tvData.setText("Phone :  " + phone  + "\nFacebook :  " + name.toLowerCase() + "\n");
                tvPatientName.setText("Name :  " + name.toUpperCase());

                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });

            }

        });




        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                callIntent.setData(Uri.parse("tel:" + phone));    //this is the phone number calling
                //check permission
                //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
                //the system asks the user to grant approval.
                if (ActivityCompat.checkSelfPermission(ConnectUsers1.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //request permission from user if the app hasn't got the required permission
                    ActivityCompat.requestPermissions(ConnectUsers1.this,
                            new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                            10);
                    return;
                } else {     //have got permission
                    try {
                        startActivity(callIntent);  //call activity and make phone call
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri sms_uri = Uri.parse("smsto:" + phone);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", "Hello, I have requested blood type.");
                startActivity(sms_intent);

            }
        });

        facebookThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(Intent.EXTRA_TEXT,
                                "Hello, I have requested blood type.");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ConnectUsers1.this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}