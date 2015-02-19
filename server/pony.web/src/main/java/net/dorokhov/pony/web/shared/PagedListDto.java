package net.dorokhov.pony.web.shared;

import java.util.ArrayList;
import java.util.List;

public class PagedListDto<T> {
	
	private int pageNumber;

	private int pageSize;

	private int totalPages;

	private long totalElements;

	private List<T> content;

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

	public List<T> getContent() {

		if (content == null) {
			content = new ArrayList<>();
		}

		return content;
	}

	public void setContent(List<T> aContent) {
		content = aContent;
	}

}
