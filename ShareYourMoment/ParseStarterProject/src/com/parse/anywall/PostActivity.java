package com.parse.anywall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class PostActivity extends AppCompatActivity {
  // UI references.
  private EditText postEditText;
  private TextView characterCountTextView;

  private String data;

  private Button postButton;

  private int maxCharacterCount = Application.getConfigHelper().getPostMaxCharacterCount();
  private ParseGeoPoint geoPoint;
  public String []mydata1={"0","1","2"};
  public Integer[]images={R.drawable.ic_1,R.drawable.ic_2, R.drawable.ic_3};



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_post);

    Spinner feeling = (Spinner)findViewById(R.id.spinner1);
    feeling.setAdapter(new MyAdapter(this, R.layout.custom_spinner, mydata1));



    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    //actionBar.setDisplayHomeAsUpEnabled(true); //homebutton
    actionBar.setIcon(R.drawable.ic_launcher);

    initFab();

    Intent intent = getIntent();
    Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
    geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

    postEditText = (EditText) findViewById(R.id.post_edittext);
    postEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        updatePostButtonState();
        updateCharacterCountTextViewText();
      }
    });

    characterCountTextView = (TextView) findViewById(R.id.character_count_textview);

    updatePostButtonState();
    updateCharacterCountTextViewText();
  }

  private void initFab() {
    findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        post();
      }
    });
  }

  public class MyAdapter extends ArrayAdapter<String>
  {

    public MyAdapter(Context context, int textViewResourceId, String[] objects)
    {
      super(context, textViewResourceId, objects);
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent)
    {
      return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {

      LayoutInflater inflater=getLayoutInflater();
      View row=inflater.inflate(R.layout.custom_spinner, parent, false);

      TextView label=(TextView)row.findViewById(R.id.textView1);
      label.setText(mydata1[position]);

     // label.setVisibility(View.INVISIBLE);
      data = mydata1[position];

      ImageView icon=(ImageView)row.findViewById(R.id.imageView1);
      icon.setImageResource(images[position]);

      return row;
    }

  }

  private void post () {
    String text = postEditText.getText().toString().trim();

    // Set up a progress dialog
    final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
    dialog.setMessage(getString(R.string.progress_post));
    dialog.show();

    // Create a post.
    AnywallPost post = new AnywallPost();

    // Set the location to the current user's location
    post.setLocation(geoPoint);
    post.setText(text);
    post.setUser(ParseUser.getCurrentUser());

    post.setFeeling(String.valueOf(data)); //Paramos aqui

    ParseACL acl = new ParseACL();

    // Give public read access
    acl.setPublicReadAccess(true);
    post.setACL(acl);

    // Save the post
    post.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        dialog.dismiss();
        finish();
      }
    });
  }

  private String getPostEditTextText () {
    return postEditText.getText().toString().trim();
  }

  private void updatePostButtonState () {
    int length = getPostEditTextText().length();
    boolean enabled = length > 0 && length < maxCharacterCount;
   // postButton.setEnabled(enabled);
  }

  private void updateCharacterCountTextViewText () {
    String characterCountString = String.format("%d/%d", postEditText.length(), maxCharacterCount);
    characterCountTextView.setText(characterCountString);
  }
}
