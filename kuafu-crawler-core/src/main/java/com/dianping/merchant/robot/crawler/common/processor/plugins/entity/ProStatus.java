package com.dianping.merchant.robot.crawler.common.processor.plugins.entity;

/**
 * 执行状态，针对processor,需要对
 * 
 * @author mobangwei
 * 
 */
public class ProStatus {

    private int messageCode;

    private String message;

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ProStatus fail(int messageCode) {
        return fail(messageCode, null);
    }

    public static ProStatus fail(int messageCode, String message) {
        ProStatus proStatus = new ProStatus();
        proStatus.setMessageCode(messageCode);
        proStatus.setMessage(message);
        return proStatus;
    }

    public static ProStatus success() {
        ProStatus proStatus = new ProStatus();
        proStatus.setMessageCode(ProMessageCode.SUCCESS.getCode());
        return proStatus;
    }

    public boolean isSuccess() {
        return ProMessageCode.SUCCESS.getCode() == this.getMessageCode();
    }
}
