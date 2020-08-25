package com.example.campusgeoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campusgeoquiz.Interface.IOnLoadLocationListener;
import com.example.campusgeoquiz.Interface.IOnLoadQuiz;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.Quiz;


public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback, GeoQueryEventListener, IOnLoadLocationListener, IOnLoadQuiz {

    private GoogleMap mMap;
    private static final String TAG = "Add_Marker";
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private GeoFire geoFire;

    private List<LatLng> item_area = new ArrayList<>();
    private List<String> images;
    private List<Quiz> quiz_list = new ArrayList<>();
    private List<Target> targets;

    private DatabaseReference myLocationRef;
    private IOnLoadLocationListener listener;

    static FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    private DatabaseReference campus;
    private DatabaseReference userDb;
    private Location lastLocation;
    private GeoQuery geoQuery;

    // connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Quiz");
    private StorageReference storageReference;

    private View dialogView;
    private View quizView;
    AlertDialog quizDialog;
    AlertDialog alertDialog;

    private static Uri imageUri;

    private String image;
    private Double myLatitude;
    private Double myLongitude;
    private String qQuestion;
    private String qAnswer;
    private String qWrong1;
    private String qWrong2;
    private String qWrong3;

    HashMap<Marker, LatLng> mMarkersHashMap;
    private ArrayList<PicassoMarker> markers = new ArrayList<PicassoMarker>();

    private PicassoMarker myMarker;
    private Marker location_marker;
    MarkerOptions markerOption;
    private EditText question;
    private EditText answer;
    private EditText wrong1;
    private EditText wrong2;
    private EditText wrong3;
    private EditText longitude;
    private EditText latitude;
    public static Double cLongitude;
    public static Double cLatitude;
    private LatLng geofence;
    private int i=0;
    private ProgressBar timer;

    private List<Quiz> latLngList = new ArrayList<>();
    private int counter = latLngList.size();
    private Boolean blank = true;
    private Boolean answ = false;
    private IOnLoadQuiz listener1;
    private String conv;
    private Integer lvl;
    private Integer exp;
    private Integer j;
    private Integer sum;
    private String cEmail;
    private ProgressBar progress;
    private TextView level ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        storageReference = FirebaseStorage.getInstance().getReference();

        level = findViewById(R.id.lvl);
        progress = findViewById(R.id.progress);

        mMarkersHashMap = new HashMap<Marker, LatLng>();
        targets = new ArrayList<>();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        buildLocationRequest();
                        buildLocationCallback();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UserMapsActivity.this);

                        initArea();
                        settingGeoFire();

                       Button marker = findViewById(R.id.add_marker);

                       marker.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(UserMapsActivity.this, "You must enable permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {

                //get the email of the current user and replace the special characters with "_".
                cEmail = profile.getEmail();
                cEmail = cEmail.replaceAll("[@, .]", "_");

            }
        }

        userDb = FirebaseDatabase.getInstance()
                .getReference("Invetory_for_" + cEmail);

        Log.d("user", ": " + userDb);



        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exp = dataSnapshot.child("exp").getValue(Integer.class);
                j = dataSnapshot.child("to_lvl").getValue(Integer.class);

                if(exp != null || j !=  null || lvl != null ){
                    sum = j - exp;

                    progress.setMax(j);
                    progress.setProgress(sum);

                    lvl = dataSnapshot.child("lvl").getValue(Integer.class);
                    level.setText(String.valueOf(lvl));
                    Log.d("to level", ":" + j);
                    Log.d("progress", ":" + exp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button inventory = (Button) findViewById(R.id.inventory);

        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent invetory = new  Intent(getApplicationContext(),invetory.class);
                startActivity(invetory);
//                switchView();
            }
        });


    }


