package net.dorokhov.pony.core.service.search;

import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.search.*;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager aEntityManager) {
		entityManager = aEntityManager;
	}

	@Override
	public void createIndex() {

		FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);

		try {
			fullTextSession.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional
	public void clearIndex() {

		FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);

		fullTextSession.purgeAll(Artist.class);
		fullTextSession.purgeAll(Album.class);
		fullTextSession.purgeAll(Song.class);

		fullTextSession.getSearchFactory().optimize(Artist.class);
		fullTextSession.getSearchFactory().optimize(Album.class);
		fullTextSession.getSearchFactory().optimize(Song.class);
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Artist> searchArtists(String aQuery, int aMaxResults) {

		FullTextQuery jpaQuery = buildQuery(aQuery, Artist.class, "name");

		jpaQuery.setSort(new Sort(new SortField("name", SortField.STRING)));
		jpaQuery.setFirstResult(0);
		jpaQuery.setMaxResults(aMaxResults);

		return (List<Artist>)jpaQuery.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Album> searchAlbums(String aQuery, int aMaxResults) {

		FullTextQuery jpaQuery = buildQuery(aQuery, Album.class, "name");

		Criteria criteria = getSession().createCriteria(Album.class)
				.setFetchMode("artist", FetchMode.JOIN);

		jpaQuery.setCriteriaQuery(criteria);

		jpaQuery.setSort(new Sort(new SortField("name", SortField.STRING)));
		jpaQuery.setFirstResult(0);
		jpaQuery.setMaxResults(aMaxResults);

		return (List<Album>)jpaQuery.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Song> searchSongs(String aQuery, int aMaxResults) {

		FullTextQuery jpaQuery = buildQuery(aQuery, Song.class, "name");

		Criteria criteria = getSession().createCriteria(Song.class)
				.setFetchMode("album", FetchMode.JOIN)
				.setFetchMode("album.artist", FetchMode.JOIN);

		jpaQuery.setCriteriaQuery(criteria);

		jpaQuery.setSort(new Sort(new SortField("name", SortField.STRING)));
		jpaQuery.setFirstResult(0);
		jpaQuery.setMaxResults(aMaxResults);

		return (List<Song>)jpaQuery.getResultList();
	}

	private FullTextQuery buildQuery(String aQuery, Class aClass, String aField) {

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(aClass).get();

		BooleanQuery luceneQuery = new BooleanQuery();

		for (String word : aQuery.trim().toLowerCase().split("\\s+")) {

			Query nameQuery = queryBuilder.keyword().wildcard().onField(aField).matching(word + "*").createQuery();

			luceneQuery.add(nameQuery, StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(word) ? BooleanClause.Occur.SHOULD : BooleanClause.Occur.MUST);
		}

		return fullTextEntityManager.createFullTextQuery(luceneQuery, aClass);
	}

	private Session getSession() {
		return (Session)entityManager.getDelegate();
	}
}
