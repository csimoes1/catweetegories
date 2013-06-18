package org.simoes.servlet.dao;

public class TwitterUser {
	
	private long id;
	private String name;
	private String screenName;
	private String location;
	private String description;
	private String profileImageUrl;
	private String profileBackgroundImageUrl;
	private int followersCount;
	private int friendsCount;
	private int favouritesCount;
	private int statusesCount;
	private int listedCount;
	private String createdAt;
	private String timeZone;
	private int utcOffset;
	private String lang;
	private String userWebSite;
	
	public TwitterUser(long id, String name, String screenName, String location, String description, String profileImageUrl, 
			String profileBackgroundImageUrl, int followersCount, int friendsCount, int favouritesCount, int statusesCount, 
			int listedCount, String createdAt, String timeZone, int utcOffset, String lang, String userWebSite) {
		this.id = id;
		this.name = name;
		this.screenName = screenName;
		this.location = location;
		this.description = description;
		this.profileImageUrl = profileImageUrl;
		this.profileBackgroundImageUrl = profileBackgroundImageUrl;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.favouritesCount = favouritesCount;
		this.statusesCount = statusesCount;
		this.listedCount = listedCount;
		this.createdAt = createdAt;
		this.timeZone = timeZone;
		this.utcOffset = utcOffset;
		this.lang = lang;
		this.setUserWebSite(userWebSite);
		
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}
	public void setProfileBackgroundImageUrl(String profileBackgroundImageUrl) {
		this.profileBackgroundImageUrl = profileBackgroundImageUrl;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public int getUtcOffset() {
		return utcOffset;
	}
	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public int getListedCount() {
		return listedCount;
	}

	public void setListedCount(int listedCount) {
		this.listedCount = listedCount;
	}

	public String getUserWebSite() {
		return userWebSite;
	}

	public void setUserWebSite(String userWebSite) {
		this.userWebSite = userWebSite;
	}
}
