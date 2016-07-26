package com.arenas.droidfan.data.model;

/**
 * @author mcxiaoke
 * @version 1.3 2012.02.27
 */
public class DirectMessageModel {
    public static final int TYPE_CONVERSATION_LIST = 301;
    public static final int TYPE_CONVERSATION = 302;
    public static final int TYPE_INBOX = 303;
    public static final int TYPE_OUTBOX = 304;

    public static final String TAG = DirectMessageModel.class.getSimpleName();

    private String id;// id in string format
    private String account; // related account id/userid
    private String owner; // owner id of the item

    private int type; // type of the item

    private long rawid; // raw id in number format
    private long time; // created at of the item
    private String text;
    private String senderId;
    private String senderScreenName;
    private String senderProfileImageUrl;
    private String recipientId;
    private String recipientScreenName;
    private String recipientProfileImageUrl;
    public String conversationId;
    private int read;
    private int incoming;
    private UserModel sender = null;
    private UserModel recipient = null;

    public DirectMessageModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRawid() {
        return rawid;
    }

    public void setRawid(long rawid) {
        this.rawid = rawid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getIncoming() {
        return incoming;
    }

    public void setIncoming(int incoming) {
        this.incoming = incoming;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderScreenName() {
        return senderScreenName;
    }

    public void setSenderScreenName(String senderScreenName) {
        this.senderScreenName = senderScreenName;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl;
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientScreenName() {
        return recipientScreenName;
    }

    public void setRecipientScreenName(String recipientScreenName) {
        this.recipientScreenName = recipientScreenName;
    }

    public String getRecipientProfileImageUrl() {
        return recipientProfileImageUrl;
    }

    public void setRecipientProfileImageUrl(String recipientProfileImageUrl) {
        this.recipientProfileImageUrl = recipientProfileImageUrl;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
        if (sender != null) {
            this.senderProfileImageUrl = sender.getProfileImageUrlLarge();
        }
    }

    public UserModel getRecipient() {
        return recipient;
    }

    public void setRecipient(UserModel recipient) {
        this.recipient = recipient;
        if (recipient != null) {
            this.recipientProfileImageUrl = recipient.getProfileImageUrlLarge();
        }
    }

    @Override
    public String toString() {
        return "DirectMessageModel [ id=" + id + " text=" + text + ", senderId=" + senderId
                + ", senderScreenName=" + senderScreenName + ", recipientId="
                + recipientId + ", recipientScreenName=" + recipientScreenName
                + ", conversationId=" + conversationId + ", incoming="
                + incoming + "]";
    }

}
