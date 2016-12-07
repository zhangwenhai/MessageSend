package com.theone.sns.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionCode.Role;
import com.theone.sns.common.FusionCode.UserCharacter;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.component.location.gps.ILocationListener;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.component.upload.io.IO;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.UploadCallRet;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.user.MeInfo;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PictureCallback;
import com.theone.sns.util.PictureHelper;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;
import com.theone.sns.util.uiwidget.PickerBirthdayWidget;
import com.theone.sns.util.uiwidget.PickerCityWidget;
import com.theone.sns.util.uiwidget.PickerHeightWidget;
import com.theone.sns.util.uiwidget.swithbutton.SwitchButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/8/31.
 */
public class RegisterUserInfoActivity extends IphoneTitleActivity implements
        PictureCallback {

    private static final String TAG = "RegisterUserInfoActivity";

    private static final int IPHONE_PREVIEW = 0;
    private static final int IPHONE_CAMERA = 1;
    private static final int IPHONE_GALLERY = 2;
    private static final int IPHONE_DELETE = 3;
    private static final int IPHONE_CANCEL = 4;
    private PictureHelper m_pictureHelper = null;

    private TextView nickName;
    private TextView registerAccount;
    private GridView interestsGridView;
    private PickerBirthdayWidget mPickerBirthday;
    private PickerHeightWidget mPickerHeightWidget;
    private PickerCityWidget mPickerCityWidget;
    private LinearLayout birthdayView;
    private LinearLayout heightView;
    private LinearLayout cityView;
    private TextView birthdayTextView;
    private TextView heightTextView;
    private ImageView headImageView;
    private TextView locationCityView;
    private TextView tLabel;
    private TextView pLabel;
    private TextView hLabel;
    private TextView unknowLabel;
    private int width;
    // private List<String> mPurpose = new ArrayList<String>();
    private List<String> mInterests = new ArrayList<String>();
    private IphoneStyleAlertDialogBuilder m_iphoneDialog;
    private SwitchButton isMarriage;

    private IAccountLogic mIAccountLogic;

    private String role;

    private String photoUrl;

    private String character;

    private boolean uploading = false;

    private LayoutInflater inflater;
    private InterestsGridAdapter mInterestsGridAdapter;

    // private PurposeGridAdapter mPurposeGridAdapter;
    // private GridView purposeGridView;

    private String birthday;
    private int with;
    private int height;

    @Override
    protected void initLogics() {
        mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.register_userinfo_main);

        getView();

        initView();

        setListener();

        setValue();

        Log.e(TAG, "character = " + character);
    }

    private void getView() {
        with = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        nickName = (TextView) findViewById(R.id.nick_name);
        registerAccount = (TextView) findViewById(R.id.register_account);

        birthdayView = (LinearLayout) findViewById(R.id.birthday_view);
        heightView = (LinearLayout) findViewById(R.id.height_view);
        cityView = (LinearLayout) findViewById(R.id.city_view);
        birthdayTextView = (TextView) findViewById(R.id.birthday);
        heightTextView = (TextView) findViewById(R.id.height);
        headImageView = (ImageView) findViewById(R.id.head);
        locationCityView = (TextView) findViewById(R.id.register_city);
        interestsGridView = (GridView) findViewById(R.id.interests);
        // purposeGridView = (GridView) findViewById(R.id.purpose);

        tLabel = (TextView) findViewById(R.id.t_label);
        pLabel = (TextView) findViewById(R.id.p_label);
        hLabel = (TextView) findViewById(R.id.h_label);
        unknowLabel = (TextView) findViewById(R.id.unknow_label);

        isMarriage = (SwitchButton) findViewById(R.id.marriage);

        width = getResources().getDisplayMetrics().widthPixels;

        birthday = birthdayTextView.getText() + "";

        initLabel();
    }

    private void setValue() {

        character = getIntent().getStringExtra(
                RegisterAction.EXTRA_USER_CHARACTER);

        Account account = FusionConfig.getInstance().getAccount();

        nickName.setText(account.profile.name);

        registerAccount.setText(account.loginAccount);

        LocalLocation location = LocationManager.getInstance().getLocation();

        if (null != location) {

            locationCityView.setText(location.city);

        } else {

            LocationManager.getInstance().start(new ILocationListener() {

                @Override
                public void onResult(boolean result,
                                     final LocalLocation location) {
                    if (result) {
                        getHandler().post(new Runnable() {

                            @Override
                            public void run() {
                                locationCityView.setText(location.city);
                            }
                        });
                    }
                }
            });
        }

        if (UserCharacter.GAY.equals(character)) {

            tLabel.setText(Role.MT);

            pLabel.setText(Role.MP);

            hLabel.setText(Role.MH);

            role = Role.MT;

        } else if (UserCharacter.LES.equals(character)) {

            tLabel.setText(Role.T);

            pLabel.setText(Role.P);

            hLabel.setText(Role.H);

            role = Role.T;
        }

        if (!TextUtils.isEmpty(birthday)) {

            String signStr = "(" + PrettyDateFormat.getConstellation(3, 22)
                    + ")";

            birthdayTextView.setText(birthday + signStr);
        }
    }

    private void initLabel() {
        tLabel.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (width - HelperFunc.dip2px(60)) / 4,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        pLabel.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (width - HelperFunc.dip2px(60)) / 4,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        hLabel.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (width - HelperFunc.dip2px(60)) / 4,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        unknowLabel.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (width - HelperFunc.dip2px(60)) / 4,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        tLabel.setBackgroundColor(Color.parseColor("#49C6F2"));
        tLabel.setTextColor(getResources().getColor(R.color.white));
        pLabel.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.profile_interest_btn));
        hLabel.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.profile_interest_btn));
        unknowLabel.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.profile_interest_btn));
        pLabel.setTextColor(getResources().getColor(R.color.black));
        hLabel.setTextColor(getResources().getColor(R.color.black));
        unknowLabel.setTextColor(getResources().getColor(R.color.black));
    }

    private void initView() {
        setTitle(R.string.setting);
        setLeftButton(R.drawable.icon_back, false, false);
        setRightButton(R.string.Done, true);

        List<String> interests = FusionConfig.getInstance().getAccount().config.interests;
        List<String> purposes = FusionConfig.getInstance().getAccount().config.purposes;
        interests.addAll(purposes);
        if (null != interests && interests.size() > 0) {
            mInterestsGridAdapter = new InterestsGridAdapter();
            mInterestsGridAdapter.setList(interests);
            interestsGridView.setAdapter(mInterestsGridAdapter);

            int hang = 0;
            if (interests.size() % 3 == 0) {
                hang = interests.size() / 3;
            } else {
                hang = interests.size() / 3 + 1;
            }

            interestsGridView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    (int) ((hang - 1) * HelperFunc.dip2px(20)
                            + HelperFunc.dip2px(20) + hang
                            * HelperFunc.dip2px(40))));
        }

        m_pictureHelper = new PictureHelper(this, this);

        tLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tLabel.setBackgroundColor(Color.parseColor("#49C6F2"));
                tLabel.setTextColor(getResources().getColor(R.color.white));

                pLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                hLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                unknowLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                pLabel.setTextColor(getResources().getColor(R.color.black));
                hLabel.setTextColor(getResources().getColor(R.color.black));
                unknowLabel
                        .setTextColor(getResources().getColor(R.color.black));

                role = ((TextView) view).getText().toString();
            }
        });

        pLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pLabel.setBackgroundColor(Color.parseColor("#49C6F2"));
                pLabel.setTextColor(getResources().getColor(R.color.white));

                tLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                hLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                unknowLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                tLabel.setTextColor(getResources().getColor(R.color.black));
                hLabel.setTextColor(getResources().getColor(R.color.black));
                unknowLabel
                        .setTextColor(getResources().getColor(R.color.black));

                role = ((TextView) view).getText().toString();
            }
        });

        hLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hLabel.setBackgroundColor(Color.parseColor("#49C6F2"));
                hLabel.setTextColor(getResources().getColor(R.color.white));

                pLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                tLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                unknowLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                pLabel.setTextColor(getResources().getColor(R.color.black));
                tLabel.setTextColor(getResources().getColor(R.color.black));
                unknowLabel
                        .setTextColor(getResources().getColor(R.color.black));

                role = ((TextView) view).getText().toString();
            }
        });

        unknowLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unknowLabel.setBackgroundColor(Color.parseColor("#49C6F2"));
                unknowLabel
                        .setTextColor(getResources().getColor(R.color.white));

                pLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                tLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                hLabel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.profile_interest_btn));
                pLabel.setTextColor(getResources().getColor(R.color.black));
                tLabel.setTextColor(getResources().getColor(R.color.black));
                hLabel.setTextColor(getResources().getColor(R.color.black));

                role = getResources().getString(R.string.role_server_unknow);
            }
        });

        findViewById(R.id.intent_tag).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (View.GONE == findViewById(R.id.grid_linear)
                                .getVisibility()) {
                            findViewById(R.id.grid_linear).setVisibility(
                                    View.VISIBLE);
                            ((TextView) findViewById(R.id.text)).setText("收起");
                            ((ImageView) findViewById(R.id.image))
                                    .setImageDrawable(getResources()
                                            .getDrawable(
                                                    R.drawable.iphone_submenu_normal_xia));
                        } else {
                            findViewById(R.id.grid_linear).setVisibility(
                                    View.GONE);
                            ((TextView) findViewById(R.id.text)).setText("展开");
                            ((ImageView) findViewById(R.id.image))
                                    .setImageDrawable(getResources()
                                            .getDrawable(
                                                    R.drawable.iphone_submenu_normal_shang));
                        }
                    }
                });

        // purpose1.setOnClickListener(purposeOnClickListener);
        // purpose2.setOnClickListener(purposeOnClickListener);
        // purpose3.setOnClickListener(purposeOnClickListener);
        // purpose4.setOnClickListener(purposeOnClickListener);
        // purpose5.setOnClickListener(purposeOnClickListener);
        // purpose6.setOnClickListener(purposeOnClickListener);
    }

    private void setListener() {
        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(photoUrl)) {

                    showToast(R.string.register_choose_photo);

                    return;
                }

                showLoadingDialog(getString(R.string.dialog_save));

                mIAccountLogic.updateMyUserInfo(createUser());
            }
        });

        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerBirthday();
            }
        });

        heightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerHeight();
            }
        });

        cityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerCity();
            }
        });

        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIphoneDialog();
            }
        });
    }

    private void pickerCity() {
        mPickerCityWidget = new PickerCityWidget(this);
        mPickerCityWidget
                .setPickerCallBack(new PickerCityWidget.IPickerBirthdayCallback() {

                    @Override
                    public void onPickerFinish(String province, String city) {
                        locationCityView.setText(province + " " + city);
                        mPickerCityWidget = null;
                    }

                    @Override
                    public void onPickerCancel(String province, String city) {
                        mPickerCityWidget = null;
                    }
                });
        mPickerCityWidget.show();
    }

    protected void pickerBirthday() {
        String[] s = birthday.split("-");
        mPickerBirthday = new PickerBirthdayWidget(this, Integer.valueOf(s[0]),
                Integer.valueOf(s[1]), Integer.valueOf(s[2]));
        mPickerBirthday
                .setPickerCallBack(new PickerBirthdayWidget.IPickerBirthdayCallback() {

                    @Override
                    public void onPickerFinish(int year, int month, int day) {

                        birthday = year + "-" + month + "-" + day;

                        String signStr = "("
                                + PrettyDateFormat.getConstellation(month, day)
                                + ")";

                        birthdayTextView.setText(birthday + signStr);

                        mPickerBirthday = null;
                    }

                    @Override
                    public void onPickerCancel(int year, int month, int day) {
                        mPickerBirthday = null;
                    }
                });
        mPickerBirthday.show();
    }

    protected void pickerHeight() {
        String s = heightTextView.getText().toString();
        mPickerHeightWidget = new PickerHeightWidget(this, Integer.valueOf(s
                .substring(0, s.length() - 2)));
        mPickerHeightWidget
                .setPickerCallBack(new PickerHeightWidget.IPickerHeightCallback() {
                    @Override
                    public void onPickerFinish(int height) {
                        height = height + 20;
                        heightTextView.setText(height + "cm");
                        mPickerHeightWidget = null;
                    }

                    @Override
                    public void onPickerCancel(int height) {
                        mPickerHeightWidget = null;
                    }
                });
        mPickerHeightWidget.show();
    }

    private void showIphoneDialog() {
        if (m_iphoneDialog == null) {
            m_iphoneDialog = new IphoneStyleAlertDialogBuilder(this);
            m_iphoneDialog.setTitle(R.string.add_a_photo);
            m_iphoneDialog.addItem(IPHONE_CAMERA,
                    getString(R.string.take_photo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
//                            m_pictureHelper.getPictureFromCamera();
                            startActivityForResult(new Intent(FusionAction.PublicAction.PHOTO_ACTION),
                                    PictureHelper.PHOTO_RESULT);
                        }
                    });
            m_iphoneDialog.addItem(IPHONE_GALLERY,
                    getString(R.string.choose_photo),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
                            m_pictureHelper
                                    .getPictureFromGallery(PictureHelper.PHOTO_PICKED_FROM_GALLERY);
                        }
                    });
            m_iphoneDialog.addItem(IPHONE_CANCEL, getString(R.string.Cancel),
                    IphoneStyleAlertDialogBuilder.COLOR_BLUE,
                    IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
                        }
                    });
        }
        m_iphoneDialog.show();
    }

    @Override
    public void setPicture(File f) {

        if (null == f) {
            return;
        }

        headImageView.setImageBitmap(ImageLoaderUtil.decodeFile(f.getPath()));

        doUpload(f);
    }

    @Override
    public void setPicture(File f, int angle, int camera) {
        if (null == f) {
            return;
        }
        int i = HelperFunc.readPictureDegree(f.getPath());
        Bitmap bitmap1 = null;
        if (i == 90 || i == 270) {
            bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), height, 0, with);
        } else {
            bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), with, 0, height);
        }
        int widt;
        int difference;
        Bitmap resizedBitmap = null;
        if (bitmap1.getWidth() >= bitmap1.getHeight()) {
            widt = bitmap1.getHeight();
            difference = bitmap1.getWidth() - bitmap1.getHeight();
        } else {
            widt = bitmap1.getWidth();
            difference = bitmap1.getHeight() - bitmap1.getWidth();
        }
        Matrix matrix = new Matrix();
        if (camera == 0) {
            matrix.postRotate(i + angle);
            resizedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, widt, widt, matrix, true);
        } else if (camera == 1) {
            matrix.postRotate(i - angle);
            matrix.postScale(-1, 1);
            resizedBitmap = Bitmap.createBitmap(bitmap1, difference, 0, widt, widt, matrix, true);
        }
        headImageView.setImageBitmap(resizedBitmap);
        ImageLoaderUtil.saveBitmap(resizedBitmap, f.getPath() + "1",
                TheOneConstants.PUBLIC_PICTURE_QUALITY, Bitmap.CompressFormat.JPEG);
        doUpload(new File(f.getPath() + "1"));
    }

    @Override
    public boolean saveToSDCard() {
        return false;
    }

    @Override
    public Integer[] needCropImage(File f) {
        return new Integer[]{TheOneConstants.USER_AVATAR_MAX_SIZE,
                TheOneConstants.USER_AVATAR_MAX_SIZE};
    }

    @Override
    public void onDeletePicture() {

    }

    @Override
    public void onPreviewPicture() {

    }

    @Override
    public void setOriginalPicture(File f, String originalPath) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        m_pictureHelper.processActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void handleStateMessage(Message msg) {

        hideLoadingDialog();

        switch (msg.what) {

            case AccountMessageType.UPDATE_MYUSER_INFO_SUCCESS: {

                startActivity(new Intent(FusionAction.RegisterAction.MAIN_ACTION));

                finish();

                break;
            }
            case AccountMessageType.UPDATE_MYUSER_INFO_FAIL: {

                showToast(R.string.update_myself_error);

                break;
            }
            default:
        }
    }

    private MeInfo createUser() {

        MeInfo user = new MeInfo();

        user.name = nickName.getText().toString();

        user.avatar_url = photoUrl;

        user.character = character;

        user.role = role;

        user.birthday = birthday;

        String height = heightTextView.getText().toString();

        user.height = height.substring(0, height.length() - 2);

        user.region = locationCityView.getText().toString();

        user.marriage = isMarriage.isChecked();

        user.interests = mInterests;

        return user;
    }

    private void doUpload(File file) {

        if (uploading) {

            Log.e(TAG, "in method doUpload, have uploading");

            return;
        }

        uploading = true;

        IO.putFile(FusionConfig.getInstance().getUploadAuthorizer(), null,
                file, null, new CallBack() {

                    @Override
                    public void onProcess(long current, long total) {

                        int percent = (int) (current * 100 / total);

                        showLoadingDialog(getString(R.string.dialog_uploading)
                                + percent + "%");
                    }

                    @Override
                    public void onSuccess(UploadCallRet ret) {

                        uploading = false;

                        photoUrl = FusionConfig.MEDIA_URL_PREFIX + ret.getKey();

                        showSucessDialog(getString(R.string.dialog_upload_success));

                        hideLoadingDialogDelayed();
                    }

                    @Override
                    public void onFailure(CallRet ret) {

                        uploading = false;

                        showFailDialog(getString(R.string.dialog_upload_fail));

                        hideLoadingDialogDelayed();
                    }
                });
    }

    private class InterestsGridAdapter extends BaseAdapter {
        private List<String> mlist = new ArrayList<String>();

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ImageLoaderViewHolder holder;
            if (convertView == null) {
                if (null == inflater) {
                    inflater = LayoutInflater
                            .from(RegisterUserInfoActivity.this);
                }
                convertView = inflater.inflate(R.layout.intent_gridview_item,
                        null);
                holder = new ImageLoaderViewHolder();
                holder.mTextView = (TextView) convertView
                        .findViewById(R.id.intent_name);
                holder.mTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        (int) (width - HelperFunc.dip2px(60)) / 3,
                        (int) HelperFunc.dip2px(40)));
                convertView.setTag(holder);
            } else {
                holder = (ImageLoaderViewHolder) convertView.getTag();
            }
            holder.mTextView.setText(mlist.get(position));

            if (mInterests.contains(holder.mTextView.getText().toString())) {
                holder.mTextView
                        .setBackgroundColor(Color.parseColor("#55C478"));
                holder.mTextView.setTextColor(getResources().getColor(
                        R.color.white));
            } else {
                holder.mTextView.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.profile_interest_btn));
                holder.mTextView.setTextColor(getResources().getColor(
                        R.color.black));
            }

            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mInterests.contains(((TextView) view).getText()
                            .toString())) {
                        mInterests.remove(((TextView) view).getText()
                                .toString());
                    } else {
                        mInterests.add(((TextView) view).getText().toString());
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public void setList(List<String> mlist) {
            this.mlist = mlist;
        }
    }

    // private class PurposeGridAdapter extends BaseAdapter {
    // private List<String> mlist = new ArrayList<String>();
    //
    // @Override
    // public int getCount() {
    // return mlist.size();
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // return position;
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    //
    // @Override
    // public View getView(final int position, View convertView, ViewGroup
    // parent) {
    // final ImageLoaderViewHolder holder;
    // if (convertView == null) {
    // if (null == inflater) {
    // inflater = LayoutInflater.from(RegisterUserInfoActivity.this);
    // }
    // convertView = inflater.inflate(R.layout.intent_gridview_item, null);
    // holder = new ImageLoaderViewHolder();
    // holder.mTextView = (TextView) convertView.findViewById(R.id.intent_name);
    // holder.mTextView.setLayoutParams(new LinearLayout.LayoutParams(
    // (int) (width - HelperFunc.dip2px(60)) / 3, (int) HelperFunc.dip2px(40)));
    // convertView.setTag(holder);
    // } else {
    // holder = (ImageLoaderViewHolder) convertView.getTag();
    // }
    // holder.mTextView.setText(mlist.get(position));
    //
    // if (mPurpose.contains(holder.mTextView.getText().toString())) {
    // holder.mTextView.setBackgroundColor(Color.parseColor("#55C478"));
    // holder.mTextView.setTextColor(getResources().getColor(R.color.white));
    // } else {
    // holder.mTextView.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.profile_interest_btn));
    // holder.mTextView.setTextColor(getResources().getColor(R.color.black));
    // }
    //
    // holder.mTextView.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View view) {
    // if (mPurpose.contains(holder.mTextView.getText().toString())) {
    // mPurpose.remove(holder.mTextView.getText().toString());
    // } else {
    // if (mPurpose.size() >= 3) {
    // return;
    // }
    // mPurpose.add(holder.mTextView.getText().toString());
    // }
    // notifyDataSetChanged();
    // }
    // });
    // return convertView;
    // }
    //
    // public void setList(List<String> mlist) {
    // this.mlist = mlist;
    // }
    // }
}
