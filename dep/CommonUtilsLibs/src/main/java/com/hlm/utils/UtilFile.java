package com.hlm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class UtilFile {
	
	private static Method metSetPermissions;
	
	static {
		try {
			Class<?> clsFileUtils = Class.forName("android.os.UtilFile");
			metSetPermissions = clsFileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
			metSetPermissions.setAccessible(true);
		} catch (Exception e) {
		}
	}
	
	public static int setPermissions(File path, int mode) {
		try {
			return (Integer) metSetPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static int setPermissions(File path, int mode, int uid, int gid) {
		try {
			return (Integer) metSetPermissions.invoke(null, path.getAbsolutePath(), mode, uid, gid);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static int setPermissions(String path, int mode) {
		try {
			return (Integer) metSetPermissions.invoke(null, path, mode, -1, -1);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static int setPermissions(String path, int mode, int uid, int gid) {
		try {
			return (Integer) metSetPermissions.invoke(null, path, mode, uid, gid);
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	public static boolean copyFile(OutputStream output, InputStream input) {
		try {
			byte[] buf = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
		}

		return true;
	}
	
	public static boolean copyFile(File dst, File src) {
		try {
			dst.delete();
			FileOutputStream fos = new FileOutputStream(dst);
			FileInputStream fis = new FileInputStream(src);
			copyFile(fos, fis);
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
		}

		return true;
	}

	public static void writeToFile(String jsContent, File file) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(jsContent.getBytes());
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
