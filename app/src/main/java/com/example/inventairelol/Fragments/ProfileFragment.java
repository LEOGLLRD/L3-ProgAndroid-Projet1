package com.example.inventairelol.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.inventairelol.Activities.Login;
import com.example.inventairelol.Activities.MainActivity;
import com.example.inventairelol.Activities.Register;
import com.example.inventairelol.DataBase.SQLiteBDD;
import com.example.inventairelol.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


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




    ImageView imageViewm;



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

        //Pour gerer le text
        TextView textView =(TextView) view.findViewById(R.id.textViewSimple);

        //Pour gerer les boutton
        Button button = (Button) view.findViewById(R.id.goProfil);
        Button buttonGoregister = (Button) view.findViewById(R.id.goRegister);

        //Vérification de la création d'une variable de connexion
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String isConnected = sharedPreferences.getString("connected", "false");

        //si l'utilisateur n'est pas connecté:
        String urlImg ;
        if(isConnected.equals("true")){
            SharedPreferences sharedPreferencesLol = getActivity().getSharedPreferences("accountLolRiot", Context.MODE_PRIVATE);
            String profileIconIdRiot = sharedPreferencesLol.getString("profileIconIdRiot","false");
            String name = sharedPreferencesLol.getString("nameRiot", "false");
            urlImg = "http://ddragon.leagueoflegends.com/cdn/13.4.1/img/profileicon/"+ profileIconIdRiot + ".png";
            String summonerLevelRiot = sharedPreferencesLol.getString("summonerLevelRiot","false");
            textView.setText(name + "\n niveau: " + summonerLevelRiot );
            buttonGoregister.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
        }else{
            urlImg ="https://militaryhealthinstitute.org/wp-content/uploads/sites/37/2021/08/blank-profile-picture-png.png";
            textView.setText(R.string.noConnected);
        }

        Glide.with(this).load(urlImg).into(imageViewm);




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


}

