package com.tzpt.cloudlibrary.app.ebook.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.books.view.WebReaderView;

/**
 * test 类
 */
public class TestActivity extends AppCompatActivity {

    private WebReaderView mWebReaderView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mWebReaderView = (WebReaderView) findViewById(R.id.mWebReaderView);
        mWebView = (WebView) findViewById(R.id.mWebView);

        mWebReaderView.loadDataWithBaseURL(null, "11111111111111111111111111", "text/html", "UTF-8", null);
        mWebView.loadDataWithBaseURL(null, "11111111111111111111111111", "text/html", "UTF-8", null);
    }
}
