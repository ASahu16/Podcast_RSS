package com.example.podcast_rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class RSS_Reader {


    private static String rssUrlString = "http://joeroganexp.joerogan.libsynpro.com/rss";
    static ListView lvRss;
    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> links = new ArrayList<>();
    static ArrayList<String> description = new ArrayList<>();
    private static RSS_Reader instance;
    private static Context context;

    private RSS_Reader() {

    }

    public static RSS_Reader getInstance() {
        if (instance == null) {
            instance = new RSS_Reader();
        }
        return instance;
    }

    public static void setRssUrlString(Context context, String rssUrlString) {
        RSS_Reader.context = context;
        RSS_Reader.rssUrlString = rssUrlString;
    }

    public static InputStream getInputStream(URL rssUrl) {
        try {
            return rssUrl.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {
        ProgressDialog progressDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.setMessage("Busy Loading RSS feed");
//            progressDialog.show() ;

        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try {
                URL url = new URL(rssUrlString);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                Boolean insideItem = false;

                // Returns the type of current event: START_TAG, END_TAG, START_DOCUMENT, END_DOCUMENT etc..
                int eventType = xpp.getEventType(); //loop control variable

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    //if we are at a START_TAG (opening tag)
                    if (eventType == XmlPullParser.START_TAG) {
                        //if the tag is called "item"
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        }
                        //if the tag is called "title"
                        else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                // extract the text between <title> and </title>
                                titles.add(xpp.nextText());
                            }
                        }
                        //if the tag is called "link"
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                // extract the text between <link> and </link>
                                links.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                // extract the text between <title> and </title>
                                description.add(xpp.nextText());
                            }
                        }
                    }
                    //if we are at an END_TAG and the END_TAG is called "item"
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next(); //move to next element
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, titles);
//            lvRss.setAdapter(adapter);
//            progressDialog.dismiss();
        }
    }


}
