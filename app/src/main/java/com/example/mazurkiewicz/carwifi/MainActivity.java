package com.example.mazurkiewicz.carwifi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.ServerSocket;


public class MainActivity extends ActionBarActivity {
    private Button forward;
    private Button back;
    private Button right;
    private Button left;
    private TextView info;

    // Importing also other views
    private JoystickView joystick;
    private TextView angleTextView;
    private TextView powerTextView;
    private TextView directionTextView;

    ServerTCP serverTCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forward  = (Button) findViewById(R.id.forward);
        back  = (Button) findViewById(R.id.back);
        right  = (Button) findViewById(R.id.right);
        left  = (Button) findViewById(R.id.left);

        info = (TextView) findViewById(R.id.infoTCP);

        forward.setOnTouchListener(forwardTouch);
        back.setOnTouchListener(backTouch);
        left.setOnTouchListener(leftTouch);
        right.setOnTouchListener(rightTouch);

        serverTCP = new ServerTCP();
        info.setText(serverTCP.getIpAddress() + ":" + serverTCP.getLocalPort());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverTCP.DestroyServer();
    }

    private boolean listCommand(String command,MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            System.out.println(command);
            serverTCP.sendAllClient(command);
        }
        else if (action == MotionEvent.ACTION_UP) {
            System.out.println("end press");
            serverTCP.sendAllClient("e");
        }

        return false;   //  the listener has NOT consumed the event, pass it on
    }

    private View.OnTouchListener forwardTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listCommand("forward",event);
        }
    };

    private View.OnTouchListener backTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listCommand("back",event);
        }
    };

    private View.OnTouchListener leftTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listCommand("turnLeft",event);
        }
    };

    private View.OnTouchListener rightTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listCommand("turnRight",event);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
