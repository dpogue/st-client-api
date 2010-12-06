package signals.com.socialtext.www;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class dm_settings.
 */
public class dm_settings extends Activity implements OnClickListener {

	/** The Constant PREFS_NAME. */
	private static final String PREFS_NAME = "dm_settings";
	
	/** The server. */
	private String server;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The ownmsg. */
	private boolean ownmsg;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dm_settings);

		Button btnOk = (Button) findViewById(R.id.btn_ok);
		Button btnCancel = (Button) findViewById(R.id.btn_cancel);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		getSettings();

	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:

			TextView text_url = (TextView) findViewById(R.id.textbox_url);
			TextView text_user = (TextView) findViewById(R.id.textbox_user);
			TextView text_pass = (TextView) findViewById(R.id.textbox_password);
			CheckBox chkbx_own = (CheckBox) findViewById(R.id.chkbx_own);

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();

			editor.putString("url", text_url.getText().toString());
			editor.putString("username", text_user.getText().toString());
			editor.putString("password", text_pass.getText().toString());
			editor.putBoolean("ownmsg", chkbx_own.isChecked());
			editor.putBoolean("open_first", true);
			editor.commit();			
			finish();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	public void getSettings() {

		TextView text_url = (TextView) findViewById(R.id.textbox_url);
		TextView text_user = (TextView) findViewById(R.id.textbox_user);
		TextView text_pass = (TextView) findViewById(R.id.textbox_password);
		CheckBox chkbx_own = (CheckBox) findViewById(R.id.chkbx_own);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		server = settings.getString("url", "https://www.socialtext.com");
		username = settings.getString("username", "Username");
		password = settings.getString("password", "password");
		ownmsg = settings.getBoolean("ownmsg", false);

		text_url.setText(server);
		text_user.setText(username);
		text_pass.setText(password);
		chkbx_own.setChecked(ownmsg);
		

	}

}