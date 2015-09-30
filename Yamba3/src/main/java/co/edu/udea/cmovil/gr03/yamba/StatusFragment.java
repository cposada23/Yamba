package co.edu.udea.cmovil.gr03.yamba;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    //Defino los objetos que voy a utilizar
    private static final String TAG = StatusActivity.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;


    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_status, container, false);
        mButtonTweet = (Button) v.findViewById(R.id.status_button_tweet);
        mTextStatus = (EditText) v.findViewById(R.id.status_text);
        mTextCount = (TextView) v.findViewById(R.id.status_text_count);

        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();
        mTextStatus.addTextChangedListener(textWatcher);
        mButtonTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String status = mTextStatus.getText().toString();
                PostTask postTask = new PostTask();
                postTask.execute(status);
                hideKeyboard();
                Log.d(TAG, "onClicked");
            }

        });

        return v;
    }




    ///cosas que agrego
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
            //updateCounter(cs);
            mTextCount.setText(String.valueOf(140 - cs.length()));
            if ((140 - cs.length()) < 0) {
                mTextCount.setTextColor(Color.RED);
            } else {
                mTextCount.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    //Activa y desactiva el boton mButtonTweet
    private void checkFieldsForEmptyValues() {


        String s1 = mTextStatus.getText().toString();

        if (s1.equals("")) {
            mButtonTweet.setEnabled(false);
        } else {
            mButtonTweet.setEnabled(true);
        }
    }
    //Desactiva el teclado
    private void hideKeyboard() {
        // Check if no view has focus:

        // Check if no view has focus:
        View view = this.getView();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    //metodo para el boton mButtonTweet
    public void onClickPost(View view) {
        String status = mTextStatus.getText().toString();
        Log.d("Texto", status);
        PostTask postTask = new PostTask();
        Log.d("POST", "Va a comenzar");
        postTask.execute(status);
        Log.d(TAG, "Onclicked");
        hideKeyboard();



    }



    //clase asyncrona para pode publicar en yamba

    private class PostTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progress;
        private String respuesta;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(getActivity());
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setTitle("Posteando");
            progress.setMessage("Enviando---");
            progress.show();


        }

        @Override
        protected String doInBackground(String... params) {
            try {

                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                // Check that username and password are not empty
                // If empty, Toast a message to set login info and bounce to
                // SettingActivity
                // Hint: TextUtils.
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    getActivity().startActivity(
                            new Intent(getActivity(), SettingsActivity.class));
                    return "Please update your username and password";
                }

                YambaClient cloud = new YambaClient(username, password);
                cloud.postStatus(params[0]);

                Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
                respuesta = "Successfully posted";
                return "Successfully posted";
            } catch (Exception e) {
                Log.e(TAG, "Failed to post to the cloud", e);
                e.printStackTrace();
                respuesta = "Failed to post";
                return "Failed to post";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(getActivity(), respuesta, Toast.LENGTH_LONG).show();
            mTextStatus.setText("");

        }
    }


}
