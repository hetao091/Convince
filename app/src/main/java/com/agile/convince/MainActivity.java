package com.agile.convince;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.agile.pdf.AddSignature;
import com.agile.pdf.Inspect;
import com.agile.view.PDFView;
import com.agile.view.listener.OnErrorListener;
import com.agile.view.scroll.DefaultScrollHandle;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private PDFView mPDFView;
    private Button mBtSign;
    private Button mBtSeal;
    private Button mBtCheck;
    private Toolbar mToolbar;
    private byte[] mBytes;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPDFView = (PDFView) findViewById(R.id.pdfView);
        mBtSign = (Button) findViewById(R.id.bt_sign);
        mBtSeal = (Button) findViewById(R.id.bt_seal);
        mBtCheck = (Button) findViewById(R.id.bt_check);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setToolbar();
        mBtSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                startActivityForResult(intent, 10);
            }
        });
        mBtSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NormalTask().execute();
            }
        });
        mBtCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    StringBuilder stringBuilder = new StringBuilder();
                    int size = new Inspect().inspectSignatures(mBytes, stringBuilder);
                    if (size==0){
                        showDialog("没有检索到有效的数字签名信息");
                    }else {
                        showDialog(stringBuilder.toString());
                    }

//                    new Inspect().inspectSignatures(getAssetsBytes("demo.pdf"));
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }


            }
        });

        mBytes = getAssetsBytes("test.pdf");
        showPDFWithBytes(mBytes);
    }

    private void setToolbar() {
        mToolbar.setTitle(getString(R.string.title_toolbar_main));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showPDFWithBytes(byte[] path) {
        configurator(mPDFView.fromBytes(path));

    }

    private void configurator(PDFView.Configurator configurator) {
        configurator.defaultPage(0)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        showToast(t.getMessage());
                    }
                })
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    private class NormalTask extends AsyncTask<String, Void, String> {
        private NormalTask() {
            dialog = ProgressDialog.show(MainActivity.this, null, "签章处理中...");
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mBytes = new AddSignature(MainActivity.this).addSignture(mBytes);
            } catch (GeneralSecurityException | IOException | DocumentException e) {
                e.printStackTrace();
                return null;
            }
            return "" + mBytes.length;
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (s != null) {
                showPDFWithBytes(mBytes);
            } else {
                showToast("签章处理异常");
            }
            super.onPostExecute(s);
        }
    }

    private class SignTask extends AsyncTask<String, Void, String> {
        private SignTask() {
            dialog = ProgressDialog.show(MainActivity.this, null, "签字处理中...");
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mBytes = PDFOperateUtil.addSignMark(mBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return "" + mBytes.length;
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (s != null) {
                Log.v("onPostExecute", "onPostExecute" + mBytes.length);
                showPDFWithBytes(mBytes);
            } else {
                showToast("签字处理异常");
            }
            super.onPostExecute(s);
        }
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("信息");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private byte[] getAssetsBytes(String fileName) {
        byte[] buffer = null;
        try {
            InputStream is = getAssets().open(fileName);
            int lenght = is.available();
            buffer = new byte[lenght];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 11) {
            new SignTask().execute();
        }
    }
}
