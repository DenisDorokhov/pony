package net.dorokhov.pony.core.service.search;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

public class SearchAnalyzer extends ReusableAnalyzerBase {

	private final Version matchVersion;

	public SearchAnalyzer(Version aMatchVersion) {
		matchVersion = aMatchVersion;
	}

	@Override
	protected TokenStreamComponents createComponents(String aFieldName, Reader aReader) {

		StandardTokenizer src = new StandardTokenizer(matchVersion, aReader);

		TokenStream filter = new StandardFilter(matchVersion, src);
		filter = new LowerCaseFilter(matchVersion, filter);

		return new TokenStreamComponents(src, filter);
	}
}
