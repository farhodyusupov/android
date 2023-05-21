package flwr.android_client;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import flwr.android_client.FlowerServiceGrpc.FlowerServiceStub;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private EditText ip;
    private EditText port;
    private Button loadDataButton;
    private Button connectButton;
    private Button trainButton;
    private TextView resultText;
    private EditText device_id;
    private ManagedChannel channel;
    public FlowerClient fc;
    private static final String TAG = "Flower";

    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    int round_cound  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultText = (TextView) findViewById(R.id.grpc_response_text);
        resultText.setMovementMethod(new ScrollingMovementMethod());
        device_id = (EditText) findViewById(R.id.device_id_edit_text);
        ip = (EditText) findViewById(R.id.serverIP);
        port = (EditText) findViewById(R.id.serverPort);
        loadDataButton = (Button) findViewById(R.id.load_data);
        connectButton = (Button) findViewById(R.id.connect);
        trainButton = (Button) findViewById(R.id.trainFederated);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circle_process);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        tv = (TextView) findViewById(R.id.tv);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 30));
        entries.add(new Entry(3, 40));
        entries.add(new Entry(4, 45));

        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry((float) 0.6, 8));
        entries1.add(new Entry((float) 0.9, 14));
        entries1.add(new Entry((float) 1.2, 12));
        entries1.add(new Entry((float) 1.7, 22));
        entries1.add(new Entry((float) 2.1, 32));

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry((float) 0.2, 5));
        entries2.add(new Entry((float) 1.1, 11));
        entries2.add(new Entry((float) 1.6, 18));
        entries2.add(new Entry((float) 2.9, 25));
        entries2.add(new Entry((float) 3.5, 32));

        List<Entry> entries3 = new ArrayList<>();
        entries3.add(new Entry((float) 0.3, 7));
        entries3.add(new Entry((float) 1.4, 17));
        entries3.add(new Entry((float) 1.7, 23));
        entries3.add(new Entry((float) 2.2, 29));
        entries3.add(new Entry((float) 3.1, 34));
        LineDataSet dataSet = new LineDataSet(entries, "Training loss");
        LineDataSet dataSet1 = new LineDataSet(entries, "Accuracy");
        LineDataSet dataSet2 = new LineDataSet(entries, "Test loss");
        LineDataSet dataSet3 = new LineDataSet(entries, "Training");
        LineData lineData = new LineData(dataSet);
        LineData lineData1 = new LineData(dataSet1);
        LineData lineData2 = new LineData(dataSet2);
        LineData lineData3 = new LineData(dataSet3);
//        LineChart lineChart = findViewById(R.id.lineChart);
//        lineChart.setData(lineData);
//        lineChart.invalidate();
//
//        LineChart lineChart1 = findViewById(R.id.lineChart1);
//        lineChart1.setData(lineData1);
//        lineChart1.invalidate();
//
//        LineChart lineChart2 = findViewById(R.id.lineChart2);
//        lineChart2.setData(lineData2);
//        lineChart2.invalidate();
//
//        LineChart lineChart3 = findViewById(R.id.lineChart3);
//        lineChart3.setData(lineData3);
//        lineChart3.invalidate();


