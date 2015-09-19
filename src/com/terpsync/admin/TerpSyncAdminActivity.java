package com.terpsync.admin;

import java.util.List;
import java.util.Locale;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.R.string;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TerpSyncAdminActivity extends Activity {

	Button addButton;
	EditText mUser, mPass, mOrg;
	AdminAccounts newAdmin;
	boolean bad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		addButton = (Button) findViewById(R.id.addButton);
		mUser = (EditText) findViewById(R.id.editUser);
		mPass = (EditText) findViewById(R.id.editPass);
		mOrg = (EditText) findViewById(R.id.editOrg);

		Parse.initialize(this, this.getString(R.string.parse_app_id),
				this.getString(R.string.parse_client_key));

		ParseObject.registerSubclass(AdminAccounts.class);

		newAdmin = new AdminAccounts();

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bad = false;

				final String UN = mUser.getEditableText().toString().toLowerCase(Locale.US).trim();

				// Check if account already exists
				ParseQuery<AdminAccounts> query = ParseQuery.getQuery(AdminAccounts.class);
				query.whereEqualTo("username", mUser);
				query.setLimit(1);
				query.findInBackground(new FindCallback<AdminAccounts>() {
					@Override
					public void done(List<AdminAccounts> arg0, ParseException arg1) {
						if (arg1 == null && arg0 != null && arg0.size() > 0
								&& UN.equals(arg0.get(0))) {
							Toast.makeText(getApplicationContext(), "Account already exists",
									Toast.LENGTH_SHORT).show();
							bad = true;
						}
					}
				});

				// If good, go ahead and save
				if (!bad) {
					newAdmin.setUsername(UN);
					newAdmin.setPassword(mPass.getText().toString());
					newAdmin.setOrganizationName(mOrg.getText().toString());

					newAdmin.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException arg0) {
							Toast.makeText(
									getApplicationContext(),
									"Account created for: " + newAdmin.getOrganizatonName() + "\n"
											+ UN + "\n" + newAdmin.getPassword(),
									Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
