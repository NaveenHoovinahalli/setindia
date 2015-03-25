package com.teli.sonyset.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.views.SonyEtMediumKalavika;
import com.teli.sonyset.views.SonyTvMediumKalavika;
import com.zedo.androidsdk.ZedoAndroidSdk;
import com.zedo.androidsdk.banners.ZedoCustomBanner;
import com.zedo.androidsdk.banners.ZedoStaticBanner;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sahana on 24/3/15.
 */
public class FeedbackActivity extends Activity {

    @InjectView(R.id.spinner_related_to)
    Spinner relatedToSpinner;

    @InjectView(R.id.occupation_et)
    Spinner occupationSpinner;

    @InjectView(R.id.age_et)
    Spinner ageSpinner;

    @InjectView(R.id.gender_sp)
    Spinner genderSpinner;

    @InjectView(R.id.country_sp)
    Spinner countrySpinner;

    @InjectView(R.id.full_name_et)
    SonyEtMediumKalavika fullName;

    @InjectView(R.id.email_id_et)
    SonyEtMediumKalavika emailId;

    @InjectView(R.id.contact_et)
    SonyEtMediumKalavika contact;

    @InjectView(R.id.city_et)
    SonyEtMediumKalavika city;

    @InjectView(R.id.message_et)
    SonyEtMediumKalavika message;

    @InjectView(R.id.zedoBannerLayout)
    LinearLayout zedoBannerLayout;

    @InjectView(R.id.zedoCustomBanner)
    ZedoCustomBanner zedoCustomBanner;

    @InjectView(R.id.reset_btn)
    SonyTvMediumKalavika reset;

    @InjectView(R.id.submit_btn)
    SonyTvMediumKalavika submit;

