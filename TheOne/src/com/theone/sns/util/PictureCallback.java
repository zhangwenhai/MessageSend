package com.theone.sns.util;

import java.io.File;

public interface PictureCallback {

	public void setPicture(final File f);

	public void setPicture(final File f, int angle, int camera);

	public boolean saveToSDCard();

	public Integer[] needCropImage(final File f);

	public void onDeletePicture();

	public void onPreviewPicture();

	public void setOriginalPicture(final File f, final String originalPath);

}
