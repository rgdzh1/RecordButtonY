package com.yey.rby;

public interface RBYCallback {
    /**
     * 记录结束的回调
     * @param current
     */
    void finishCb(String current);

    /**
     * 每一秒 都会触发该回调
     * @param current
     */
    void eventCb(String current);

    /**
     * 开始记录的回调
     */
    void startCb(String current);
}
