package dev.team.gradius.hackathon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
       /* ServerDataReadWriter task = new ServerDataReadWriter(this);
        task.execute();*/
    }
}