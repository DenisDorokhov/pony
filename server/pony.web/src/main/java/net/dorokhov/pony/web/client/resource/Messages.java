package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages {

	public static final Messages INSTANCE = GWT.create(Messages.class);

	public String dateFormatTechnical();

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
	public String toolbarLogout();

	public String artistUnknown();
	public String albumUnknown();
	public String albumDisc(Integer aDisc);
	public String albumDownload();

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
	public String scanningStatusCleaningSongsUnknownProgress();
	public String scanningStatusCleaningArtworks(String aProgress);
	public String scanningStatusCleaningArtworksUnknownProgress();
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

	public String pagedListPrevious();
	public String pagedListNext();
	public String pagedListRefresh();

	public String logTitle();
	public String logPager(int aPageNumber, int aTotalPages, @Optional int aSize, @Optional long aTotalElements);
	public String logColumnDate();
	public String logColumnType();
	public String logColumnText();
	public String logTypeDebug();
	public String logTypeInfo();
	public String logTypeWarn();
	public String logTypeError();
	public String logFilterType();
	public String logFilterMinDate();
	public String logFilterMaxDate();
	public String logFilterApply();

	public String userListTitle();
	public String userListPager(int aPageNumber, int aTotalPages, @Optional int aSize, @Optional long aTotalElements);
	public String userListColumnCreationDate();
	public String userListColumnUpdateDate();
	public String userListColumnName();
	public String userListColumnEmail();
	public String userListColumnRole();
	public String userListColumnEdit();
	public String userListRoleUser();
	public String userListRoleAdmin();
	public String userListButtonAdd();
	public String userListButtonEdit();

	public String userEditCreationTitle();
	public String userEditModificationTitle();
	public String userEditName();
	public String userEditEmail();
	public String userEditPassword();
	public String userEditRepeatPassword();
	public String userEditNewPassword();
	public String userEditRepeatNewPassword();
	public String userEditRole();
	public String userEditSaveButton();
	public String userEditDeleteButton();
	public String userEditRoleUser();
	public String userEditRoleAdmin();
	public String userEditDeletionConfirmation();

	public String settingsTitle();
	public String settingsAutoScan();
	public String settingsAutoScanEveryHour();
	public String settingsAutoScanEveryDay();
	public String settingsAutoScanEveryWeek();
	public String settingsAutoScanOff();
	public String settingsLibraryFolders();
	public String settingsLibraryFolderPlaceholder();
	public String settingsConfirmScanAfterLibraryChange();
	public String settingsSaveButton();

	public String logMessageColumnDetails();

	public String logMessageTitle();
	public String logMessageDate();
	public String logMessageType();
	public String logMessageText();
	public String logMessageDetails();

}
