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
