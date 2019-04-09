package com.tzpt.cloudlibrary.cbreader.book;

import com.tzpt.cloudlibrary.cbreader.sort.TitledEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * JNI对book进行赋值
 * Created by Administrator on 2017/4/8.
 */

public abstract class AbstractBook extends TitledEntity<AbstractBook> {
//    public static final String READ_LABEL = "read";

//    private volatile long myId;

//    protected volatile String myLanguage;
    //作者
    protected volatile List<Author> mAuthors;
    //标签
//    protected volatile List<Tag> myTags;
    //标注
//    private volatile List<Label> myLabels;
//    protected volatile SeriesInfo mySeriesInfo;
    //    protected volatile List<UID> myUids;
//    protected volatile RationalNumber myProgress;

//    public volatile boolean HasBookmark;

    AbstractBook(String title) {
        super(title);
        mAuthors = null;
    }

    public abstract String getPath();

//    public void updateFrom(AbstractBook book) {
//        if (book == null || myId != book.myId) {
//            return;
//        }
//        setTitle(book.getTitle());
//        setEncoding(book.myEncoding);
//        setLanguage(book.myLanguage);
//        if (!ComparisonUtil.equal(myAuthors, book.myAuthors)) {
//            myAuthors = book.myAuthors != null ? new ArrayList<Author>(book.myAuthors) : null;
//            mSaveState = SaveState.NotSaved;
//        }
//        if (!ComparisonUtil.equal(myTags, book.myTags)) {
//            myTags = book.myTags != null ? new ArrayList<Tag>(book.myTags) : null;
//            mSaveState = SaveState.NotSaved;
//        }
//        if (!MiscUtil.listsEquals(myLabels, book.myLabels)) {
//            myLabels = book.myLabels != null ? new ArrayList<Label>(book.myLabels) : null;
//            mSaveState = SaveState.NotSaved;
//        }
//        if (!ComparisonUtil.equal(mySeriesInfo, book.mySeriesInfo)) {
//            mySeriesInfo = book.mySeriesInfo;
//            mSaveState = SaveState.NotSaved;
//        }
//        if (!MiscUtil.listsEquals(myUids, book.myUids)) {
//            myUids = book.myUids != null ? new ArrayList<UID>(book.myUids) : null;
//            mSaveState = SaveState.NotSaved;
//        }
//        setProgress(book.myProgress);
//        if (HasBookmark != book.HasBookmark) {
//            HasBookmark = book.HasBookmark;
//            mSaveState = SaveState.NotSaved;
//        }
//    }

//    public final List<Author> authors() {
//        return myAuthors != null
//                ? Collections.unmodifiableList(myAuthors)
//                : Collections.<Author>emptyList();
//    }

//    public final String authorsString(String separator) {
//        final List<Author> authors = myAuthors;
//        if (authors == null || authors.isEmpty()) {
//            return null;
//        }
//
//        final StringBuilder buffer = new StringBuilder();
//        boolean first = true;
//        for (Author a : authors) {
//            if (!first) {
//                buffer.append(separator);
//            }
//            buffer.append(a.DisplayName);
//            first = false;
//        }
//        return buffer.toString();
//    }

//    void addAuthorWithNoCheck(Author author) {
//        if (myAuthors == null) {
//            myAuthors = new ArrayList<Author>();
//        }
//        myAuthors.add(author);
//    }

//    public void removeAllAuthors() {
//        if (myAuthors != null) {
//            myAuthors = null;
//        }
//    }

    public void addAuthor(Author author) {
        if (author == null) {
            return;
        }
        if (mAuthors == null) {
            mAuthors = new ArrayList<>();
            mAuthors.add(author);
        } else if (!mAuthors.contains(author)) {
            mAuthors.add(author);
        }
    }

    public void addAuthor(String name) {
        addAuthor(name, null);
    }

    public void addAuthor(String name, String sortKey) {
        addAuthor(Author.create(name, sortKey));
    }

//    public long getId() {
//        return myId;
//    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            return;
        }
        title = title.trim();
        if (title.length() == 0) {
            return;
        }
        if (!getTitle().equals(title)) {
            super.setTitle(title);
        }
    }

//    public SeriesInfo getSeriesInfo() {
//        return mySeriesInfo;
//    }

//    void setSeriesInfoWithNoCheck(String name, String index) {
//        mySeriesInfo = SeriesInfo.createSeriesInfo(name, index);
//    }

//    public void setSeriesInfo(String name, String index) {
//        setSeriesInfo(name, SeriesInfo.createIndex(index));
//    }

//    public void setSeriesInfo(String name, BigDecimal index) {
//        if (mySeriesInfo == null) {
//            if (name != null) {
//                mySeriesInfo = new SeriesInfo(name, index);
//            }
//        } else if (name == null) {
//            mySeriesInfo = null;
//        } else if (!name.equals(mySeriesInfo.Series.getTitle()) || mySeriesInfo.Index != index) {
//            mySeriesInfo = new SeriesInfo(name, index);
//        }
//    }

