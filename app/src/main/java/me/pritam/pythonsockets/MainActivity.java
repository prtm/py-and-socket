package me.pritam.pythonsockets;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private String dataString;
    private TextView dataReceive;
    private List<String> pidList = new ArrayList<>();
    private List<String> processList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText) findViewById(R.id.hello);
        dataReceive = (TextView) findViewById(R.id.socketDataReceived);
        dataReceive.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendMessage().execute(editText.getText().toString());
            }
        });
    }

    private void setList(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class SendMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            try {
                Socket socket = new Socket("192.168.10.89", 8888);
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                printWriter.print(strings[0]);
                printWriter.flush();
                Log.d("prtm", "Print writer flushed");

                final byte[] messageByte = new byte[1000];
                boolean end = false;
                final StringBuilder dataString = new StringBuilder();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                dataString.append(new String(messageByte, 0, in.read(messageByte)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.dataString += "\n" + dataString.toString();
                        dataReceive.setText(MainActivity.this.dataString);
                        for (String s : dataString.toString().split("\n")) {
                            Log.d("prtm", "prtm: :" + s);
                            pidList.add(s.split(",")[0]);
                            processList.add(s.split(",")[1]);
                        }
                        pidList.remove(0);
                        processList.remove(0);
                        Log.d("prtm", pidList.toString());
                        Log.d("prtm", processList.toString());
                    }
                });

//                DataInputStream in = new DataInputStream(socket.getInputStream());
//
//                while (!end) {
//                    final int bytesRead = in.read(messageByte);
//                    dataString.append(new String(messageByte, 0, bytesRead));
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pidList.add(new String(messageByte, 0, bytesRead));
//                            processList.add(new String(messageByte, 0, bytesRead));
//                        }
//                    });
//                    if (dataString.toString().length() == 1000) {
//                        end = true;
//                    }
//                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
