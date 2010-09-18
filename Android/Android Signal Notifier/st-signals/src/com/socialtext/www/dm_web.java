package com.socialtext.www;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class dm_web extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm_svc.msg_count=0;
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(dm_svc.server +"/m/signals")));
        finish();
	}
	
}