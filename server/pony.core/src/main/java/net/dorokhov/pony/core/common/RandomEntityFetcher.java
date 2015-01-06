package net.dorokhov.pony.core.common;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class RandomEntityFetcher<T> {

	public List<T> fetch(int aCount, Dao<T> aDao) {

		List<T> result = new ArrayList<>();

		long count = aDao.fetchCount();

		while (result.size() < aCount) {

			int pageIndex = RandomUtils.nextInt(count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count);

			Page<T> page = aDao.fetchContent(new PageRequest(pageIndex, 1));

			if (page.hasContent()) {

				result.add(page.getContent().get(0));

			} else {

				count = aDao.fetchCount();

				if (count == 0) {
					result.clear();
					break;
				}
			}
		}

		return result;
	}

	public static interface Dao<T> {

		public long fetchCount();

		public Page<T> fetchContent(Pageable aPageable);

	}

}
