# Gallery 3D 画廊

![](https://github.com/JackChen1999/Gallery3D/blob/master/art/3dGallery1.png) ![](https://github.com/JackChen1999/Gallery3D/blob/master/art/3dGallery2.png)

# Gallery

> This class was deprecated in API level 16.
This widget is no longer supported. Other horizontally scrolling widgets include HorizontalScrollView and ViewPager from the support library.

```java
package com.github.gallery.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class CustomGallery extends Gallery {

	private int galleryCenterPoint = 0;	// gallery的中心点
	private Camera camera;

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 启用getChildStaticTransformation被调用
		setStaticTransformationsEnabled(true);

		camera = new Camera();
	}

	/**
	 * 当gallery控件的宽和高改变时回调此方法, 第一次计算出gallery的宽和高时, 也会出发此方法
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		galleryCenterPoint = getGalleryCenterPoint();
	}

	/**
	 * 返回gallery的item的子图形变换效果
	 * Transformation 指定当前item的变换效果
	 */
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		int viewCenterPoint = getviewCenterPoint(child);	// item的中心点
		int rotateAngle = 0;		// 默认旋转角度为0

		// 如果当前的View的中心点不等于gallery的中心点, 就是两边的图片, 需要计算旋转角度
		if(viewCenterPoint != galleryCenterPoint) {
			// gallery中心点 - 图片中心点 = 差值
			int diff = galleryCenterPoint - viewCenterPoint;

			// 差值 / 图片的宽度 = 比值
			float scale = (float)diff / (float)child.getWidth();

			// 比值 * 最大旋转角度 = 最终的旋转角度
			rotateAngle = (int) (scale * 50);

			if(Math.abs(rotateAngle) > 50) {		// 当前角度超过了50, 需要赋值到50 或者 -50
				rotateAngle = rotateAngle > 0 ? 50 : -50;
			}
		}

		// 设置变换效果之前, 需要把Transformation中的上一个item的变换效果清楚
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);	// 设置变换效果的类型为矩阵类型

		startTransformationItem((ImageView) child, rotateAngle, t);
		return true;
	}

	/**
	 * 设置变换效果
	 * @param iv gallery的item
	 * @param rotateAngle 旋转的角度
	 * @param t 变换的对象
	 */
	private void startTransformationItem(ImageView iv, int rotateAngle, Transformation t) {
		camera.save();		// 保存状态

		int absRotateAngle = Math.abs(rotateAngle);	// 取旋转角度的绝对值

		// 3. 放大效果(中间的图片要比两边的图片要大)
		camera.translate(0, 0, 100f);		// 给摄像机定位

		int zoom = -250 + (absRotateAngle * 2);
		camera.translate(0, 0, zoom);

		// 2. 透明度(中间的图片是完全显示, 两边有一定的透明度)
		int alpha = (int) (255 - (absRotateAngle * 2.5));
		iv.setAlpha(alpha);		// 透明度取值范围: 0 ~ 255, 0 就是完全隐藏, 255 完全显示

		// 1. 旋转(在中间的图片没有旋转角度, 只要不在中间就有旋转角度)
		camera.rotateY(rotateAngle);

		Matrix matrix = t.getMatrix(); 	// 变换的矩阵, 需要把变换的效果添加到矩阵中

		// 给matrix赋值
		camera.getMatrix(matrix);		// 把matrix矩阵给camera对象, camera对象就会把上面添加的效果转换成矩阵添加到matrix对象中

		// 矩阵前乘
		matrix.preTranslate(-iv.getWidth() / 2, -iv.getHeight() / 2);

		// 矩阵后乘
		matrix.postTranslate(iv.getWidth() / 2, iv.getHeight() / 2);

		camera.restore(); // 恢复到之前保存的状态
	}

	/**
	 * 获得gallery的中心点
	 * @return
	 */
	private int getGalleryCenterPoint() {
		return this.getWidth() / 2;
	}

	/**
	 * 获得view的中心点
	 * @return
	 */
	private int getviewCenterPoint(View v) {
		return v.getWidth() / 2 + v.getLeft();		// 图片的宽度的一半 + 图片左边在父控件中的位置
	}
}

```
# ImageUtils
```java
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

```

# MainActivity

```java
package com.github.gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.github.gallery.view.CustomGallery;


public class MainActivity extends Activity {

	private int[] imageResIDs;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CustomGallery mGallery = (CustomGallery) findViewById(R.id.customgallery);

		imageResIDs = new int[] {
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8,
				R.drawable.pic_1,
				R.drawable.pic_2,
				R.drawable.pic_3,
				R.drawable.pic_4,
				R.drawable.pic_5,
				R.drawable.pic_6,
				R.drawable.pic_7,
				R.drawable.pic_8
		};

		mGallery.setAdapter(new MyAdapter());
	}


	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return imageResIDs.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = null;
			if(convertView == null) {
				iv = new ImageView(MainActivity.this);
			} else {
				iv = (ImageView) convertView;
			}

			Bitmap bitmap = ImageUtils.getImageBitmap(MainActivity.this.getResources(), imageResIDs[position]);

			BitmapDrawable bd = new BitmapDrawable(bitmap);
			bd.setAntiAlias(true);		// 消除锯齿

			iv.setImageDrawable(bd);
			LayoutParams params = new LayoutParams(dip2Px(160), dip2Px(240));
			iv.setLayoutParams(params);
			return iv;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	public int dip2Px(int dip) {
		float density = getResources().getDisplayMetrics().density;
		int px = (int) (dip * density + 0.5f);
		return px;
	}

	public int px2Dip(int px) {
		float density = getResources().getDisplayMetrics().density;
		int dip = (int) (px / density + 0.5f);
		return dip;
	}
}

```