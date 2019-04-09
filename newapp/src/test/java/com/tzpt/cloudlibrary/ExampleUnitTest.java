package com.tzpt.cloudlibrary;

import android.util.Log;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        List<String> str1 = new ArrayList<String>();
        List<String> str2 = new ArrayList<String>();
        str1.add("1");
        str1.add("2");
        str1.add("3");
        str1.add("4");
        str1.add("36");

        str2.add("7");
        str2.add("8");
        str2.add("9");

        for (int i = 0; i < str1.size(); i++) {
            for (int j = 0; j < str2.size(); j++) {
//                if (!str1.get(i).equals(str2.get(j))) {
//                    str2.remove(j);
//                }
                if (!str1.contains(str2.get(j))) {
                    str2.remove(j);
                }
            }
        }
        System.out.println(str2);
    }

    @Test
    public void Test2() {
        double obj = 96.0;
        int noteId = (int) obj;
        System.out.println(noteId + "");
    }

    @Test
    public void Test() {

        searchContent("预言");

    }

    private void searchContent(String content) {
        List<String> list = new ArrayList<>();
        String lastContent = "理想国,预言,预言,今天真的是一个合适,的日子,日,看书的日子,合适看书,的日子日,故事大王,今天,真的是,一个,合适,看书,的日子,合适看书";
        if (lastContent.contains(",")) {//!TextUtils.isEmpty(lastContent) &&
            String[] temps = lastContent.split(",");
            list.addAll(Arrays.asList(temps));
            //1.去掉重复元素
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                String x = it.next();
                if (x.equals(content)) {
                    it.remove();
                }
            }
        }
        //2.将最新元素放在最前面
        list.add(0, content);
        //3.取最新的10条记录
        arrayToString((list.size() > 10) ? list.subList(0, 10) : list);
    }

    private void arrayToString(List<String> strList) {
        String[] l2 = (String[]) strList.toArray(new String[0]);
        //4.转化为string
        String result = Arrays.toString(l2).replace("[", "").replace("]", "");
        System.out.println(result);
        //5.保存到文件中
//        saveToHistory();
    }

    @Test
    public void myTest() {
        String account = "5107041988**52";
        String ps = account.substring(account.length() - 4, account.length() - 2);
        System.out.println(ps);
        System.out.println(account.length());
    }

    @Test
    public void TimeTest() {
        isWithinTimeLimit("20171010");
    }

    public boolean isWithinTimeLimit(String afterDays) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date afterDate = sdf.parse(afterDays);
            long afterDateTime = afterDate.getTime();
            String times = sdf.format(new Date());
            Date date = sdf.parse(times);
            long nowTime = date.getTime();
            return afterDateTime < nowTime;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void TestCard() {
        setQRText("978852074439444399");
    }

    private final String space = " ";

    private void setQRText(String content) {
//        if (null != mView) {
        int len = content.length();
        if (len == 18) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                builder.append(content.charAt(i));
                if (i == 3 || i == 7 || i == 11) {
                    if (i != len - 1) {
                        builder.append(space);
                    }
                }
            }
            System.out.println(builder.toString());
        }
//            mView.setQRText(content);
//        }
    }

    @Test
    public void TestNum() {
//        boolean isNumber = isNumeric("12.78");
        System.out.print("-->" + Double.parseDouble("12.78"));
    }

    public boolean isNumeric(String str) {
        if (null == str || str.length() == 0) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void calcPage() {

        int index = 98;
        int currentPage = index / 20 + 1;
        int count = currentPage * 20;
        System.out.println(currentPage);
        System.out.println(count);

    }

    @Test
    public void calcRow() {
        getRow(3, 1);
        getRow(3, 5);
        getRow(3, 4);
        getRow(3, 8);
        getRow(3, 16);

    }

    private void getRow(int count, int position) {
        int result = position / count;
        System.out.println("result-->" + result);
    }
}