    private String showTitle;
    private List<String> relatedToList;
    Map<String, String> aliasMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);


        aliasMap = new LinkedHashMap<String, String>();
        aliasMap.put("banner", "c406d19s1");

        ZedoAndroidSdk.init(getApplicationContext(), "1408", aliasMap);

        addItemsToRelatedToSpinner();
        addItemsToOccupationSpinner();
        addItemsToAgeSpinner();
        addItemsToGenderSpinner();
        addItemsToCountrySpinner();

        showTitle = SonyDataManager.init(this).getShowTitle();
        initializeRelatedToSpinner();
    }

    private void initializeRelatedToSpinner() {

        Log.d(getClass().getSimpleName(),"initializeRelatedTo"+showTitle);

        if(!showTitle.isEmpty()){
            for (int i = 0; i<relatedToList.size();i++){
                Log.d(getClass().getSimpleName(),"listof shows"+relatedToList.get(i));
                if(relatedToList.get(i).equalsIgnoreCase(showTitle)){
                    Log.d(getClass().getSimpleName(),"equals"+relatedToList.get(i));
                    relatedToSpinner.setSelection(i);
                }
            }
        }else {
            relatedToSpinner.setSelection(0);
        }

    }

    private void addItemsToCountrySpinner() {
        List<String> countryList = new ArrayList<String>();
        Collections.addAll(countryList, this.getResources().getStringArray(R.array.country_items));
        //initAdapter(countrySpinner,countryList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, countryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //dataAdapter.notifyDataSetChanged();
        countrySpinner.setAdapter(dataAdapter);
    }

    private void addItemsToAgeSpinner() {
        List<String> ageList = new ArrayList<String>();
        Collections.addAll(ageList, this.getResources().getStringArray(R.array.age_items));
        //initAdapter(ageSpinner,ageList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, ageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //dataAdapter.notifyDataSetChanged();
        ageSpinner.setAdapter(dataAdapter);
    }

    private void addItemsToOccupationSpinner() {
        List<String> occupationList = new ArrayList<String>();
        Collections.addAll(occupationList, this.getResources().getStringArray(R.array.occupation_items));
        //initAdapter(occupationSpinner,occupationList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, occupationList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupationSpinner.setAdapter(dataAdapter);
    }

    private void addItemsToRelatedToSpinner() {
        relatedToList = new ArrayList<String>();
        Collections.addAll(relatedToList, this.getResources().getStringArray(R.array.show_items));
        //initAdapter(relatedToSpinner,relatedToList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, relatedToList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relatedToSpinner.setAdapter(dataAdapter);
    }


    private void addItemsToGenderSpinner() {
        List<String> genderList = new ArrayList<String>();
        Collections.addAll(genderList, this.getResources().getStringArray(R.array.gender_items));
        //initAdapter(genderSpinner,genderList);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, genderList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);
    }

    private void initAdapter(Spinner spinner, List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.sony_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.notifyDataSetChanged();
        spinner.setAdapter(dataAdapter);

    }

    void loadCustomBanner() {

        zedoCustomBanner.setBackgroundColor(Color.TRANSPARENT);
        zedoCustomBanner.setAutoRefreshIntervalInSeconds(15);
        zedoCustomBanner.setZedoBannerListener(new ZedoStaticBanner.ZedoBannerListener() {
            @Override
            public void onNoAdsAvailable() {
                zedoCustomBanner.hide();
                zedoBannerLayout.setVisibility(View.GONE);
            }
        });
        zedoCustomBanner.setAutoRefreshIntervalInSeconds(15);
        zedoCustomBanner.displayAd("banner");
        zedoCustomBanner.forceLayoutDimensions(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomBanner();
    }

    @OnClick(R.id.backBtn)
    public void backPressed(){
        super.onBackPressed();
    }

    @OnClick(R.id.reset_btn)
    public void resetClicked(){
        relatedToSpinner.setSelection(0);
        occupationSpinner.setSelection(0);
        ageSpinner.setSelection(0);
        genderSpinner.setSelection(0);
        countrySpinner.setSelection(0);
        fullName.setText(null);
        emailId.setText(null);
        contact.setText(null);
        message.setText(null);
    }


    @OnClick(R.id.submit_btn)
    public void submitClicked(){

        boolean isValid = validateEntries();

        if(!isValid)
            return;

        String showId = SonyDataManager.init(this).getShowId();
        String showNid = SonyDataManager.init(this).getShowNid();
        String countryId = SonyDataManager.init(this).getCountryId();

        String url = String.format(Constants.SUBMIT_FEEDBACK,showNid,showId,countryId,
                fullName.getText().toString().replace(" ", "%20"),
                emailId.getText().toString().replace(" ", "%20"),
                contact.getText().toString().replace(" ", "%20"),
                message.getText().toString().replace(" ","%20"));

        Log.d(getClass().getSimpleName(), "URL POST::" + url);

        SonyRequest request = new SonyRequest(this , url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d(getClass().getSimpleName(), "Feedback response" + s);

                showSuccessDialog();

            }

            @Override
            public void onError(String e) {
                Log.d(getClass().getSimpleName(), "Feedback Exception" + e);
            }
        };
        request.execute();


    }

    private void showSuccessDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FeedbackActivity.this);

        alertDialogBuilder.setTitle("Sony Entertainment Television");
        alertDialogBuilder
                .setMessage("Thank you. Your feedback is successfully submitted.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private boolean validateEntries() {

        if(!AndroidUtils.isNetworkOnline(this)){
            Toast.makeText(this,"Please check your internet connection and try again.",Toast.LENGTH_SHORT).show();
        }else{

            if(AndroidUtils.isValidName(fullName.getText().toString())) {

                if (AndroidUtils.isValidEmail(emailId.getText())) {

                    if (AndroidUtils.isValidPhoneNumber(contact.getText().toString())) {

                        if (AndroidUtils.isValidName(city.getText().toString())) {

                            if (!relatedToSpinner.getSelectedItem().toString().contains("-Select-")) {
                                if (!occupationSpinner.getSelectedItem().toString().contains("-Select-")) {
                                    if (!ageSpinner.getSelectedItem().toString().contains("-Select-")) {
                                        if (!genderSpinner.getSelectedItem().toString().contains("-Select-")) {
                                            if (!countrySpinner.getSelectedItem().toString().contains("-Select-")) {
                                                return true;
                                            }else {
                                                Toast.makeText(this, "Please select a country.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(this, "Please select your gender.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "Please select your age group.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Please select your occupation.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Please select a show.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Invalid city name", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid contact number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid email Id", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }


}
