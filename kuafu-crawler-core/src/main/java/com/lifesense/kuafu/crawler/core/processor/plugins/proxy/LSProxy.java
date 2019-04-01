package com.lifesense.kuafu.crawler.core.processor.plugins.proxy;

import org.apache.http.HttpHost;
import us.codecraft.webmagic.proxy.Proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class LSProxy implements Delayed, Serializable {

    private static final long serialVersionUID = 228939737383625551L;
    public static final int ERROR_403 = 403;
    public static final int ERROR_404 = 404;
    public static final int ERROR_BANNED = 10000;
    public static final int ERROR_Proxy = 10001;
    public static final int SUCCESS = 200;

    private final HttpHost httpHost;

    private int reuseTimeInterval = 1500;// ms
    private Long canReuseTime = 0L;
    private Long lastBorrowTime = System.currentTimeMillis();
    private Long responseTime = 0L;
    private Long idleTime = 0L;

    private int failedNum = 0;
    private int successNum = 0;
    private int borrowNum = 0;

    private List<Integer> failedErrorType = new ArrayList<Integer>();

    public LSProxy(HttpHost httpHost) {
        this.httpHost = httpHost;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);
    }

    public LSProxy(HttpHost httpHost, int reuseInterval) {
        this.httpHost = httpHost;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseInterval, TimeUnit.MILLISECONDS);
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void successNumIncrement(int increment) {
        this.successNum += increment;
    }

    public Long getLastUseTime() {
        return lastBorrowTime;
    }

    public void setLastBorrowTime(Long lastBorrowTime) {
        this.lastBorrowTime = lastBorrowTime;
    }

    public void recordResponse() {
        this.responseTime = (System.currentTimeMillis() - lastBorrowTime + responseTime) / 2;
        this.lastBorrowTime = System.currentTimeMillis();
    }

    public List<Integer> getFailedErrorType() {
        return failedErrorType;
    }

    public void setFailedErrorType(List<Integer> failedErrorType) {
        this.failedErrorType = failedErrorType;
    }

    public void fail(int failedErrorType) {
        this.failedNum++;
        this.failedErrorType.add(failedErrorType);
    }

    public void setFailedNum(int failedNum) {
        this.failedNum = failedNum;
    }

    public int getFailedNum() {
        return failedNum;
    }

    public String getFailedType() {
        String re = "";
        for (Integer i : this.failedErrorType) {
            re += i + " . ";
        }
        return re;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public int getReuseTimeInterval() {
        return reuseTimeInterval;
    }

    public void setReuseTimeInterval(int reuseTimeInterval) {
        this.reuseTimeInterval = reuseTimeInterval;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);

    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(canReuseTime - System.nanoTime(), unit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        LSProxy that = (LSProxy) o;
        return canReuseTime > that.canReuseTime ? 1 : (canReuseTime < that.canReuseTime ? -1 : 0);

    }

    @Override
    public String toString() {

        String re = String.format("host: %15s >> %5dms >> success: %-3.2f%% >> borrow: %d", httpHost.getAddress().getHostAddress(), responseTime,
                successNum * 100.0 / borrowNum, borrowNum);
        return re;

    }

    public void borrowNumIncrement(int increment) {
        this.borrowNum += increment;
    }

    public int getBorrowNum() {
        return borrowNum;
    }
}
