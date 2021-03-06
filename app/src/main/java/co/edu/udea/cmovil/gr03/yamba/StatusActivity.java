package co.edu.udea.cmovil.gr03.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;


public class StatusActivity extends Activity {


    //Defino los objetos que voy a utilizar
    private static final String TAG = StatusActivity.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //Inicializo los componentes
        mButtonTweet = (Button) findViewById(R.id.status_button_tweet);
        mTextStatus = (EditText) findViewById(R.id.status_text);
        mTextCount = (TextView) findViewById(R.id.status_text_count);

        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();
        mTextStatus.addTextChangedListener(textWatcher);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
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

        mButtonTweet = (Button) findViewById(R.id.status_button_tweet);
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            progress = new ProgressDialog(StatusActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setTitle("Posteando");
            progress.setMessage("Enviando---");
            progress.show();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                YambaClient cloud = new YambaClient("student", "password");
                cloud.postStatus(params[0]);
                Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
                respuesta = "Successfully posted";
                return "Successfully posted";


            } catch (YambaClientException e) {
                Log.e(TAG, "Failed to post to the cloud", e);

                e.printStackTrace();
                respuesta = "Failed to post";
                return "Failed to post";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(StatusActivity.this, respuesta, Toast.LENGTH_LONG).show();
            mTextStatus.setText("");

        }
    }
}
