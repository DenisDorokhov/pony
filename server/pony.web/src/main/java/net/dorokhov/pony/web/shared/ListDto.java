package net.dorokhov.pony.web.shared;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ListDto<T> {

	public static interface ContentConverter<EntityType, DtoType> {
		public DtoType convert(EntityType aItem);
	}

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

	public static <EntityType, DtoType> ListDto<DtoType> valueOf(Page<EntityType> aPage, ContentConverter<EntityType, DtoType> aItemConverter) {

		ListDto<DtoType> dto = new ListDto<>();

		dto.setPageNumber(aPage.getNumber());
		dto.setPageSize(aPage.getSize());
		dto.setTotalPages(aPage.getTotalPages());
		dto.setTotalElements(aPage.getTotalElements());

		for (EntityType item : aPage.getContent()) {
			dto.getContent().add(aItemConverter.convert(item));
		}

		return dto;
	}

}
