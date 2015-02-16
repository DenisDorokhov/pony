package net.dorokhov.pony.web.shared;

public class PagedListDto<T> extends ListDto<T> {

	private int pageNumber;

	private int pageSize;

	private int totalPages;

	private long totalElements;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int aPageNumber) {
		pageNumber = aPageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int aPageSize) {
		pageSize = aPageSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int aTotalPages) {
		totalPages = aTotalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long aTotalElements) {
		totalElements = aTotalElements;
	}

}
