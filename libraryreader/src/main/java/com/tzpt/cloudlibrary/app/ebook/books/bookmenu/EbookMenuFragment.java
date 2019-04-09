package com.tzpt.cloudlibrary.app.ebook.books.bookmenu;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.adapter.CloudLibraryTOCAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.model.Chapter;
import com.tzpt.cloudlibrary.app.ebook.books.model.ReaderSettings;
import com.tzpt.cloudlibrary.app.ebook.books.parser.EpubParser;

import java.util.ArrayList;

/**
 * 电子书目录
 */
public class EbookMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    private EbookMenuFragmentToChapterListener callback;
    //book menu info
    private ListView mListView;
    private CloudLibraryTOCAdapter adapter;
    private TextView textShow;
    private Typeface typeFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ebook_menu, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (EbookMenuFragmentToChapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EbookMenuFragmentToChapterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mListView = (ListView) view.findViewById(R.id.mListView);
        this.textShow = (TextView) view.findViewById(R.id.textShow);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null == adapter) {
            return;
        }
        EpubParser.NavPoint nav = adapter.getItem(position);
        if (null == nav || null == callback) {
            return;
        }
        callback.callbackClickItemToChapter(nav, position);
    }

    /**
     * 获取adapter
     *
     * @return
     */
    public CloudLibraryTOCAdapter getAdapter() {
        return adapter;
    }

    /**
     * 设置电子书目录列表
     *
     * @param mReaderSettings
     * @param sequenceReadingChapterList
     * @param toc
     * @param currentChapterIndex
     */
    public void setListViewDatas(ReaderSettings mReaderSettings, SparseArray<Chapter> sequenceReadingChapterList, ArrayList<EpubParser.NavPoint> toc, int currentChapterIndex) {
        adapter = new CloudLibraryTOCAdapter(getActivity(), mReaderSettings, sequenceReadingChapterList, toc, currentChapterIndex, typeFace);
        if (null != mListView) {
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(this);
            if (null == toc || toc.size() == 0) {
                mListView.setVisibility(View.GONE);
                textShow.setVisibility(View.VISIBLE);
                if (null != typeFace) {
                    textShow.setTypeface(typeFace);
                }
            } else {
                mListView.setVisibility(View.VISIBLE);
                textShow.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 设置字体类型
     *
     * @param typeFace
     */
    public void setTypeFace(Typeface typeFace) {
        this.typeFace = typeFace;
    }


    public interface EbookMenuFragmentToChapterListener {

        void callbackClickItemToChapter(EpubParser.NavPoint nav, int position);
    }
}
