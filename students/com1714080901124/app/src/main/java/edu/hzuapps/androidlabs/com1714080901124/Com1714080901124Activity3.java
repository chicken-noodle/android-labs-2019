package edu.hzuapps.androidlabs.com1714080901124;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class Com1714080901124Activity2 extends AppCompatActivity implements View.OnClickListener {

    public static final int DOWNLOAD_CODE = 10001;
    public static final int DOWNLOAD_FAIL = 300;
    public static final int CONNECT_TIMEOUT = 2000;
    private ImageView image_download;
    private Button btn_download;

    private Handler handler;

    private String path = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1555732919&di=b38a8ad9d9f43c8728391db832bbc7db&src=http://cdn.moji002.com/images/simgs/2017/12/04/15123851100.53172200.1955_android.jpg";

    private int fileLength;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_1714080901124_activity2);

        initialView();

        btn_download.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DOWNLOAD_CODE:
                        Bitmap bitmap = (Bitmap) msg.obj;
                        if (bitmap != null) {
                            image_download.setImageBitmap(bitmap);//disPlay image
                        }
                        break;
                    case DOWNLOAD_FAIL:
                        Toast.makeText(Com1714080901124Activity2.this, "����ʧ��", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }

    public void initialView() {
        btn_download = findViewById(R.id.btn_download);
        image_download = findViewById(R.id.image_download);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                new Thread(new GetPictThread(handler, path)).start();
                break;
        }
    }


    public class GetPictThread implements Runnable {
        public Handler handler;
        public String path;

        public GetPictThread(Handler handler, String path) {
            this.handler = handler;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(30 * 1000);
                connection.connect();

                int requestCode = connection.getResponseCode();
                System.out.println(requestCode);

                if (requestCode == HttpURLConnection.HTTP_OK) {
                    fileLength = connection.getContentLength();

                    InputStream is = new BufferedInputStream(connection.getInputStream());

                    byte[] arr = streamToArr(is);

                    mBitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                    Message message = Message.obtain();
                    message.what = DOWNLOAD_CODE;
                    message.obj = mBitmap;
                    handler.sendMessage(message);

                } else {
                    Log.e("TAG", "run:error " + requestCode);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(DOWNLOAD_FAIL);
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(DOWNLOAD_FAIL);
            }
        }
    }

    public byte[] streamToArr(InputStream inputStream) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            baos.close();
            inputStream.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}