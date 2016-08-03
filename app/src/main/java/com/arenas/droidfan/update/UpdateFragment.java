package com.arenas.droidfan.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdateFragment extends Fragment implements UpdateContract.View
        , View.OnClickListener , TextWatcher{

    private static final String TAG = UpdateFragment.class.getSimpleName();
    private static final int MAX_TEXT_LENGTH = 140;
    public static final int REQUEST_SELECT_PHOTO = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;

    private UpdateContract.Presenter mPresenter;

    private AutoCompleteTextView mStatusText;
    private ImageView mSend;
    private TextView mTextCount;

    private ImageView mSelectImage;
    private ImageView mPhoto;
    private ImageView mTakePhoto;

    private CharSequence temp;

    public UpdateFragment() {

    }

    protected IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    @Override
    public void onResume() {
        super.onResume();
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
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FanFouService.FILTER_USERS);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update , container , false);
        init(view);
        return view;
    }

    private void init(View view){
        mStatusText = (AutoCompleteTextView) view.findViewById(R.id.et_status_text);
        mStatusText.addTextChangedListener(this);

        mSend = (ImageView) view.findViewById(R.id.send);
        mSend.setOnClickListener(this);

        mTextCount = (TextView)view.findViewById(R.id.text_count);
        mSelectImage = (ImageView)view.findViewById(R.id.add_photo);
        mSelectImage.setOnClickListener(this);
        mPhoto = (ImageView)view.findViewById(R.id.iv_photo);
        mPhoto.setOnClickListener(this);

        mTakePhoto = (ImageView)view.findViewById(R.id.take_photo);
        mTakePhoto.setOnClickListener(this);

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
                getActivity().finish();
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

    private void activateSend(){
        mSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_send));
    }
    private void invalidSend(){
        mSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_grey));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                mPresenter.update(getContext() , mStatusText.getText().toString());
                break;
            case R.id.add_photo:
                Utils.hideKeyboard(getContext() , mStatusText);
                mPresenter.selectPhoto();
                break;
            case R.id.iv_photo:
                mPresenter.deletePhoto();
                break;
            case R.id.take_photo:
                Utils.hideKeyboard(getContext() , mStatusText);
                mPresenter.takePhoto(this , REQUEST_TAKE_PHOTO);
                break;
        }
    }

    @Override
    public void showPhotoAlbum() {
        Utils.selectImage(this , REQUEST_SELECT_PHOTO);
    }

    @Override
    public void showPhoto(Bitmap bitmap) {
        if (bitmap != null){
            mPhoto.setVisibility(View.VISIBLE);
            mPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void hidePhoto() {
        mPhoto.setVisibility(View.GONE);
    }

    @Override
    public void showHome() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onResult(getContext() , requestCode , resultCode , data);
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

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }
}