//        dataSet.notifyDataSetChanged();
//        lineData.notifyDataChanged();
//        lineChart.notifyDataSetChanged();
//        lineChart.invalidate();
//
//        lineChart1.notifyDataSetChanged();
//        lineChart1.invalidate();
//        lineData1.notifyDataChanged();
//        dataSet1.notifyDataSetChanged();
//
//        lineChart2.notifyDataSetChanged();
//        lineChart2.invalidate();
//        lineData2.notifyDataChanged();
//        dataSet2.notifyDataSetChanged();
//
//        lineChart3.notifyDataSetChanged();
//        lineChart3.invalidate();
//        lineData3.notifyDataChanged();
//        dataSet3.notifyDataSetChanged();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                while (pStatus < 100) {
//                    pStatus += 1;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            mProgress.setProgress(pStatus);
//                            tv.setText(pStatus + "%");
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        // Just to display the progress slowly
//                        Thread.sleep(5000); //thread will take approx 3 seconds to finish
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        fc = new FlowerClient(this);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void setResultText(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
        String time = dateFormat.format(new Date());
        resultText.append("\n" + time + "   " + text);
    }

    public void loadData(View view) {
        if (TextUtils.isEmpty(device_id.getText().toString())) {
            Toast.makeText(this, "Please enter a client partition ID between 1 and 10 (inclusive)", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(device_id.getText().toString()) > 10 || Integer.parseInt(device_id.getText().toString()) < 1) {
            Toast.makeText(this, "Please enter a client partition ID between 1 and 10 (inclusive)", Toast.LENGTH_LONG).show();
        } else {
            hideKeyboard(this);
            setResultText("Loading the local training dataset in memory. It will take several seconds.");
            loadDataButton.setEnabled(false);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                private String result;

                @Override
                public void run() {
                    try {
                        fc.loadData(Integer.parseInt(device_id.getText().toString()));
                        result = "Training dataset is loaded in memory.";
                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        pw.flush();
                        result = "Training dataset is loaded in memory.";
                    }
                    handler.post(() -> {
                        setResultText(result);
                        connectButton.setEnabled(true);
                    });
                }
            });
        }
    }

    public void connect(View view) {
        String host = ip.getText().toString();
        String portStr = port.getText().toString();
        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(portStr) || !Patterns.IP_ADDRESS.matcher(host).matches()) {
            Toast.makeText(this, "Please enter the correct IP and port of the FL server", Toast.LENGTH_LONG).show();
        } else {
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.parseInt(portStr);
            channel = ManagedChannelBuilder.forAddress(host, port).maxInboundMessageSize(10 * 1024 * 1024).usePlaintext().build();
            hideKeyboard(this);
            trainButton.setEnabled(true);
            connectButton.setEnabled(false);
            setResultText("Channel object created. Ready to train!");
        }
    }

    public void runGrpc(View view) {
        MainActivity activity = this;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            private String result;

            @Override
            public void run() {
                try {
                    (new FlowerServiceRunnable()).run(FlowerServiceGrpc.newStub(channel), activity);
                    result = "Connection to the FL server successful \n";
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    pw.flush();
                    result = "Failed to connect to the FL server \n" + sw;
                }
                handler.post(() -> {
                    setResultText(result);
                    trainButton.setEnabled(false);
                });
            }
        });
    }

    public void restart(View view) {
        loadDataButton.setEnabled(true);
        connectButton.setEnabled(false);
        trainButton.setEnabled(false);
        port.setText("");
        ip.setText("");
        device_id.setText("");
        resultText.setText("");
        fc = null;
    }


    private static class FlowerServiceRunnable {
        protected Throwable failed;
        private StreamObserver<ClientMessage> requestObserver;

        public void run(FlowerServiceStub asyncStub, MainActivity activity) {
            join(asyncStub, activity);
        }

        private void join(FlowerServiceStub asyncStub, MainActivity activity)
                throws RuntimeException {


            final CountDownLatch finishLatch = new CountDownLatch(1);
            Log.e(TAG, "cound::" + finishLatch.getCount());
            requestObserver = asyncStub.join(
                    new StreamObserver<ServerMessage>() {

                        @Override
                        public void onNext(ServerMessage msg) {
                            handleMessage(msg, activity);

                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            failed = t;
                            finishLatch.countDown();
                            activity.setResultText("Finished and Connection channel closed");
//                            Log.e(TAG, t.getCause().toString());
                        }

                        @Override
                        public void onCompleted() {
                            finishLatch.countDown();
                            activity.setResultText("done");
                            Log.e(TAG, "Done");
                        }
                    });
        }

        private void handleMessage(ServerMessage message, MainActivity activity) {
                Log.e(TAG, "server messages ::"+message.toString());
            try {
                ByteBuffer[] weights;
                ClientMessage c = null;

                if (message.hasGetParametersIns()) {
                    Log.e(TAG, "Handling GetParameters");
                    activity.setResultText("Handling GetParameters message from the server.");

                    weights = activity.fc.getWeights();
                    c = weightsAsProto(weights);
                } else if (message.hasFitIns()) {
                    Log.e(TAG, "Handling FitIns");
                    activity.setResultText("Handling Fit request from the server.");

                    List<ByteString> layers = message.getFitIns().getParameters().getTensorsList();

                    Scalar epoch_config = message.getFitIns().getConfigMap().getOrDefault("local_epochs", Scalar.newBuilder().setSint64(1).build());
                    Scalar num_rounds = message.getFitIns().getConfigMap().getOrDefault("num_rounds", Scalar.newBuilder().setSint64(1).build());

                    assert epoch_config != null;
                    assert num_rounds != null;
                    int local_epochs = (int) epoch_config.getSint64();
                    int training_rounds = (int) num_rounds.getSint64();
                    Log.e(TAG, String.valueOf(local_epochs));
                    Log.e(TAG, "trainingrounds::"+String.valueOf(training_rounds));
                    // Our model has 10 layers
                    ByteBuffer[] newWeights = new ByteBuffer[10];
                    for (int i = 0; i < 10; i++) {
                        newWeights[i] = ByteBuffer.wrap(layers.get(i).toByteArray());
                    }

                    Pair<ByteBuffer[], Integer> outputs = activity.fc.fit(newWeights, local_epochs, training_rounds);
                    c = fitResAsProto(outputs.first, outputs.second);
                } else if (message.hasEvaluateIns()) {
                    Log.e(TAG, "Handling EvaluateIns");
                    activity.setResultText("Handling Evaluate request from the server");

                    List<ByteString> layers = message.getEvaluateIns().getParameters().getTensorsList();

                    // Our model has 10 layers
                    ByteBuffer[] newWeights = new ByteBuffer[10];
                    for (int i = 0; i < 10; i++) {
                        newWeights[i] = ByteBuffer.wrap(layers.get(i).toByteArray());
                    }
                    Pair<Pair<Float, Float>, Integer> inference = activity.fc.evaluate(newWeights);

                    float loss = inference.first.first;
                    float accuracy = inference.first.second;
                    activity.setResultText("Test Accuracy after this round = " + accuracy);
                    int test_size = inference.second;
                    c = evaluateResAsProto(loss, test_size);
                }
                requestObserver.onNext(c);
                activity.setResultText("Response sent to the server");
            } catch (Exception e) {
                Log.e(TAG, "error");
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private static ClientMessage weightsAsProto(ByteBuffer[] weights) {
        List<ByteString> layers = new ArrayList<>();
        for (ByteBuffer weight : weights) {
            layers.add(ByteString.copyFrom(weight));
        }
        Parameters p = Parameters.newBuilder().addAllTensors(layers).setTensorType("ND").build();
        ClientMessage.GetParametersRes res = ClientMessage.GetParametersRes.newBuilder().setParameters(p).build();
        return ClientMessage.newBuilder().setGetParametersRes(res).build();
    }

    private static ClientMessage fitResAsProto(ByteBuffer[] weights, int training_size) {
        List<ByteString> layers = new ArrayList<>();
        for (ByteBuffer weight : weights) {
            layers.add(ByteString.copyFrom(weight));
        }
        Parameters p = Parameters.newBuilder().addAllTensors(layers).setTensorType("ND").build();
        ClientMessage.FitRes res = ClientMessage.FitRes.newBuilder().setParameters(p).setNumExamples(training_size).build();
        return ClientMessage.newBuilder().setFitRes(res).build();
    }

    private static ClientMessage evaluateResAsProto(float accuracy, int testing_size) {
        ClientMessage.EvaluateRes res = ClientMessage.EvaluateRes.newBuilder().setLoss(accuracy).setNumExamples(testing_size).build();
        return ClientMessage.newBuilder().setEvaluateRes(res).build();
    }
}
