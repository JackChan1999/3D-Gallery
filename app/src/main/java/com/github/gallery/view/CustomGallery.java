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
