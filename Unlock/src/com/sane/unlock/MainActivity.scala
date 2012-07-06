package com.sane.unlock;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

class MainActivity extends Activity {

    override def onCreate(savedInstanceState:Bundle )= {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    
    override def onCreateOptionsMenu(menu:Menu):Boolean ={
        getMenuInflater().inflate(R.menu.activity_main, menu);
        true;
    }

    
}
