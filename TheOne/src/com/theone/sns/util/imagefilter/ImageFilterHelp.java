package com.theone.sns.util.imagefilter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by zhangwenhai on 2014/9/16.
 */
public class ImageFilterHelp {

	/**
	 * SeekBar的中间值
	 */
	private static final int MIDDLE_VALUE = 128;

	/**
	 * 将彩色图转换为黑白图
	 *
	 * @param bmp
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return newBmp;
	}

	/**
	 * 设置图片 色相 饱和度 亮度
	 * 
	 * @param bm
	 * @param sexiang
	 * @param baohedu
	 * @param liangdu
	 * @return
	 */
	public static Bitmap setImage(Bitmap bm, int sexiang, int duibidu, int baohedu, int liangdu) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		// 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
		Canvas canvas = new Canvas(bmp); // 得到画笔对象
		Paint paint = new Paint(); // 新建paint
		paint.setAntiAlias(true); // 设置抗锯齿,也即是边缘做平滑处理
		ColorMatrix mAllMatrix = new ColorMatrix();
		ColorMatrix mLightnessMatrix = new ColorMatrix(); // 用于颜色变换的矩阵，android位图颜色变化处理主要是靠该对象完成
		ColorMatrix mSaturationMatrix = new ColorMatrix();
		ColorMatrix mHueMatrix = new ColorMatrix();
		ColorMatrix cMatrix = new ColorMatrix();
		// 需要改变色相
		mHueMatrix.reset();
		mHueMatrix.setScale((sexiang + MIDDLE_VALUE) * 1.0F / MIDDLE_VALUE,
				(sexiang + MIDDLE_VALUE) * 1.0F / MIDDLE_VALUE, (sexiang + MIDDLE_VALUE) * 1.0F
						/ MIDDLE_VALUE, 1); // 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
		float contrast = (float) ((duibidu + 64) / 128.0);
		cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0,// 改变对比度
				0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

		// 需要改变饱和度
		// saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
		// 为1表示饱和度不变，设置大于1，就显示过饱和
		mSaturationMatrix.reset();
		mSaturationMatrix.setSaturation((baohedu + 100) * 1.0F / 100);
		// 亮度
		// hueColor就是色轮旋转的角度,正值表示顺时针旋转，负值表示逆时针旋转
		// mLightnessMatrix.reset(); // 设为默认值
		// mLightnessMatrix.setRotate(0, (liangdu) * 1.0F / 100 * 180); //
		// 控制让红色区在色轮上旋转的角度
		// mLightnessMatrix.setRotate(1, (liangdu) * 1.0F / 100 * 180); //
		// 控制让绿红色区在色轮上旋转的角度
		// mLightnessMatrix.setRotate(2, (liangdu) * 1.0F / 100 * 180); //
		// 控制让蓝色区在色轮上旋转的角度
		mLightnessMatrix.set(new float[] { 1, 0, 0, 0, liangdu, 0, 1, 0, 0, liangdu,// 改变亮度
				0, 0, 1, 0, liangdu, 0, 0, 0, 1, 0 });

		// 这里相当于改变的是全图的色相
		mAllMatrix.reset();
		mAllMatrix.postConcat(cMatrix);
		mAllMatrix.postConcat(mHueMatrix);
		mAllMatrix.postConcat(mSaturationMatrix); // 效果叠加
		mAllMatrix.postConcat(mLightnessMatrix); // 效果叠加

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// 设置颜色变换效果
		canvas.drawBitmap(bm, 0, 0, paint); // 将颜色变化后的图片输出到新创建的位图区
		// 返回新的位图，也即调色处理后的图片
		return bmp;
	}

	/**
	 * 图片RGB调整
	 * 
	 * @param bm
	 * @param R
	 * @param G
	 * @param B
	 * @param a
	 * @return
	 */
	public static Bitmap setImageColor(Bitmap bm, int R, int G, int B, int a) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp); // 得到画笔对象
		Paint paint = new Paint(); // 新建paint
		float progressR = (R + MIDDLE_VALUE) / 128f;
		float progressG = (G + MIDDLE_VALUE) / 128f;
		float progressB = (B + MIDDLE_VALUE) / 128f;
		float progressA = (a + MIDDLE_VALUE) / 128f;
		float[] src = new float[] { progressR, 0, 0, 0, 0, 0, progressG, 0, 0, 0, 0, 0, progressB,
				0, 0, 0, 0, 0, progressA, 0 };
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(src);
		paint.setColorFilter(new ColorMatrixColorFilter(src));
		canvas.drawBitmap(bm, new Matrix(), paint);
		return bmp;
	}

	/**
	 * 设置颜色滤镜
	 * 
	 * @param bm
	 * @param R
	 * @param G
	 * @param B
	 * @param a
	 * @return
	 */
	public static Bitmap setImageColorRGB(Bitmap bm, int R, int G, int B, int a) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp); // 得到画笔对象
		Paint paint = new Paint(); // 新建paint
		float progressR = R / 255f;
		float progressG = G / 255f;
		float progressB = B / 255f;
		float progressA = a / 255f;
		float[] src = new float[] { progressR, 0, 0, 0, 0, 0, progressG, 0, 0, 0, 0, 0, progressB,
				0, 0, 0, 0, 0, progressA, 0 };
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(src);
		paint.setColorFilter(new ColorMatrixColorFilter(src));
		canvas.drawBitmap(bm, new Matrix(), paint);
		return bmp;
	}

	/**
	 * 图片锐化（拉普拉斯变换）
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		float alpha = 0.3F;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + n) * width + k + m];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 高斯模糊
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 16; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
