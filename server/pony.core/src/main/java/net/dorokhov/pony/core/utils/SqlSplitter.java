package net.dorokhov.pony.core.utils;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

public class SqlSplitter {

	private static final char DEFAULT_STATEMENT_SEPARATOR = ';';
	private static final String DEFAULT_COMMENT_PREFIX = "--";

	private char statementSeparator;

	private String commentPrefix;

	public SqlSplitter() {
		setStatementSeparator(DEFAULT_STATEMENT_SEPARATOR);
		setCommentPrefix(DEFAULT_COMMENT_PREFIX);
	}

	public SqlSplitter(char aStatementSeparator, String aCommentPrefix) {
		setStatementSeparator(aStatementSeparator);
		setCommentPrefix(aCommentPrefix);
	}

	public char getStatementSeparator() {
		return statementSeparator;
	}

	public void setStatementSeparator(char aStatementSeparator) {
		statementSeparator = aStatementSeparator;
	}

	public String getCommentPrefix() {
		return commentPrefix;
	}

	public void setCommentPrefix(String aCommentPrefix) {
		commentPrefix = aCommentPrefix;
	}

	public List<String> splitScript(File aFile) throws Exception {

		LineNumberReader reader = null;

		try {

			reader = new LineNumberReader(new FileReader(aFile));

			return splitScript(readScript(reader));

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
			}
		}
	}

	public List<String> splitScript(String aScript) {

		List<String> statements = new LinkedList<>();

		char delimiter = getStatementSeparator();
		if (!containsSqlScriptDelimiters(aScript, delimiter)) {
			delimiter = '\n';
		}

		splitScript(aScript, "" + delimiter, statements);

		return statements;
	}

	private void splitScript(String aScript, String aDelimiter, List<String> aStatements) {

		StringBuilder sb = new StringBuilder();

		boolean inLiteral = false;
		boolean inEscape = false;

		String sqlScript = removeSqlComments(aScript);

		char[] content = sqlScript.toCharArray();

		for (int i = 0; i < sqlScript.length(); i++) {

			char c = content[i];

			if (inEscape) {
				inEscape = false;
				sb.append(c);
				continue;
			}

			// MySQL style escapes
			if (c == '\\') {
				inEscape = true;
				sb.append(c);
				continue;
			}

			if (c == '\'') {
				inLiteral = !inLiteral;
			}

			if (!inLiteral) {
				if (startsWithDelimiter(sqlScript, i, aDelimiter)) {

					if (sb.length() > 0) {
						aStatements.add(sb.toString().trim());
						sb = new StringBuilder();
					}

					i += aDelimiter.length() - 1;

					continue;

				} else if (c == '\n' || c == '\t') {
					c = ' ';
				}
			}

			sb.append(c);
		}

		if (StringUtils.hasText(sb)) {
			aStatements.add(sb.toString().trim());
		}
	}

	public String readScript(LineNumberReader aLineNumberReader) throws IOException {

		String currentStatement = aLineNumberReader.readLine();

		StringBuilder scriptBuilder = new StringBuilder();

		while (currentStatement != null) {

			if (StringUtils.hasText(currentStatement) && (getCommentPrefix() != null && !currentStatement.startsWith(getCommentPrefix()))) {
				if (scriptBuilder.length() > 0) {
					scriptBuilder.append('\n');
				}
				scriptBuilder.append(currentStatement);
			}

			currentStatement = aLineNumberReader.readLine();
		}

		return scriptBuilder.toString();
	}

	public boolean containsSqlScriptDelimiters(String aScript, char aDelimiter) {

		boolean inLiteral = false;

		char[] content = aScript.toCharArray();

		for (int i = 0; i < aScript.length(); i++) {
			if (content[i] == '\'') {
				inLiteral = !inLiteral;
			}
			if (content[i] == aDelimiter && !inLiteral) {
				return true;
			}
		}

		return false;
	}

	private boolean startsWithDelimiter(String aSource, int aStartIndex, String aDelimiter) {

		int endIndex = aStartIndex + aDelimiter.length();

		return aSource.length() >= endIndex && aSource.substring(aStartIndex, endIndex).equals(aDelimiter);
	}

	private String removeSqlComments(String aSource) {
		if (StringUtils.isEmpty(aSource)) {
			return aSource;
		}

		String commentPrefix = getCommentPrefix();
		String sqlCommentsRegExp =
				"(?m)^((?:(?!" + commentPrefix + "|').|'(?:''|[^'])*')*)" + commentPrefix + ".*$";

		return aSource.replaceAll(sqlCommentsRegExp, "$1");
	}
}