//    private void showAddMarkerForm() {
//
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//
//        dialogView = LayoutInflater.from(this).inflate(R.layout.add_marker_dialog, viewGroup, false);
//
//        Button btnok = (Button) dialogView.findViewById(R.id.buttonOk);
//
//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        //setting the view of the builder to our custom view that we already inflated
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//        alertDialog = builder.create();
//        alertDialog.show();
//
//        question = dialogView.findViewById(R.id.question);
//        answer = dialogView.findViewById(R.id.answer);
//        wrong1 = dialogView.findViewById(R.id.wrong1);
//        wrong2 = dialogView.findViewById(R.id.wrong2);
//        wrong3 = dialogView.findViewById(R.id.wrong3);
//        latitude = dialogView.findViewById(R.id.text_latitude);
//        longitude = dialogView.findViewById(R.id.text_longitude);
//
//        btnok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                campus = FirebaseDatabase.getInstance()
//                        .getReference("Quiz");
//
//                final String Question = question.getText().toString().trim();
//                final String Answer = answer.getText().toString().trim();
//                final String Wrong1 = wrong1.getText().toString().trim();
//                final String Wrong2 = wrong2.getText().toString().trim();
//                final String Wrong3 = wrong3.getText().toString().trim();
//                final String Latitude = latitude.getText().toString().trim();
//                final String Longitude = longitude.getText().toString().trim();
//
//                final RadioGroup group = dialogView.findViewById(R.id.radio_group);
//
//                int checkedRadioButtonId = group.getCheckedRadioButtonId();
//                if (checkedRadioButtonId == -1) {
//                    // No item selected
//                }
//                else{
//                    if (checkedRadioButtonId == R.id.radio_cpu)
//                    {
//                        // Do something with the button
//                        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                                "://" + getResources().getResourcePackageName(R.drawable.cpu) +
//                                '/' + getResources().getResourceTypeName(R.drawable.cpu) +
//                                '/' + getResources().getResourceEntryName(R.drawable.cpu) );
//                    }
//                    if(checkedRadioButtonId == R.id.radio_ram)
//                    {
//                        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                                "://" + getResources().getResourcePackageName(R.drawable.ram_memory) +
//                                '/' + getResources().getResourceTypeName(R.drawable.ram_memory) +
//                                '/' + getResources().getResourceEntryName(R.drawable.ram_memory) );
//                    }
//                    if (checkedRadioButtonId == R.id.radio_mother_board)
//                    {
//                        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                                "://" + getResources().getResourcePackageName(R.drawable.motherboard) +
//                                '/' + getResources().getResourceTypeName(R.drawable.motherboard) +
//                                '/' + getResources().getResourceEntryName(R.drawable.motherboard) );
//                    }
//                }
//
//                if (!TextUtils.isEmpty(Question) && !TextUtils.isEmpty(Answer) && !TextUtils.isEmpty(Wrong1) && !TextUtils.isEmpty(Wrong2)
//                        && !TextUtils.isEmpty(Wrong3) && !TextUtils.isEmpty(Latitude) && !TextUtils.isEmpty(Longitude) && imageUri != null)
//                {
//
//                    final StorageReference filepath = storageReference.child("Quiz").child("my_image_" + Timestamp.now().getSeconds());
//
//                    cLatitude = Double.parseDouble(Latitude);
//                    cLongitude = Double.parseDouble(Longitude);
//
//                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//
//                                    quiz_list = new ArrayList<>();
//
//                                    String imageUrl = uri.toString();
//
//                                    Quiz quiz = new Quiz();
//                                    quiz.setQuestion(Question);
//                                    quiz.setAnswer(Answer);
//                                    quiz.setWrong1(Wrong1);
//                                    quiz.setWrong2(Wrong2);
//                                    quiz.setWrong3(Wrong3);
//                                    quiz.setLatitude(cLatitude);
//                                    quiz.setLongitude(cLongitude);
//                                    quiz.setImageUrl(imageUrl);
//
//                                    quiz_list.add(quiz);
//
//                                    campus.push().setValue(quiz_list).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.d("success", " " + task);
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.d("error", " " + e);
//                                        }
//                                    });
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("error", " " + e);
//                                }
//                            });
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("error", " " + e);
//                        }
//                    });
//                }
//                alertDialog.hide();
//            }
//        });
//
//    }

    private void initArea() {

        campus = FirebaseDatabase.getInstance()
                .getReference("Quiz");

        listener = this;
        listener1 = this;
        campus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //update item area

                List<Quiz> images = new ArrayList<>();
                ArrayList<LatLng> markers = new ArrayList<>();
                String Iimage = "";
                for (DataSnapshot location : dataSnapshot.getChildren()) {
                    for (DataSnapshot snap : location.getChildren()) {

                        Quiz latLng = snap.getValue(Quiz.class);
                        Quiz image1 = snap.getValue(Quiz.class);

                        Iimage = image1.getImageUrl();

                        myLatitude = latLng.getLatitude();
                        myLongitude = latLng.getLongitude();
                        image = latLng.getImageUrl();
                        qQuestion = latLng.getQuestion();
                        qAnswer = latLng.getAnswer();
                        qWrong1 = latLng.getWrong1();
                        qWrong2 = latLng.getWrong2();
                        qWrong3 = latLng.getWrong3();


                        geofence = new LatLng(myLatitude, myLongitude);

                        markers.add(geofence);


                        latLngList.add(latLng);

                        images.add(image1);

                    }
                }


                if (latLngList.size()> 0){
                    listener.onLoadLocationSuccess(latLngList, images);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addUserMarker() {
        geoFire.setLocation("You", new GeoLocation(lastLocation.getLatitude(),
                lastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (currentUser != null)  currentUser.remove();
                currentUser = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lastLocation.getLatitude(),
                                lastLocation.getLongitude()))
                        .title("You")
                        .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_panda)));
                //after we add the marker, move camera
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(currentUser.getPosition(), 17.0f));
            }
        });
    }

    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(final LocationResult locationResult){
                if(mMap != null)
                {

                    lastLocation = locationResult.getLastLocation();

//                  Add user Marker
                    addUserMarker();
                }

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,R.raw.newstyle));
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed");
            }

        } catch(Resources.NotFoundException e) {
            Log.e("MapsActivity", "Style parsing failed", e);
        }

        if(fusedLocationProviderClient != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        addCircleArea();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(UserMapsActivity.this, "hey !!!!!", Toast.LENGTH_SHORT).show();

                showQuizDialog();

                return false;
            }
        });
    }

    private void showQuizDialog() {

        userDb = FirebaseDatabase.getInstance()
                .getReference("Invetory_for_" + cEmail);

        ViewGroup viewGroup = findViewById(android.R.id.content);

        quizView = LayoutInflater.from(this).inflate(R.layout.quiz_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(quizView);

        //finally creating the alert dialog and displaying it
        quizDialog = builder.create();
        quizDialog.show();

        CountDownTimer mCountDownTimer;
        TextView question = quizView.findViewById(R.id.question);
        ImageView imageView = quizView.findViewById(R.id.image);
        Button answer = quizView.findViewById(R.id.answer);
        Button wrong1 = quizView.findViewById(R.id.wrong1);
        Button wrong2 = quizView.findViewById(R.id.wrong2);
        Button wrong3 = quizView.findViewById(R.id.wrong3);

        if (latLngList.size()>0) {
            Picasso.get().load(latLngList.get(counter).getImageUrl()).resize(120, 128).into(imageView);
        }


        answer.setText(latLngList.get(counter).getAnswer());
        question.setText(latLngList.get(counter).getQuestion());
        wrong1.setText(latLngList.get(counter).getWrong1());
        wrong2.setText(latLngList.get(counter).getWrong2());
        wrong3.setText(latLngList.get(counter).getWrong3());



        wrong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizDialog.hide();

            }
        });

        wrong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizDialog.hide();
            }
        });

        wrong3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizDialog.hide();
            }
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userDb.push().child("image").setValue(latLngList.get(counter).getImageUrl());

                counter ++;
                quizDialog.hide();
                location_marker.setVisible(false);
            }
        });

        timer = quizView.findViewById(R.id.time);
        timer.setProgress(i);

        mCountDownTimer = new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
                timer.setProgress((int)i * 100 / (15000 / 1000));

            }

            @Override
            public void onFinish() {

                quizDialog.hide();
                i = 0;
                timer.setProgress(i);

            }
        };
        mCountDownTimer.start();
    }


    private void addCircleArea() {

        if (geoQuery != null)
        {
            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }
        for (final LatLng latLng: item_area) {
            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(15)// 15 meters
                    .fillColor(0x000000ff) //00 is transparent code
                    .strokeWidth(0.0f)
            );

            blank = true;

            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude), 0.015f); // 15 meters
            geoQuery.addGeoQueryEventListener(UserMapsActivity.this);

        }
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        sendNotification("Campus Geo Quiz", String.format("%s are near an item!!", key));

        markerOption = new MarkerOptions().position(new LatLng(location.latitude, location.longitude));
        location_marker = mMap.addMarker(markerOption);

        if (location.equals(markerOption)){
            location_marker.setVisible(true);
        }
        Target target = new PicassoMarker(location_marker);
        targets.add(target);
        Picasso.get().load(latLngList.get(counter).getImageUrl()).resize(120, 128).into(target);
    }

    @Override
    public void onKeyExited(String key) {
        sendNotification("Campus Geo Quiz", String.format("%s have to get the item!!", key));
        blank = false;
        location_marker.setVisible(false);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        sendNotification("Campus Geo Quiz", String.format("%s forgot the Item!!", key));
        blank = false;
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String title, String content)
    {
        Toast.makeText(this, ""+content, Toast.LENGTH_SHORT).show();

        String NOTIFICATION_CHANNEL_ID = "Campus Geo Quiz" ;
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            //configuration
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_panda)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_panda));

        Notification notification= builder.build();
        notificationManager.notify(new Random().nextInt(),notification);
    }

    @Override
    public void onLoadLocationSuccess(List<Quiz> latLngs, List<Quiz> images1) {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(UserMapsActivity.this);

        // clear map and add it again
        if (mMap != null)
        {
            Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
            mMap.clear();
            //add user location
            addUserMarker();
            // add circle of dangerous area
            addCircleArea();
        }

        item_area = new ArrayList<>();

        for (Quiz myLatLng : latLngs)
        {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            item_area.add(convert);
        }


        images = new ArrayList<>();

        for (Quiz Images: images1)
        {
            conv = new String(Images.getImageUrl());
            images.add(conv);
            Log.d("converted image", conv);
        }

    }

    @Override
    public void onLoadLocationFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

}
