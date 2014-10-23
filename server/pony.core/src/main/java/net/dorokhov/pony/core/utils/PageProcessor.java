package net.dorokhov.pony.core.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageProcessor<T> {

	private final Handler<T> handler;

	private final int pageSize;

	private final Sort sort;

	public PageProcessor(int aPageSize, Sort aSort, Handler<T> aHandler) {
		pageSize = aPageSize;
		sort = aSort;
		handler = aHandler;
	}

	public void run() {

		Page<T> page = handler.getPage(new PageRequest(0, pageSize, sort));

		long indexInAll = 0;

		do {

			int indexInPage = 0;

			for (T item : page.getContent()) {

				handler.process(item, page, indexInPage, indexInAll);

				indexInPage++;
				indexInAll++;
			}

			Pageable nextPageable = page.nextPageable();

			page = nextPageable != null ? handler.getPage(nextPageable) : null;

		} while (page != null);
	}

	public static interface Handler<T> {

		public void process(T aItem, Page<T> aPage, int aIndexInPage, long aIndexInAll);

		public Page<T> getPage(Pageable aPageable);

	}

}
