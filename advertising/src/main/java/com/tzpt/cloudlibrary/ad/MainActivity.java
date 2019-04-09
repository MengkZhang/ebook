package com.tzpt.cloudlibrary.ad;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tzpt.cloudlibrary.ad.agentweb.AgentWeb;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mWebContentRl;
    private ImageButton mBackBtn;
    private ImageButton mForwardBtn;
    private ImageButton mRefreshBtn;
    private ImageView mSplashImgIv;
    protected AgentWeb mAgentWeb;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    mAgentWeb.goBack();
                    break;
                case R.id.forward:
                    mAgentWeb.goForward();
                    break;
                case R.id.refresh:
                    mAgentWeb.reload();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mWebContentRl = (LinearLayout) findViewById(R.id.web_content_rl);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
        mBackBtn = (ImageButton) findViewById(R.id.back);
        mForwardBtn = (ImageButton) findViewById(R.id.forward);
        mRefreshBtn = (ImageButton) findViewById(R.id.refresh);
        mSplashImgIv = (ImageView) findViewById(R.id.bg_splash_default_iv);

        mBackBtn.setOnClickListener(mClickListener);
        mForwardBtn.setOnClickListener(mClickListener);
        mRefreshBtn.setOnClickListener(mClickListener);

        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mWebContentRl,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go("http://www.ytsg.com/");
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();//恢复
        super.onResume();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSplashImgIv.setVisibility(View.GONE);
            if (mAgentWeb.canGoBack()) {
                mBackBtn.setImageResource(R.mipmap.ic_back_nor);
            } else {
                mBackBtn.setImageResource(R.mipmap.ic_back_pre);
            }

            if (mAgentWeb.canGoForward()) {
                mForwardBtn.setImageResource(R.mipmap.ic_forward_nor);
            } else {
                mForwardBtn.setImageResource(R.mipmap.ic_forward_pre);
            }
        }
    };
}
