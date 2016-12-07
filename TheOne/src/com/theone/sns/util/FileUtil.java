package com.theone.sns.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.text.TextUtils;

import com.theone.sns.common.FusionConfig;

/**
 * 文件处理工具类
 */
public class FileUtil {

	private static Object object = new Object();

	/**
	 * 获取保存文件目录
	 * 
	 * @return 文件路径
	 */
	public static String getSavePath() {
		if (MemoryStatus.externalMemoryAvailable()) {
			return FusionConfig.SAVE_IMAGE_EXTERNAL_PATH;
		} else {
			return FusionConfig.SAVE_IMAGE_INTERNAL_PATH;
		}
	}

    /**
     * 获取保存视频文件目录
     *
     * @return 文件路径
     */
    public static String getVideoSavePath() {
        if (MemoryStatus.externalMemoryAvailable()) {
            return FusionConfig.SAVE_VIDEO_EXTERNAL_PATH;
        } else {
            return FusionConfig.SAVE_VIDEO_INTERNAL_PATH;
        }
    }

	/**
	 * 通过提供的文件名在默认路径下生成文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param isNextPath
	 *            暂时未使用
	 * @return 生成的文件
	 * @throws IOException
	 */
	public static File createFile(String filePath, boolean isNextPath) {
		synchronized (object) {
			if (TextUtils.isEmpty(filePath)) {
				return null;
			}
			File file = createFolder(filePath);
			try {
				file.createNewFile();
			} catch (Exception e) {
			}
			return file;
		}
	}

	/**
	 * 将一个路径的图片保存到另一个路径，相当于复制的过程
	 *
	 * @param srcFilePath
	 *            源文件路径
	 * @param dstFilePath
	 *            目标文件的相对路径
	 * @return 保存后的文件
	 */
	public static File fileCopy(String srcFilePath, String dstFilePath) {
		if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(dstFilePath)) {
			return null;
		}
		// 目标文件的绝对路径
		if (!new File(srcFilePath).exists()) {
			return null;
		}
		File file = createFile(dstFilePath, false);
		fileRW(srcFilePath, dstFilePath);
		return file;
	}

	/**
	 * 如果存在文件先删除，然后创建
	 *
	 * @param filePath
	 *            文件路径
	 * @return 文件对象
	 */
	private static File createFolder(String filePath) {
		String folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
		File folder = getFileByPath(folderPath);
		folder.mkdirs();
		File file = getFileByPath(filePath);

		if (file.exists()) {
			file.delete();
		}
		return file;
	}

	/**
	 * 根据文件路径创建文件
	 *
	 * @param filePath
	 *            文件路径
	 * @return 文件对象
	 */
	public static File getFileByPath(String filePath) {
		filePath = filePath.replaceAll("\\\\", "/");
		boolean isSdcard = false;
		int subIndex = 0;
		if (filePath.indexOf("/sdcard") == 0) {
			isSdcard = true;
			subIndex = 7;
		} else if (filePath.indexOf("/mnt/sdcard") == 0) {
			isSdcard = true;
			subIndex = 11;
		}

		if (isSdcard) {
			if (isExistSdcard()) {
				// 获取SDCard目录,2.2的时候为:/mnt/sdcard
				// 2.1的时候为：/sdcard，所以使用静态方法得到路径会好一点。
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = filePath.substring(subIndex);
				return new File(sdCardDir, fileName);
			} else if (isEmulator()) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = filePath.substring(subIndex);
				return new File(sdCardDir, fileName);
			}
			return null;
		} else {
			return new File(filePath);
		}
	}

	private static boolean isExistSdcard() {
		if (!isEmulator()) {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		}
		return true;
	}

	private static boolean isEmulator() {
		return android.os.Build.MODEL.equals("sdk");
	}

	/**
	 * 复制文件
	 *
	 * @param srcFilePath
	 *            源文件路劲
	 * @param dstFilePath
	 *            目标文件路劲
	 */
	private static void fileRW(String srcFilePath, String dstFilePath) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(srcFilePath);
			outputStream = new FileOutputStream(dstFilePath);
			byte[] b = new byte[1024 * 1024];
			int count;
			while ((count = inputStream.read(b)) != -1) {
				outputStream.write(b);
			}
			outputStream.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != outputStream) {
					outputStream.close();
				}

				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
