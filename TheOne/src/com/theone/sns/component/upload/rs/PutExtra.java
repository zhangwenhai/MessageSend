package com.theone.sns.component.upload.rs;

import java.util.HashMap;

public class PutExtra {

	public final static int UNUSE_CRC32 = 0;

	public final static int AUTO_CRC32 = 1;

	public final static int SPECIFY_CRC32 = 2;

	// 用户自定义参数，key必须以 "x:" 开头
	public HashMap<String, String> params = new HashMap<String, String>();

	public String mimeType;

	public long crc32;

	public int checkCrc = UNUSE_CRC32;
}
