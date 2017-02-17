package com.github.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

public class ImageUtils {

	private static final String TAG = "ImageUtils";
	// 缓存集合
	private static Hashtable<Integer, SoftReference<Bitmap>>
								mCacheHashTable = new Hashtable<Integer, SoftReference<Bitmap>>();

	/**
	 * 根据id返回一个处理后的图片
	 * @param res
	 * @param imageID
	 * @return
	 */
	public static Bitmap getImageBitmap(Resources res, int imageID) {

		// 先去集合中取当前imageID 是否已经拿过图片, 如果集合中有, 说明已经拿过, 直接使用集合中的图片返回
		SoftReference<Bitmap> softReference = mCacheHashTable.get(imageID);
		if(softReference != null) {
			Bitmap bitmap = softReference.get();

			if(bitmap != null) {
				// 从内存中取
				Log.i(TAG, "从内存中取");
				return bitmap;
			}
		}

		// 如果集合中没有, 就调用getInvertImage得到一个图片, 需要向集合中保留一张, 最后返回当前图片
		Log.i(TAG, "重新加载");
		Bitmap invertImage = getInvertImage(res, imageID);
		// 在集合中存一份, 便于下次再取的时候直接去集合中取.
		mCacheHashTable.put(imageID, new SoftReference<Bitmap>(invertImage));

		return invertImage;
	}

	/**
	 * 根据id返回一个处理后的图片
	 * @param imageID
	 * @return
	 */
	public static Bitmap getInvertImage(Resources res, int imageID) {
		// 获取原图
		Bitmap sourceBitmap = BitmapFactory.decodeResource(res, imageID);

		// 生成倒影图片
		Matrix m = new Matrix();		// 图形矩阵
		m.setScale(1f, -1f);		// 让图形按照矩阵进行垂直反转

		//		float[] values = {
		//				1.0f, 0f, 0f,
		//				0f, -1.0f, 0f,
		//				0f, 0f, 1.0f
		//		};
		//		m.setValues(values);

		Bitmap invertBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() / 2,
				sourceBitmap.getWidth(), sourceBitmap.getHeight() / 2, m, false);

		// 把两张图片合成一张
		Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				(int) (sourceBitmap.getHeight() * 1.5 + 5), Config.ARGB_8888);

		Canvas canvas = new Canvas(resultBitmap);		// 指定画板画在合成图片上

		canvas.drawBitmap(sourceBitmap, 0, 0, null);	// 把原图画在合成图片的上面

		canvas.drawBitmap(invertBitmap, 0, sourceBitmap.getHeight() + 5, null);		// 把倒影图片画在合成图片上

		// 添加遮罩效果
		Paint paint = new Paint();

		// 设置颜色
		LinearGradient shader = new LinearGradient(0, sourceBitmap.getHeight() + 5,
				0, resultBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);

		// 设置模式为: 遮罩, 是取交集
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, sourceBitmap.getHeight() + 5, sourceBitmap.getWidth(), resultBitmap.getHeight(), paint);

		return resultBitmap;
	}
}
