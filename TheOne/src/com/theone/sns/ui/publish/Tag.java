package com.theone.sns.ui.publish;

import java.io.Serializable;

public class Tag implements Serializable {

	public static final int LABEL_TYPE = 0;

	public static final int PLACE_TYPE = 1;

	public static final int FIGURE_TYPE = 2;

	public Tag(String id, String name, float x, float y, int type) {
		tagName = name;
		this.id = id;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public Tag(String id, String name, String userId, float x, float y, int type) {
		tagName = name;
		this.userId = userId;
		this.id = id;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public Tag(String id, String name, float x, float y) {
		tagName = name;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public String tagName;
	public String userId;
	public float x;
	public float y;
	public int type = -1;
	public String id;
    public String align;
}
