package com.example.inventairelol.Fragments;

import static android.content.Context.MODE_PRIVATE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.inventairelol.Activities.Login;
import com.example.inventairelol.Activities.MainActivity;
import com.example.inventairelol.Activities.Register;
import com.example.inventairelol.DataBase.SQLiteBDD;
import com.example.inventairelol.R;

import com.example.inventairelol.Util.Preferences.PreferenceUserRiot;
import com.example.inventairelol.Util.Preferences.PreferencesUser;

import java.util.Map;

import com.example.inventairelol.Service.ApiLocalisation;
import com.example.inventairelol.Service.BaseGpsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements BaseGpsListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    TextView textViewLocation ;


    ImageView imageViewm;
    private static final int PERMISSION_LOCATION = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Pour gerer l'img
        imageViewm = (ImageView) view.findViewById(R.id.imageView);
         textViewLocation = (TextView) view.findViewById(R.id.tv_location) ;

        //Pour gerer le text
        TextView textView = (TextView) view.findViewById(R.id.textViewSimple);

        //Pour gerer les boutton
        Button button = (Button) view.findViewById(R.id.goProfil);
        Button buttonGoregister = (Button) view.findViewById(R.id.goRegister);
        Button buttonLogout = (Button) view.findViewById(R.id.logOut);

        //Localisation
        TextView textViewLocalisation = (TextView) view.findViewById(R.id.tv_location);
        Button buttonLocalisation = (Button) view.findViewById(R.id.location);

        // on vérifie si on peut acceder à la localisation, si oui, on lance la methode showLocation
        buttonLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Check for location  permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
                }else{
                    showLocation();
                }
            }
        });


        //Vérification de la création d'une variable de connexion

        PreferenceUserRiot preferenceUserRiot = new PreferenceUserRiot(getActivity());
        Map<String, String> infosRiot = preferenceUserRiot.getUserInfo();
        PreferencesUser preferencesUser = new PreferencesUser(getActivity());
        Map<String, String> infosUser = preferencesUser.getUserInfo();

        String connected =  "false";
        if (infosUser.containsKey("connected")){
            connected = infosUser.get("connected");
        }

        //si l'utilisateur n'est pas connecté:
        String urlImg;
        if (connected.equals("true")) {

            Log.i("infosRiot", infosRiot.toString());

            String profileIconId = "", name = "", summonerLevel = "";
            if (infosRiot.containsKey("profileIconId")){
                profileIconId = infosRiot.get("profileIconId");
            }
            if (infosRiot.containsKey("name")){
                name = infosRiot.get("name");
            }
            if (infosRiot.containsKey("summonerLevel")){
                summonerLevel = infosRiot.get("summonerLevel");
            }

            urlImg = "http://ddragon.leagueoflegends.com/cdn/13.4.1/img/profileicon/" + profileIconId + ".png";
            textView.setText(name + "\n niveau: " + summonerLevel);

            buttonGoregister.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
        } else {
            urlImg = "https://militaryhealthinstitute.org/wp-content/uploads/sites/37/2021/08/blank-profile-picture-png.png";
            textView.setText(R.string.noConnected);
        }

        Glide.with(this).load(urlImg).into(imageViewm);


    //boutton pour la deconnexion

    buttonLogout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pseudo", "");
            editor.putString("password", "");
            editor.apply();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }
    });


        buttonGoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults){
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showLocation();
        }else {
            Toast.makeText(getActivity(), "permission not granted!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    //Methode pour
    @SuppressLint("MissingPermission")
    private void showLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Check if gps is enable
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            textViewLocation.setText("Loading location..");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100L, (float) 100, this);

        }else{
            Toast.makeText(getActivity(), "Enable GPS! ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


    private String hereLocation(Location location)  {

        String response = "";
        String lattitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        String apiKeyLocalisation = "8ceaa7aed357415d5728b70c95b14e7f";
        String url = "http://api.openweathermap.org/geo/1.0/reverse?lat=" + lattitude +"&lon="+ longitude +"&limit=100&appid=" + apiKeyLocalisation;
        ApiLocalisation apiLocalisation = new ApiLocalisation();
        apiLocalisation.execute(url);
        try {
            JSONArray jsonObject = null;
            JSONObject data = null;
            response = apiLocalisation.get();
            jsonObject = new JSONArray(response);
            data = jsonObject.getJSONObject(0);
            String value = null;
            value = data.getString("name");
            return "City: " + value;
        } catch (ExecutionException|JSONException|InterruptedException e) {
            e.printStackTrace();
            return "Error : No return from api";
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        //update location
            textViewLocation.setText(hereLocation(location));

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String probider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}

