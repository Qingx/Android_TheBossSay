package net.cd1369.tbs.android.data.entity;

import java.util.List;

public class ArticlePrintEntity {

    private String articleAuthor;
    private String articleCopyright;
    private String articleSource;
    private Integer articleTime;
    private String articleType;
    private Integer baseCollect;
    private Integer baseRead;
    private String bossId;
    private String bossName;
    private String content;
    private long createTime;
    private String createUserId;
    private Boolean deleted;
    private String descContent;
    private String encryptContent;
    private String filterType;
    private Integer fromType;
    private String id;
    private String interview;
    private String head;
    private Integer level;
    private String originLink;
    private String originPath;
    private String pictureSource;
    private Integer readCount;
    private String releaseRemake;
    private Boolean releaseStatus;
    private long releaseTime;
    private String releaseUser;
    private String releaseUserId;
    private String reportRemake;
    private long reportTime;
    private String reportUser;
    private String reportUserId;
    private int shareNumber;
    private int status;
    private String title;
    private List<String> files;

    public String getId() {
        return id;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getHead() {
        return head;
    }

    public String getBossName() {
        return bossName;
    }

    public String getDescContent() {
        return descContent;
    }

    public long getReleaseTime() {
        return releaseTime;
    }
}
