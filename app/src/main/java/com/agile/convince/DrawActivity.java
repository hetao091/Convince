package com.agile.convince;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.agile.sign.SignaturePad;

/**
 * 创建人:    何 涛
 * 创建时间:  2017/5/25 下午4:45
 * 描述:
 */

public class DrawActivity extends AppCompatActivity {

    private SignaturePad mPad;
    private Toolbar mToolbar;

    public static float padWidth = 0.0f;
    public static float padHeight = 0.0f;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        mPad= (SignaturePad) findViewById(R.id.signaturePad);
        mToolbar= (Toolbar) findViewById(R.id.toolbar_draw);
        setToolbar();
        initView();
    }
    private void setToolbar() {
        mToolbar.setTitle(getString(R.string.title_toolbar_draw));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {

        ViewTreeObserver vto = mPad.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                padHeight = mPad.getMeasuredHeight();
                padWidth = mPad.getMeasuredWidth();
                return true;
            }
        });

        mPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id==R.id.action_clear){
            SignaturePad.mListAllX.clear();
            SignaturePad.mListAllY.clear();
            mPad.clear();
        }
        if (id==R.id.action_confirm){
            setResult(11);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
