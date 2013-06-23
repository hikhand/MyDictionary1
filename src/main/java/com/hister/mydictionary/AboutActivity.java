package com.hister.mydictionary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

<<<<<<< HEAD:src/main/java/com/hister/mydictionary/AboutActivity.java
<<<<<<< HEAD:src/main/java/com/hister/mydictionarypro/AboutActivity.java
import com.hister.mydictionarypro.R;

=======
>>>>>>> parent of 739692b... 2.0.1:src/main/java/com/hister/mydictionary/AboutActivity.java
=======
>>>>>>> parent of 739692b... 2.0.1:src/main/java/com/hister/mydictionary/AboutActivity.java
/**
 * Created by khaled on 6/17/13.
 */

public class AboutActivity extends Activity {
    TextView tvSiteUrl;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        tvSiteUrl = (TextView) findViewById(R.id.tvSiteUrl);

    }

    public void linkToSite(View view) {

    }
}