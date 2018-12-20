package com.tiktokdemo.lky.tiktokdemo.record.bean;

import java.io.Serializable;

/**
 * Created by 1 on 2017/4/25.
 */
public class MusicBean implements Serializable {
    private int musicId;
    private String name;
    private String cover;
    private String author;
    private int useTimesVirtual;
    private int formatDuration;
    private String url;
    private boolean isFav;
    private boolean isSelected;
    private boolean isPlaying;
    private boolean isDownloading;
    private String localPath;
    private String detailUrl;
    private String actName;


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setIsDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFormatDuration() {
        return formatDuration;
    }

    public int getFormatDurationInSecond() {
        return formatDuration / 1000;
    }

    public void setFormatDuration(int formatDuration) {
        this.formatDuration = formatDuration;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUseTimesVirtual() {
        return useTimesVirtual;
    }

    public void setUseTimesVirtual(int useTimesVirtual) {
        this.useTimesVirtual = useTimesVirtual;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActName() {
        return actName;
    }

    public MusicBean toCollectBean() {
        MusicBean bean = new MusicBean();
        bean.setIsSelected(false);
        bean.setIsPlaying(false);
        bean.setAuthor(author);
        bean.setMusicId(musicId);
        bean.setName(name);
        bean.setCover(cover);
        bean.setFormatDuration(formatDuration);
        bean.setIsFav(true);
        bean.setUrl(url);
        bean.setUseTimesVirtual(useTimesVirtual);
        return bean;
    }
}
