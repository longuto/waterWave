package com.longuto.wave;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyRingWave extends View {

	public MyRingWave(Context context) {
		super(context);
		init();
	}

	public MyRingWave(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyRingWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private List<Wave> mWaveList;	// 圆波纹的集合
	public final int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			flushData();
			invalidate();
			if(!mWaveList.isEmpty()) {
				handler.sendEmptyMessageDelayed(0, 50);				
			}
		}
	};
	
	protected void flushData() {
		List<Wave> removeList = new ArrayList<MyRingWave.Wave>();	// 圆环移除的集合
		// 更新圆环半径,厚度,透明度
		for (Wave wave : mWaveList) {
			int alpha = wave.wavePt.getAlpha();
			if(alpha <= 0) {
				removeList.add(wave);
				continue;
			}
			wave.waveR += 3;
			wave.wavePt.setStrokeWidth(wave.waveR/3);
			alpha -= 5;
			if(alpha < 0) {
				alpha = 0;
			}
			wave.wavePt.setAlpha(alpha);
		}
		mWaveList.removeAll(removeList);	// 移除所有已经透明的圆环
	}
	
	private void init() {
		mWaveList = new ArrayList<MyRingWave.Wave>();
		invalidate();	// 刷新界面
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Wave wave : mWaveList) {
			canvas.drawCircle(wave.waveX, wave.waveY, wave.waveR, wave.wavePt);	// 画圆环
		}
	}
	
	private int startX;		
	private int startY;
	private int dx;	// x偏移量
	private int dy;	// y偏移量
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = 0;
			startY = 0;
		case MotionEvent.ACTION_MOVE:
			if(mWaveList.isEmpty()) {
				handler.sendEmptyMessage(0);
			} 
			dx = (int)event.getX() - startX;
			dy = (int)event.getY() - startY;
			startX = (int) event.getX();
			startY = (int) event.getY();
			if(Math.abs(dx) > 10 || Math.abs(dy) > 10) {
				Wave wave = new Wave();	// 圆对象
				wave.waveX = (int) event.getX();
				wave.waveY = (int) event.getY();
				// 设置画笔
				Paint paint = new Paint();	// 画笔
				int random = (int) (Math.random() * 4);	// [0-4)的随机数
				paint.setColor(colors[random]);	// 设置颜色
				paint.setStyle(Style.STROKE);	// 设置为空心
				paint.setAntiAlias(true);	// 去掉锯齿
				paint.setStrokeWidth(wave.waveR / 3);	// 设置圆环宽度
				paint.setAlpha(255);	// 设置为不透明
				wave.wavePt = paint;
				
				mWaveList.add(wave);	// 添加到集合对象中				
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	class Wave {
		int waveX;	// x轴
		int waveY;	// y轴
		int waveR;	// 半径
		Paint wavePt;	//画笔
	}
	
}
