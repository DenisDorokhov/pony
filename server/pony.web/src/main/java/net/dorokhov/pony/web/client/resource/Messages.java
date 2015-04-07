package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages {

	public static final Messages INSTANCE = GWT.create(Messages.class);

	public String errorTitle();
	public String errorText();
	public String errorHomeButton();

	public String loginTitle();

	public String libraryTitle();
	public String libraryTitleSongPrefix();
	public String libraryTitleSongBody(String aArtist, String aSong);

	public String errorsHeader();

	public String loginViewHeader();
	public String loginViewEmail();
	public String loginViewPassword();
	public String loginViewLoginButton();

	public String toolbarRefresh();
	public String toolbarSystem();
	public String toolbarConfig();
	public String toolbarScanning();
	public String toolbarLog();
	public String toolbarUsers();
	public String toolbarEditProfile();
	public String toolbarLogout();

	public String artistUnknown();
	public String albumUnknown();
	public String albumDisc(Integer aDisc);

	public String statusLoading();
	public String statusError();
	public String statusArtistListEmpty();
	public String statusAlbumListEmpty();

	public String playerAlertPlaybackWillStop();
	public String playerTitle();
	public String playerSubtitle();

	public String scanningTitle();
	public String scanningStatus();
	public String scanningProgress();
	public String scanningButton();
	public String scanningStatusInactive();
	public String scanningStatusStarting();
	public String scanningStatusSearchingMediaFiles();
	public String scanningStatusCleaningSongs(String aProgress);
	public String scanningStatusCleaningArtworks(String aProgress);
	public String scanningStatusImportingSongs(String aProgress);
	public String scanningStatusNormalizing(String aProgress);
	public String scanningPager(int aPageNumber, int aTotalPages, @Optional int aCount, @Optional long aTotalCount);
	public String scanningColumnStarted();
	public String scanningColumnUpdated();
	public String scanningColumnStatus();
	public String scanningColumnLastMessage();
	public String scanningJobStatusStarting();
	public String scanningJobStatusStarted();
	public String scanningJobStatusComplete();
	public String scanningJobStatusFailed();
	public String scanningJobStatusInterrupted();

	public String logTitle();

	public String pagedListPrevious();
	public String pagedListNext();

	public String logColumnDate();
	public String logColumnType();
	public String logColumnText();
}
