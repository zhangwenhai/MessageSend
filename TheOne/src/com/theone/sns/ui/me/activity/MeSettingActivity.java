package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.CommonField;
import com.theone.sns.common.FusionAction.MeAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.Role;
import com.theone.sns.common.FusionCode.UserCharacter;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.component.upload.io.IO;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.UploadCallRet;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.model.user.MeInfo;
import com.theone.sns.logic.model.user.User;
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
 * Created by zhangwenhai on 2014/9/22.
 */
public class MeSettingActivity extends IphoneTitleActivity implements
        PictureCallback {

    private static final int NICKNAMEREQUESTCODE = 1024;

    private static final int IPHONE_PREVIEW = 0;
    private static final int IPHONE_CAMERA = 1;
    private static final int IPHONE_GALLERY = 2;
    private static final int IPHONE_DELETE = 3;
    private static final int IPHONE_CANCEL = 4;

    private static final int ROLE_T = 1;
    private static final int ROLE_P = 2;
    private static final int ROLE_H = 3;
    private static final int ROLE_UNKNOWN = 4;
    private static final int ROLE_CANCEL = 5;

    private RelativeLayout touxiangView;
    private RelativeLayout nicknameView;
    private ImageView touxiangImageView;
    private TextView nicknameDisTextView;
    private ImageView roleDisImageView;
    private TextView roleUnknownTextView;
    private RelativeLayout roleImageView;
    private RelativeLayout birthdayView;
    private RelativeLayout heightView;
    private RelativeLayout areaView;
    private SwitchButton marriage;
    private GridView interestsGridView;
    private InterestsGridAdapter mInterestsGridAdapter;
    private LayoutInflater inflater;
    private int width;
    private IAccountLogic mIAccountLogic;
    private User mUser;
    private int height;
    private int with;

    private PictureHelper m_pictureHelper = null;
    private IphoneStyleAlertDialogBuilder m_iphoneDialog;
    private IphoneStyleAlertDialogBuilder m_chooseRoleDialog;
    private String photoUrl;
    private boolean uploading = false;

    private TextView birthdayDisTextView;
    private TextView heightDisTextView;
    private TextView areaDisTextView;
    private List<String> mAllInterests = new ArrayList<String>();
    private List<String> mInterests = new ArrayList<String>();
    private PickerHeightWidget mPickerHeightWidget;
    private PickerBirthdayWidget mPickerBirthday;
    private PickerCityWidget mPickerCityWidget;

    private String role1 = "";
    private String role2 = "";
    private String role3 = "";
    private String role4 = "";

    private String userRole = "";

    private String birthday;

    @Override
    protected void initLogics() {
        mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.me_setting_main);

        getView();

        setView();

        setListener();
    }

    private void getView() {
        width = getResources().getDisplayMetrics().widthPixels;
        with = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        touxiangView = (RelativeLayout) findViewById(R.id.touxiang_view);
        nicknameView = (RelativeLayout) findViewById(R.id.nickname_view);
        birthdayView = (RelativeLayout) findViewById(R.id.birthday_view);
        heightView = (RelativeLayout) findViewById(R.id.height_view);
        areaView = (RelativeLayout) findViewById(R.id.area_view);
        marriage = (SwitchButton) findViewById(R.id.marriage);
        touxiangImageView = (ImageView) findViewById(R.id.touxiang);
        nicknameDisTextView = (TextView) findViewById(R.id.nickname_dis);
        roleDisImageView = (ImageView) findViewById(R.id.role_dis);
        roleUnknownTextView = (TextView) findViewById(R.id.role_dis_text);
        roleImageView = (RelativeLayout) findViewById(R.id.role_view);
        birthdayDisTextView = (TextView) findViewById(R.id.birthday_dis);
        heightDisTextView = (TextView) findViewById(R.id.height_dis);
        areaDisTextView = (TextView) findViewById(R.id.area_dis);
        interestsGridView = (GridView) findViewById(R.id.interests);
    }

    private void setView() {
        setTitle(R.string.me_message);
        setLeftButton(R.drawable.icon_back, false, false);
        setRightButton(R.string.save, true);

        mAllInterests = FusionConfig.getInstance().getAccount().config.interests;
        List<String> purposes = FusionConfig.getInstance().getAccount().config.purposes;
        purposes.addAll(mAllInterests);
        if (null != purposes && purposes.size() > 0) {
            mInterestsGridAdapter = new InterestsGridAdapter();
            mInterestsGridAdapter.setList(purposes);
            interestsGridView.setAdapter(mInterestsGridAdapter);

            int hang = 0;
            if (mAllInterests.size() % 3 == 0) {
                hang = mAllInterests.size() / 3;
            } else {
                hang = mAllInterests.size() / 3 + 1;
            }

            interestsGridView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    (int) ((hang - 1) * HelperFunc.dip2px(20)
                            + HelperFunc.dip2px(20) + hang
                            * HelperFunc.dip2px(40))));
        }

        m_pictureHelper = new PictureHelper(this, this);

        User myUser = mIAccountLogic.getMyUserInfoFromDB();
        if (null != myUser) {
            mUser = myUser;
            setViewUser();
        } else {
            mUser = new User();
        }

        initRole();
    }

    private void setViewUser() {

        ImageLoader.getInstance().displayImage(mUser.avatar_url,
                touxiangImageView, options);

        nicknameDisTextView.setText(mUser.name);

        areaDisTextView.setText(mUser.region);

        if (mUser.marriage) {
            marriage.setChecked(true);
        } else {
            marriage.setChecked(false);
        }

        if (TextUtils.isEmpty(mUser.height)) {
            heightDisTextView.setText(0 + getString(R.string.height_cm));
        } else {
            heightDisTextView.setText(mUser.height
                    + getString(R.string.height_cm));
        }

        if (!TextUtils.isEmpty(mUser.birthday)) {

            birthday = PrettyDateFormat.getBirthday(mUser.birthday);

            String signStr = "";

            if (!TextUtils.isEmpty(mUser.sign)) {

                signStr = "(" + mUser.sign + ")";
            }

            birthdayDisTextView.setText(birthday + signStr);

        } else {

            birthday = birthdayDisTextView.getText() + "";

            String signStr = "(" + PrettyDateFormat.getConstellation(3, 22)
                    + ")";

            birthdayDisTextView.setText(birthday + signStr);
        }

        List<String> interestList = mUser.interests;

        if ((null != mAllInterests && mAllInterests.size() > 0)
                && (null != interestList && interestList.size() > 0)) {

            for (String interestTemp : interestList) {

                if (mAllInterests.contains(interestTemp)
                        && !mInterests.contains(interestTemp)) {

                    mInterests.add(interestTemp);
                }
            }
        }

        setRole(mUser.role);
    }

    private MeInfo updateUser() {

        MeInfo mUser = new MeInfo();

        mUser.name = nicknameDisTextView.getText().toString();

        if (!TextUtils.isEmpty(photoUrl)) {
            mUser.avatar_url = photoUrl;
        }

        if (getString(R.string.role_server_unknow).equals(userRole)
                || getString(R.string.role_unknow).equals(userRole)) {

            userRole = getString(R.string.role_server_unknow);
        }

        mUser.role = userRole;

        mUser.birthday = birthday;

        String height = heightDisTextView.getText().toString();

        mUser.height = height.substring(0, height.length() - 2);

        mUser.region = areaDisTextView.getText().toString();

        mUser.marriage = marriage.isChecked();

        mUser.interests = mInterests;

        return mUser;
    }

    private void setListener() {

        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLoadingDialog(getString(R.string.dialog_save));

                mIAccountLogic.updateMyUserInfo(updateUser());
            }
        });

        heightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerHeight();
            }
        });

        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerBirthday();
            }
        });

        areaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerCity();
            }
        });

        nicknameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeAction.NICKNAME_ACTION);

                intent.putExtra(CommonField.USER_NAME_KEY, mUser.name);

                startActivityForResult(intent, NICKNAMEREQUESTCODE);
            }
        });

        roleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRoleChooseDialog();
            }
        });

        findViewById(R.id.intent_view).setOnClickListener(
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

        touxiangView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIphoneDialog();
            }
        });
    }

    protected void pickerHeight() {
        String s = heightDisTextView.getText().toString();
        mPickerHeightWidget = new PickerHeightWidget(this, Integer.valueOf(s
                .substring(0, s.length() - 2)));
        mPickerHeightWidget
                .setPickerCallBack(new PickerHeightWidget.IPickerHeightCallback() {
                    @Override
                    public void onPickerFinish(int height) {
                        height = height + 20;
                        heightDisTextView.setText(height + "cm");
                        mPickerHeightWidget = null;
                    }

                    @Override
                    public void onPickerCancel(int height) {
                        mPickerHeightWidget = null;
                    }
                });
        mPickerHeightWidget.show();
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

                        birthdayDisTextView.setText(birthday + signStr);

                        mPickerBirthday = null;
                    }

                    @Override
                    public void onPickerCancel(int year, int month, int day) {
                        mPickerBirthday = null;
                    }
                });
        mPickerBirthday.show();
    }

    private void pickerCity() {
        mPickerCityWidget = new PickerCityWidget(this);
        mPickerCityWidget
                .setPickerCallBack(new PickerCityWidget.IPickerBirthdayCallback() {

                    @Override
                    public void onPickerFinish(String province, String city) {
                        areaDisTextView.setText(province + " " + city);
                        mPickerCityWidget = null;
                    }

                    @Override
                    public void onPickerCancel(String province, String city) {
                        mPickerCityWidget = null;
                    }
                });
        mPickerCityWidget.show();
    }

    @Override
    protected void handleStateMessage(Message msg) {

        hideLoadingDialog();

        switch (msg.what) {

            case AccountMessageType.UPDATE_MYUSER_INFO_SUCCESS: {

                setResult(RESULT_OK, new Intent());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        m_pictureHelper.processActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case NICKNAMEREQUESTCODE: {

                if (null == data) {
                    return;
                }

                String name = data.getStringExtra(CommonField.USER_NAME_KEY);

                if (!TextUtils.isEmpty(name)) {

                    mUser.name = name;

                    nicknameDisTextView.setText(mUser.name);
                }

                return;
            }
            default:
        }
    }

    private class InterestsGridAdapter extends BaseAdapter {
        private List<String> mlist;

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
                    inflater = LayoutInflater.from(MeSettingActivity.this);
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

    private void showIphoneDialog() {
        if (m_iphoneDialog == null) {
            m_iphoneDialog = new IphoneStyleAlertDialogBuilder(this);
            m_iphoneDialog.setTitle(R.string.add_a_photo);
            m_iphoneDialog.addItem(IPHONE_CAMERA,
                    getString(R.string.take_photo), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
                            startActivityForResult(new Intent(FusionAction.PublicAction.PHOTO_ACTION),
                                    PictureHelper.PHOTO_RESULT);
//							m_pictureHelper.getPictureFromCamera();
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

    private void showRoleChooseDialog() {

        if (m_chooseRoleDialog == null) {

            m_chooseRoleDialog = new IphoneStyleAlertDialogBuilder(this);

            m_chooseRoleDialog.addItem(ROLE_T, role1, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_chooseRoleDialog.dismiss();
                    setRole(role1);
                }
            });
            m_chooseRoleDialog.addItem(ROLE_P, role2, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_chooseRoleDialog.dismiss();
                    setRole(role2);
                }
            });
            m_chooseRoleDialog.addItem(ROLE_H, role3, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_chooseRoleDialog.dismiss();
                    setRole(role3);
                }
            });

            m_chooseRoleDialog.addItem(ROLE_UNKNOWN, role4, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_chooseRoleDialog.dismiss();
                    setRole(role4);
                }
            });

            m_chooseRoleDialog.addItem(ROLE_CANCEL, getString(R.string.cancel),
                    IphoneStyleAlertDialogBuilder.COLOR_BLUE,
                    IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_chooseRoleDialog.dismiss();
                        }
                    });
        }
        m_chooseRoleDialog.show();
    }

    private void initRole() {

        String character = FusionConfig.getInstance().getAccount().profile.character;

        if (UserCharacter.GAY.equals(character)) {

            role1 = Role.MT;

            role2 = Role.MP;

            role3 = Role.MH;

        } else if (UserCharacter.LES.equals(character)) {

            role1 = Role.T;

            role2 = Role.P;

            role3 = Role.H;
        }

        role4 = getString(R.string.role_unknow);
    }

    private void setRole(String role) {

        userRole = role;

        if (getString(R.string.role_unknow).equals(role)
                || getString(R.string.role_server_unknow).equals(role)) {
            roleUnknownTextView.setVisibility(View.VISIBLE);
            roleDisImageView.setVisibility(View.GONE);
        } else {
            roleUnknownTextView.setVisibility(View.GONE);
            roleDisImageView.setVisibility(View.VISIBLE);
        }

        if (FusionCode.Role.H.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_h_icon));
        } else if (FusionCode.Role.T.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_t_icon));
        } else if (FusionCode.Role.P.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_p_icon));
        } else if (FusionCode.Role.MH.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_0_icon));
        } else if (FusionCode.Role.MT.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_1_icon));
        } else if (FusionCode.Role.MP.equals(role)) {
            roleDisImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_0_5_icon));
        } else if (getString(R.string.role_unknow).equals(role)
                || getString(R.string.role_server_unknow).equals(role)) {
            roleUnknownTextView.setText(R.string.role_unknow);
        }
    }

    private void doUpload(File file) {

        if (uploading) {

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

    @Override
    public void setPicture(File f) {
        if (null == f) {
            return;
        }

        touxiangImageView
                .setImageBitmap(ImageLoaderUtil.decodeFile(f.getPath()));

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
        touxiangImageView.setImageBitmap(resizedBitmap);
        ImageLoaderUtil.saveBitmap(resizedBitmap, f.getPath() + "1",
                TheOneConstants.PUBLIC_PICTURE_QUALITY, Bitmap.CompressFormat.JPEG);
        doUpload(new File(f.getPath() + "1"));
    }

    @Override
    public boolean saveToSDCard() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer[] needCropImage(File f) {
        return new Integer[]{TheOneConstants.USER_AVATAR_MAX_SIZE,
                TheOneConstants.USER_AVATAR_MAX_SIZE};
    }

    @Override
    public void onDeletePicture() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPreviewPicture() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOriginalPicture(File f, String originalPath) {
        // TODO Auto-generated method stub

    }
}