//    @Override
//    public String getLanguage() {
//        return myLanguage;
//    }

//    public void setLanguage(String language) {
//        if (!ComparisonUtil.equal(myLanguage, language)) {
//            myLanguage = language;
//            resetSortKey();
//        }
//    }

//    public final String tagsString(String separator) {
//        final List<Tag> tags = myTags;
//        if (tags == null || tags.isEmpty()) {
//            return null;
//        }
//
//        final HashSet<String> tagNames = new HashSet<String>();
//        final StringBuilder buffer = new StringBuilder();
//        boolean first = true;
//        for (Tag t : tags) {
//            if (!first) {
//                buffer.append(separator);
//            }
//            if (!tagNames.contains(t.Name)) {
//                tagNames.add(t.Name);
//                buffer.append(t.Name);
//                first = false;
//            }
//        }
//        return buffer.toString();
//    }

//    void addTagWithNoCheck(Tag tag) {
//        if (myTags == null) {
//            myTags = new ArrayList<Tag>();
//        }
//        myTags.add(tag);
//    }

//    public void removeAllTags() {
//        if (myTags != null) {
//            myTags = null;
//        }
//    }

//    public void addTag(Tag tag) {
//        if (tag != null) {
//            if (myTags == null) {
//                myTags = new ArrayList<Tag>();
//            }
//            if (!myTags.contains(tag)) {
//                myTags.add(tag);
//            }
//        }
//    }

//    public void addTag(String tagName) {
//        addTag(Tag.getTag(null, tagName));
//    }

//    public boolean hasLabel(String name) {
//        for (Label l : labels()) {
//            if (name.equals(l.Name)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public List<Label> labels() {
//        return myLabels != null ? Collections.unmodifiableList(myLabels) : Collections.<Label>emptyList();
//    }

//    void addLabelWithNoCheck(Label label) {
//        if (myLabels == null) {
//            myLabels = new ArrayList<Label>();
//        }
//        myLabels.add(label);
//    }

//    public void addNewLabel(String label) {
//        addLabel(new Label(label));
//    }
//
//    public void addLabel(Label label) {
//        if (myLabels == null) {
//            myLabels = new ArrayList<Label>();
//        }
//        if (!myLabels.contains(label)) {
//            myLabels.add(label);
//            mSaveState = SaveState.NotSaved;
//        }
//    }
//
//    public void removeLabel(String label) {
//        if (myLabels != null && myLabels.remove(new Label(label))) {
//            mSaveState = SaveState.NotSaved;
//        }
//    }

//    public List<UID> uids() {
//        return myUids != null ? Collections.unmodifiableList(myUids) : Collections.<UID>emptyList();
//    }

//    public void addUid(String type, String id) {
//        addUid(new UID(type, id));
//    }

//    public void addUid(UID uid) {
//        if (uid == null) {
//            return;
//        }
//        if (myUids == null) {
//            myUids = new ArrayList<UID>();
//        }
//        if (!myUids.contains(uid)) {
//            myUids.add(uid);
//            mSaveState = SaveState.NotSaved;
//        }
//    }

//    public boolean matchesUid(UID uid) {
//        return myUids.contains(uid);
//    }

//    public RationalNumber getProgress() {
//        return myProgress;
//    }
//
//    public void setProgress(RationalNumber progress) {
//        if (!ComparisonUtil.equal(myProgress, progress)) {
//            myProgress = progress;
//            if (mSaveState == SaveState.Saved) {
//                mSaveState = SaveState.ProgressNotSaved;
//            }
//        }
//    }
//
//    public void setProgressWithNoCheck(RationalNumber progress) {
//        myProgress = progress;
//    }

//    public boolean matches(String pattern) {
//        if (MiscUtil.matchesIgnoreCase(getTitle(), pattern)) {
//            return true;
//        }
//        if (mySeriesInfo != null && MiscUtil.matchesIgnoreCase(mySeriesInfo.Series.getTitle(), pattern)) {
//            return true;
//        }
//        if (myAuthors != null) {
//            for (Author author : myAuthors) {
//                if (MiscUtil.matchesIgnoreCase(author.DisplayName, pattern)) {
//                    return true;
//                }
//            }
//        }
//        if (myTags != null) {
//            for (Tag tag : myTags) {
//                if (MiscUtil.matchesIgnoreCase(tag.Name, pattern)) {
//                    return true;
//                }
//            }
//        }
//
//        String fileName = getPath();
//        // first archive delimiter
//        int index = fileName.indexOf(":");
//        // last path delimiter before first archive delimiter
//        if (index == -1) {
//            index = fileName.lastIndexOf("/");
//        } else {
//            index = fileName.lastIndexOf("/", index);
//        }
//        fileName = fileName.substring(index + 1);
//        if (MiscUtil.matchesIgnoreCase(fileName, pattern)) {
//            return true;
//        }
//        return false;
//    }
}
