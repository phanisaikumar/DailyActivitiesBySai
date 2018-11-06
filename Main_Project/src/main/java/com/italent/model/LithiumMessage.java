package com.italent.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lithium_messages")
public class LithiumMessage {
    @Id
    @Column(name = "msg_id")
    private String MsgId;
    @Column(name = "author")
    private String Author;
    @Column(name = "msg_subject")
    private String Message;
    @Column(name = "msg_href")
    private String href;
    @Column(name = "last_edit_time")
    private Date lastEditTime;
    @Column(name = "post_time")
    private Date postTime;

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public String toString() {
        return "LithiumMessage [MsgId=" + MsgId + ", Message=" + Message + "]";
    }

}
