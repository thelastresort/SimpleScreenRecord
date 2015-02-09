package com.example.screen_shot_turbo;

import java.io.DataOutputStream;
import java.io.IOException;

public class Exec
{
    static {
        System.loadLibrary("a1w0n");
    }

    public native int test(byte[] data, int multiple);
    
    public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		
		try {
			Runtime.getRuntime().exec(new String[] { "/system/bin/su", "-c","chmod 777 /dev/graphics/fb0" });
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

}

