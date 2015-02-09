package com.example.imagetovideo;

import java.io.File;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.screen_shot_turbo.Exec;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MainService extends Service {
	public static final String ACTION_RECORD_SCREEN="com.example.imagetovideo.record";
	public static final String THREAD_RECORD_RUN="THREAD_RECORD_RUN";
	
	private BroadcastReceiver mReceiver;
	private RecordThread mRecordThread;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		mRecordThread=new RecordThread();
		mRecordThread.mRun=false;
		IntentFilter filter=new IntentFilter(ACTION_RECORD_SCREEN);
		mReceiver=new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra(THREAD_RECORD_RUN)) {
					boolean isRun=intent.getBooleanExtra(THREAD_RECORD_RUN, false);
					if(isRun) {
						if(!mRecordThread.mRun) {
							mRecordThread=new RecordThread();
							mRecordThread.mRun=true;
							mRecordThread.start();
						}
					}
					else {
						mRecordThread.mRun=false;
					}
				}
			}
			
		};
		try {
			unregisterReceiver(mReceiver);
			registerReceiver(mReceiver, filter);
		} catch (Exception e) {
			registerReceiver(mReceiver, filter);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
		}
		mRecordThread.mRun = false;
	}

	class TestThread extends Thread {
		public boolean mRun=false;

		@Override
		public void run() {
			super.run();
			while(mRun) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i("my", "service run");
			}
			Log.i("my", "service end");
			//MainService.this.stopSelf();
		}
		
	}

	class RecordThread extends Thread {
		public boolean mRun=false;

		@Override
		public void run() {
			super.run();
			
			Exec exec=new Exec();
			Exec.upgradeRootPermission(getPackageCodePath());
			
			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			int screenWidth=wm.getDefaultDisplay().getWidth();
			int screenHeight=wm.getDefaultDisplay().getHeight();
			byte[] byteBuffer=new byte[screenWidth*screenHeight/2];
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
			
			FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(new File("/sdcard/000.mp4"), 
					bitmap.getWidth(), bitmap.getHeight());
//			recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
//			recorder.setVideoQuality(0.25);
//			recorder.setSampleRate(44100);
			recorder.setFormat("mp4");
			recorder.setFrameRate(10);
			try {
				recorder.start();
			} catch (com.googlecode.javacv.FrameRecorder.Exception e1) {
				e1.printStackTrace();
			}
			
			IplImage iplImage;
			iplImage=IplImage.create(bitmap.getWidth(), bitmap.getHeight(), opencv_core.IPL_DEPTH_8U, 4);
//			bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
			
			int i=0;
			long startTime=new Date().getTime();
			long nowTime, during;
			// 循环需要优化
			while(mRun) {
				exec.test(byteBuffer, 0);
				bitmap=BitmapFactory.decodeByteArray(byteBuffer, 0, byteBuffer.length);
				bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
				bitmap.recycle();
				
//				recorder.setTimestamp(1000000*i++); // 微秒为单位
				if(i++==0)
					recorder.setTimestamp(0);
				else {
					nowTime=new Date().getTime();
					during=nowTime-startTime;
					recorder.setTimestamp(during*1000);
				}
				
				try {
					recorder.record(iplImage);
				} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				recorder.stop();
				recorder.release();
				mHandler.sendEmptyMessageDelayed(1, 1000);
			} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
				e.printStackTrace();
			}

		}
		
	}
	
	Handler mHandler=new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 1:
				Toast.makeText(MainService.this, "视频保存到SD卡的 000.mp4", Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}
		
	};

}
