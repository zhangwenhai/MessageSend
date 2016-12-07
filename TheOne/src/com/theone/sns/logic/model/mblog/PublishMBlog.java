package com.theone.sns.logic.model.mblog;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.mblog.base.Video;

@SuppressWarnings("serial")
public class PublishMBlog extends BaseModel {

	public String visibility;

	public Photo photo;

	public AudioDesc audio_desc;

	public Video video;

	public String text_desc;

	public List<MBlogTag> tags;

	public List<String> location;
}
