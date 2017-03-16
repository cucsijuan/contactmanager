package com.cucsijuan.contactmanager;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CallFragment extends Fragment {
    TextView txtNumber;


    public CallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();




        txtNumber = (TextView)getActivity().findViewById(R.id.txt_number);
        Button btn0 = (Button)getActivity().findViewById(R.id.btn_0);
        Button btn1 = (Button)getActivity().findViewById(R.id.btn_1);
        Button btn2 = (Button)getActivity().findViewById(R.id.btn_2);
        Button btn3 = (Button)getActivity().findViewById(R.id.btn_3);
        Button btn4 = (Button)getActivity().findViewById(R.id.btn_4);
        Button btn5 = (Button)getActivity().findViewById(R.id.btn_5);
        Button btn6 = (Button)getActivity().findViewById(R.id.btn_6);
        Button btn7 = (Button)getActivity().findViewById(R.id.btn_7);
        Button btn8 = (Button)getActivity().findViewById(R.id.btn_8);
        Button btn9 = (Button)getActivity().findViewById(R.id.btn_9);
        Button btnSharp = (Button)getActivity().findViewById(R.id.btn_sharp);
        Button btnStar = (Button)getActivity().findViewById(R.id.btn_star);
        ImageButton btnErase = (ImageButton)getActivity().findViewById(R.id.btnErase);

        View.OnClickListener numListener = new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if (getActivity().findViewById(view.getId())== getActivity().findViewById(R.id.btnErase)){
                    if (txtNumber.length() <= 0){
                        //nothing
                    }else{
                        txtNumber.setText(txtNumber.getText().toString().substring(0,txtNumber.length()-1));
                    }
                }else{
                    Button tempBtn = (Button)getActivity().findViewById(view.getId());
                    if (txtNumber.equals(null)){
                        txtNumber.setText(txtNumber.getText().toString());
                    }else{
                        txtNumber.setText(txtNumber.getText().toString() + tempBtn.getText().toString());
                    }
                }
            }

        };

        btn0.setOnClickListener(numListener);
        btn1.setOnClickListener(numListener);
        btn2.setOnClickListener(numListener);
        btn3.setOnClickListener(numListener);
        btn4.setOnClickListener(numListener);
        btn5.setOnClickListener(numListener);
        btn6.setOnClickListener(numListener);
        btn7.setOnClickListener(numListener);
        btn8.setOnClickListener(numListener);
        btn9.setOnClickListener(numListener);
        btnSharp.setOnClickListener(numListener);
        btnStar.setOnClickListener(numListener);
        btnErase.setOnClickListener(numListener);

        getActivity().findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ txtNumber.getText().toString()));

                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

}