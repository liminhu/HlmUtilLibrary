package com.hlm.exception;



public class NotInitModelException extends RuntimeException {
    public NotInitModelException() {
        super("library 未初始化 ，请在应用application onCreate 函数中调用 CommonConfig.initLibrary(this);");
    }
}
