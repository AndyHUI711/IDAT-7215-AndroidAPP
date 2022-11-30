package com.example.imagerecognitiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity implements OnClickListener{
    private EditText  input;
    private Button enter;
    //private EditText  input2;
    private EditText  input3;
    private Button reset;
    private Spinner spinner;
    private Switch switch1;
    private SeekBar seekBar;
    private TextView threshold_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initView();

        enter.setOnClickListener(this);
        reset.setOnClickListener(this);
        switch1.setOnClickListener(this);
        seekBar.setOnClickListener(this);

    }

    private void initView()
    {
        input=(TextInputEditText) findViewById(R.id.input);
        //input2=(TextInputEditText) findViewById(R.id.input2);
        input3=(TextInputEditText) findViewById(R.id.input3);
        enter=(Button) findViewById(R.id.enter);
        reset=(Button) findViewById(R.id.reset);
        spinner = findViewById(R.id.spinner);
        switch1 = (Switch) findViewById(R.id.switch1);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        threshold_text = (TextView) findViewById(R.id.textNUM);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.threshold = seekBar.getProgress();
                Log.i("threshold", String.valueOf(MainActivity.threshold));
                double thre = (float)MainActivity.threshold/(float)10;
                threshold_text.setText(String.format("%.2f", thre));
                Log.i("log","当前进度值:" + progress + "  / 10 ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MainActivity.threshold = seekBar.getProgress();
                Log.i("threshold", String.valueOf(MainActivity.threshold));
                double thre = (float)MainActivity.threshold/(float)10;
                threshold_text.setText(String.format("%.2f", thre));
                Log.i("log", "触碰SeekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity.threshold = seekBar.getProgress();
                Log.i("threshold", String.valueOf(MainActivity.threshold));
                double thre = (float)MainActivity.threshold/(float)10;
                threshold_text.setText(String.format("%.2f", thre));
                Log.i("log", "放开SeekBar");
            }
        });


        String[] items = getResources().getStringArray(R.array.spinnerclass);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String adapter2=MainActivity.model_type;
        spinner.setAdapter(adapter);
        input.setText(MainActivity.URL);
        //input2.setText(MainActivity.model_type);
        input3.setText(MainActivity.function);
        switch1.setChecked(MainActivity.show_per);
        seekBar.setProgress(MainActivity.threshold);
        double thre = (float)MainActivity.threshold/(float)10;
        threshold_text.setText(String.format("%.2f", thre));
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.enter:
                //Toast.makeText(this, input.getText().toString(), Toast.LENGTH_LONG).show();
                //Log.e("URL", input.getText().toString());
                if(input.getText().toString().startsWith("http://")){
                MainActivity.URL = input.getText().toString();}
                else MainActivity.URL = "https://356ac17611.goho.co/detect/";

                MainActivity.model_type = spinner.getSelectedItem().toString();
                //MainActivity.model_type = input2.getText().toString();
                MainActivity.function = input3.getText().toString();


                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.reset:
                input.setText("https://356ac17611.goho.co/detect/");
                //input2.setText("general");
                input3.setText("POST");
                switch1.setChecked(Boolean.parseBoolean("true"));
                seekBar.setProgress(5);
                threshold_text.setText("0.50");

                break;
            case R.id.switch1:
                if(MainActivity.show_per == Boolean.TRUE) {
                    MainActivity.show_per = Boolean.FALSE;
                    Log.e("show percecnt",MainActivity.show_per.toString());
                }
                else{
                    MainActivity.show_per = Boolean.TRUE;
                    Log.e("show percecnt",MainActivity.show_per.toString());}
                break;

            case R.id.seekBar:

                break;


            default:
                break;
        }
    }

}