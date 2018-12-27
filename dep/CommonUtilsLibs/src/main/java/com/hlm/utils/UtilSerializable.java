package com.hlm.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by CYL on 16-8-23.
 * email:670654904@qq.com
 *
 * 当方法有变化时 需要同时修改Utils包下的SerializableUtils
 */
public class UtilSerializable {
    /**
     * 从BLOB转换为AdEntity
     */
    public static <T extends Serializable> T getSerializableEntityFromBlob(Class<T> c, byte[] data) {
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(data);
            ObjectInputStream si = new ObjectInputStream(bi);
            T adEntity = (T) si.readObject();
            return adEntity;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return null;
    }

    /**
     * 将AdEntity转换为BLOB
     */
    public static byte[] getBlobFromSerializableEntity(Serializable entity) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(entity);
            so.flush();
            return bo.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] marshall(Parcelable parceable) {
        try {
            Parcel parcel = Parcel.obtain();
            parceable.writeToParcel(parcel, 0);
            byte[] bytes = parcel.marshall();
            parcel.recycle();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param bytes
     * @return
     */
    public static Parcel unmarshall(byte[] bytes) {
        try {
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0); // This is extremely important!
            return parcel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T unmarshall(Parcelable.Creator<T> creator, byte[] bytes) {
        try {
            Parcel parcel = unmarshall(bytes);
            T result = creator.createFromParcel(parcel);
            parcel.recycle();
            return result;
        } catch (Exception e) {
            return null;
        }

    }


}
