package com.example.imagetovideo;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import com.example.screen_shot_turbo.Exec;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.avutil;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import android.R.integer;
import android.R.mipmap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	public String IMAGE_PATH="/sdcard/0image/";
	
	private ImageView mPreviewView;
	private Button mTestButton;
	private Button m1ImageToVideoButton;
	private Button mStartRecordButton, mFinishRecordButton;
	private Button mCaptureButton;
	private Button mOpenRecordButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_main);
		
		initWidget();
		
		Intent intent=new Intent(MainActivity.this, MainService.class);
		startService(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Intent intent=new Intent(MainActivity.this, MainService.class);
		stopService(intent);
	}

	private void initWidget() {
		mPreviewView=(ImageView)findViewById(R.id.iv_preview);
		mTestButton=(Button)findViewById(R.id.btn_load_image);
		m1ImageToVideoButton=(Button)findViewById(R.id.btn_1_image_to_video);
		mStartRecordButton=(Button)findViewById(R.id.btn_start_record);
		mFinishRecordButton=(Button)findViewById(R.id.btn_finish_record);
		mCaptureButton=(Button)findViewById(R.id.btn_capture);
		mOpenRecordButton=(Button)findViewById(R.id.btn_open_record);
		
		mTestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				File file=new File(IMAGE_PATH+"img000.jpg");
				byte[] byteBuffer;
				FileInputStream inputStream;
				Bitmap image;
				try {
					inputStream=new FileInputStream(file);
					byteBuffer=new byte[inputStream.available()];
					inputStream.read(byteBuffer);
					image=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
					mPreviewView.setImageBitmap(image);
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		m1ImageToVideoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				File file=new File(IMAGE_PATH+"img000.jpg");
				byte[] byteBuffer;
				FileInputStream inputStream;
				Bitmap image=null;
				try {
					inputStream=new FileInputStream(file);
					byteBuffer=new byte[inputStream.available()];
					inputStream.read(byteBuffer);
					image=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(new File("/sdcard/0image/0.mp4"), 
						image.getWidth(), image.getHeight());
				recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
				recorder.setVideoQuality(0.25);
//				recorder.setSampleRate(44100);
				recorder.setFrameRate(1);
				try {
					recorder.start();
				} catch (com.googlecode.javacv.FrameRecorder.Exception e1) {
					e1.printStackTrace();
				}
				
				IplImage iplImage;
				iplImage=IplImage.create(image.getWidth(), image.getHeight(), opencv_core.IPL_DEPTH_8U, 4);
				image.copyPixelsToBuffer(iplImage.getByteBuffer());
				for(int i=0;i<100;i++) {
					recorder.setTimestamp(1000000*i); // 微秒为单位
					try {
						recorder.record(iplImage);
					} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
						e.printStackTrace();
					}
				}
				try {
					recorder.stop();
					recorder.release();
				} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		mStartRecordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				mRecordThread.mRun=true;
//				mRecordThread.start();
				Intent intent=new Intent(MainService.ACTION_RECORD_SCREEN);
				intent.putExtra(MainService.THREAD_RECORD_RUN, true);
				sendBroadcast(intent);
			}
		});
		
		mFinishRecordButton.setOnClickListener(new OnClickListener() {
			
//			@Override
			public void onClick(View arg0) {
//				mRecordThread.mRun=false;
				Intent intent=new Intent(MainService.ACTION_RECORD_SCREEN);
				intent.putExtra(MainService.THREAD_RECORD_RUN, false);
				sendBroadcast(intent);
			}
		});
		
		mCaptureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Exec exec=new Exec();
				Exec.upgradeRootPermission(getPackageCodePath());
				
				int screenWidth=getWindowManager().getDefaultDisplay().getWidth();
				int screenHeight=getWindowManager().getDefaultDisplay().getHeight();
				byte[] byteBuffer=new byte[screenWidth*screenHeight/2];
				exec.test(byteBuffer, 0); // jpeg data
				
				Bitmap bitmap=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
				mPreviewView.setImageBitmap(bitmap);
			}
		});
		
		mOpenRecordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("/sdcard/000.mp4"), "video/mp4");
                startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
//	RecordThread mRecordThread=new RecordThread();
	
	class RecordThread extends Thread {
		public boolean mRun=true;

		@Override
		public void run() {
			super.run();
			
			Exec exec=new Exec();
			Exec.upgradeRootPermission(getPackageCodePath());
			
			int screenWidth=getWindowManager().getDefaultDisplay().getWidth();
			int screenHeight=getWindowManager().getDefaultDisplay().getHeight();
			byte[] byteBuffer=new byte[screenWidth*screenHeight/16];
			exec.test(byteBuffer, 0); // jpeg data
			
			Bitmap bitmap=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
//			mPreviewView.setImageBitmap(bitmap);
			
//			File file=new File(IMAGE_PATH+"img000.jpg");
//			byte[] byteBuffer;
//			FileInputStream inputStream;
//			Bitmap image=null;
//			try {
//				inputStream=new FileInputStream(file);
//				byteBuffer=new byte[inputStream.available()];
//				inputStream.read(byteBuffer);
//				image=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
//				inputStream.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(new File("/sdcard/0image/1.mp4"), 
					bitmap.getWidth(), bitmap.getHeight());
//			recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
//			recorder.setVideoQuality(0.25);
//			recorder.setSampleRate(44100);
			recorder.setFormat("mp4");
			recorder.setFrameRate(1);
			try {
				recorder.start();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e1) {
				e1.printStackTrace();
			}
			
			IplImage iplImage;
			iplImage=IplImage.create(bitmap.getWidth(), bitmap.getHeight(), opencv_core.IPL_DEPTH_8U, 4);
//			bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
			
			int i=0;
			while(mRun) {
				exec.test(byteBuffer, 0);
				bitmap=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
				bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
				bitmap.recycle();
				
				recorder.setTimestamp(1000000*i++); // 微秒为单位
				try {
					recorder.record(iplImage);
				} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				recorder.stop();
				recorder.release();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
				e.printStackTrace();
			}

		}
		
	}

}
