package com.socialtext.www;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class dm_settings extends Activity implements OnClickListener {
	
	private static final String PREFS_NAME = "dm_settings";
	private String server;
	private String username;
	private String password;
	private int poll;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dm_settings);
        
		Spinner spinner_poll = (Spinner) findViewById(R.id.spinner_poll);

		Button btnOk = (Button)findViewById(R.id.btn_ok);
		Button btnCancel = (Button)findViewById(R.id.btn_cancel);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ping_int, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_poll.setAdapter(adapter);

		getSettings();

	}

    @Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_ok:

			Spinner spinner_poll = (Spinner) findViewById(R.id.spinner_poll);
			TextView text_url = (TextView) findViewById(R.id.textbox_url);
			TextView text_user = (TextView) findViewById(R.id.textbox_user);
			TextView text_pass = (TextView) findViewById(R.id.textbox_password);

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			
			editor.putString("url", text_url.getText().toString());
			editor.putInt("poll", spinner_poll.getSelectedItemPosition());
			editor.putString("username", text_user.getText().toString());
			editor.putString("password", text_pass.getText().toString());
			
			editor.commit();
			
			startService(new Intent(dm_settings.this, dm_svc.class));

			finish();
			break;
		case R.id.btn_cancel:
			dm_svc.r=false;
			finish();
			break;
		}
	}
    
	public void getSettings(){
		
		Spinner spinner_poll = (Spinner) findViewById(R.id.spinner_poll);
		TextView text_url = (TextView) findViewById(R.id.textbox_url);
		TextView text_user = (TextView) findViewById(R.id.textbox_user);
		TextView text_pass = (TextView) findViewById(R.id.textbox_password);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		server = settings.getString("url", "https://www.socialtext.com");
		username = settings.getString("username", "Username");
		password = settings.getString("password", "password");
		poll = settings.getInt("poll", 3);
		
		
		text_url.setText(server);
		text_user.setText(username);
		text_pass.setText(password);
		spinner_poll.setSelection(poll);
		
	}

}