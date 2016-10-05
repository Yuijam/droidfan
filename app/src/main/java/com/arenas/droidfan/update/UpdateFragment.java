package com.arenas.droidfan.update;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.draft.DraftActivity;

import java.io.FileInputStream;
import java.util.jar.Manifest;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdateFragment extends Fragment implements UpdateContract.View
        , View.OnClickListener , TextWatcher{

    private static final String TAG = UpdateFragment.class.getSimpleName();
    private static final int MAX_TEXT_LENGTH = 140;
    public static final int REQUEST_SELECT_PHOTO = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final int REQUEST_DRAFT = 3;

    private UpdateContract.Presenter mPresenter;

    @BindView(R.id.et_status_text) AutoCompleteTextView mStatusText;
    @BindView(R.id.send) Button mSend;
    @BindView(R.id.text_count) TextView mTextCount;
    @BindView(R.id.add_photo) ImageView addPhoto;
    @BindView(R.id.take_photo) ImageView takePhoto;
    @BindView(R.id.iv_photo) ImageView photo;
    @BindView(R.id.draft) ImageView draft;
    @BindView(R.id.topic) ImageView topic;

    private CharSequence temp;
    private static final String KEY_STATUS = "key_status";

    public UpdateFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG , "onResume```");
        mPresenter.start();
    }

    public static UpdateFragment newInstance(){
        return new UpdateFragment();
    }

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (UpdateContract.Presenter)presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG , "onCreate``");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_STATUS , mStatusText.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update , container , false);
        init(view);
        if (savedInstanceState != null){
            Log.d(TAG , savedInstanceState.getString(KEY_STATUS));
            mStatusText.setText(savedInstanceState.getString(KEY_STATUS));
        }
        return view;
    }

    private void init(View view){
        ButterKnife.bind(this , view);

        mStatusText.addTextChangedListener(this);
        mSend.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
        photo.setOnClickListener(this);
        takePhoto.setOnClickListener(this);

        topic.setOnClickListener(this);
        draft.setOnClickListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void setAutoTextAdapter(String[] users) {
        ArrayAdapter<String> adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                users);
        mStatusText.setAdapter(adapter);
        mStatusText.setThreshold(1);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int rest = MAX_TEXT_LENGTH - temp.length();
        mTextCount.setText(""+rest);
        if (rest < 0 || rest == MAX_TEXT_LENGTH){
            invalidSend();
        }else {
            activateSend();
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    public void onBackPressed(){
        if (mStatusText.getText().toString().length()>0){
            showSaveDraftDialog();
        }else {
            finish();
        }
    }

    private void showSaveDraftDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.setMessage(getContext().getString(R.string.save_draft_message))
                .setNegativeButton(getContext().getString(R.string.do_not_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.saveDraft(mStatusText.getText().toString());
                        dialogInterface.dismiss();
                        finish();
                    }
                }).create();
        alertDialog.show();
    }

    private void activateSend(){
        mSend.setBackground(getResources().getDrawable(R.drawable.bg_send_button_activate));
    }
    private void invalidSend(){
        mSend.setBackground(getResources().getDrawable(R.drawable.bg_send_button_invalid));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                mPresenter.update(getContext() , mStatusText.getText().toString());
                break;
            case R.id.add_photo:
                Utils.hideKeyboard(getContext() , mStatusText);
//                checkPermission(REQUEST_SELECT_PHOTO);
                mPresenter.selectPhoto(getActivity() , REQUEST_SELECT_PHOTO);
                break;
            case R.id.take_photo:
//                checkPermission(REQUEST_TAKE_PHOTO);
                Utils.hideKeyboard(getContext() , mStatusText);
                mPresenter.takePhoto(getActivity() , REQUEST_TAKE_PHOTO);
                break;
            case R.id.iv_photo:
                mPresenter.deletePhoto();
                break;
            case R.id.draft:
                DraftActivity.start(getActivity() , REQUEST_DRAFT);
                break;
            case R.id.topic:
                addTopic();
                break;
        }
    }

    private void addTopic(){
        mStatusText.getText().append("##");
        Selection.setSelection(mStatusText.getText() , mStatusText.getText().length()-1);
    }

    @Override
    public void showPhoto(Bitmap bitmap) {
        if (bitmap != null){
            photo.setVisibility(View.VISIBLE);
            photo.setImageBitmap(bitmap);
        }
    }

    @Override
    public void hidePhoto() {
        photo.setVisibility(View.GONE);
    }

    @Override
    public void showHome() {
        getActivity().finish();
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), "something wrong!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStatusText(String statusText) {
        mStatusText.setText(statusText);
    }

    @Override
    public void setSelection(String text) {
        Selection.setSelection(mStatusText.getText() , text.length());
    }

    @Override
    public void refreshInputStatus() {
        int textLength = mStatusText.getText().toString().length();
        mTextCount.setText((MAX_TEXT_LENGTH - textLength) + "");
        if (textLength > 0 && textLength <= MAX_TEXT_LENGTH){
            activateSend();
        }

    }
}
