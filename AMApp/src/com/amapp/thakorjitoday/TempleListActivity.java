package com.amapp.thakorjitoday;

import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.amapp.AMAppMasterActivity;
import com.amapp.R;
import com.amapp.common.AMConstants;
import com.smart.caching.SmartCaching;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 16/7/15.
 */

public class TempleListActivity extends AMAppMasterActivity {

    private static final String TAG = "TempleListActivity";

    private FrameLayout frmListFragmentContainer;

    private TempleListFragment templeListFragment;

    private ArrayList<ContentValues> temples = new ArrayList<>();

    private SmartCaching smartCaching;

    private boolean isCachedDataDisplayed = false;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.temples_activity;
    }

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void setAnimations() {
        super.setAnimations();
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setReturnTransition(new Slide(Gravity.BOTTOM));
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public void initComponents() {
        super.initComponents();

        frmListFragmentContainer = (FrameLayout) findViewById(R.id.frmListFragmentContainer);
        templeListFragment = new TempleListFragment();
        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.frmListFragmentContainer, templeListFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCachedTemples();
            }
        }, 500);
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.HOME);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle(AMConstants.AM_Application_Title);
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void getTemples() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Temples_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getThakorjiTodayUrl());

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null && errorMessage.equalsIgnoreCase(getString(R.string.no_content_found))) {
                    SmartUtils.showSnackBar(TempleListActivity.this, getString(R.string.no_gym_found), Snackbar.LENGTH_LONG);
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("temples"), "temples", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                temples = mapTableNameAndData.get("temples");
                                setTempleDataInFragments(temples, isCachedDataDisplayed);
                            }
                        }, "images");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp,response
                                        .getString(AMConstants.AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams,null, !isCachedDataDisplayed);
    }

    private String getThakorjiTodayUrl() {
        String endpoint = environment.getThakorjiTodayEndpoint();
        String lastUpdatedTimeStamp = SmartApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp,"");

        String url = endpoint + "?" + AMConstants.AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp + "=" + lastUpdatedTimeStamp;
        return url;
    }

    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Temples_Tag);
    }

    private void getCachedTemples() {

        ArrayList<ContentValues> temples = new SmartCaching(this).getDataFromCache("temples");

        String lastUpdatedTimeStamp = SmartApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp, "");

        if (temples != null && temples.size() > 0) {
            this.temples = temples;
            setTempleDataInFragments(temples, isCachedDataDisplayed);
            isCachedDataDisplayed = true;
        }

        //isCachedDataDisplayed = false;
        getTemples();

//        if (temples == null || temples.size() <= 0) {
//            isCachedDataDisplayed = false;
//            getTemples();
//        } else {
//            this.temples = temples;
//            setTempleDataInFragments(temples, isCachedDataDisplayed);
//            isCachedDataDisplayed = true;
//        }
    }


    private void setTempleDataInFragments(ArrayList<ContentValues> temples, boolean isCachedDataDisplayed) {

        templeListFragment.setTemples(temples, isCachedDataDisplayed);

    }

}