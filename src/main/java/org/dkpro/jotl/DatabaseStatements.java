/*******************************************************************************
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.dkpro.jotl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal object of the API for caching prepared statements.
 */
public class DatabaseStatements {

	protected Map<String, PreparedStatement> prepStatement;
	protected Connection dbConnection;
	protected int language;
	protected boolean caseSensitive;
	protected DatabaseConfiguration dbConfig;

	/** Initializes frequently used prepared statements.
	 *  @param dbConfig DatabaseConfiguration
	 *  @param caseSensitive Case Sensitivity Setting
	 *  @throws SQLException in case of database errors. */
	public DatabaseStatements(final DatabaseConfiguration dbConfig,
			final boolean caseSensitive) throws SQLException {
		prepStatement = new HashMap<String, PreparedStatement>();
		this.dbConfig = dbConfig;
		language = dbConfig.getLanguage();
		dbConnection = getConnection(dbConfig);
		this.caseSensitive  = caseSensitive;
		initStatements();
	}

	/** Initialize the prepared statements using the current configuration.
	 *  @throws SQLException in case of database errors. */
	protected void initStatements() throws SQLException{
		initCaseSensitiveStatements();
		PreparedStatement pstmt = null;

		// SelectSynset 1-int: synset.id
		String sql = "SELECT id FROM synset WHERE id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("SelectSynset", pstmt);

		// SynsetLinks  1-int: synset_id; 2-int: target_synset_id, 3-int: link_type_id
		sql = "SELECT * FROM synset_link WHERE synset_id=? OR target_synset_id=?" +
				" AND link_type_id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("SynsetLinks", pstmt);

		// TermLinks, 1-int: termId; 2-int: link_type_id
		sql = "SELECT * FROM term INNER JOIN term_link ON " +
			 " term.id=term_link.target_term_id" +
			 " WHERE term_link.term_id=?" +
			 " AND term_link.link_type_id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("TermLinks", pstmt);

		// AllSynsets
		sql = "SELECT id FROM synset";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("AllSynsets", pstmt);

		// Count synsets
		sql = "SELECT COUNT(id) as num FROM synset";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("CountSynsets", pstmt);

		// Term by id.
		sql = "SELECT * FROM term WHERE id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("SelectTermById", pstmt);

		// Term by synset_id.
		sql = "SELECT * FROM term WHERE synset_id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("SelectTermBySynset", pstmt);

		// Categories for a synset.
		sql = "SELECT c.id, c.category_name, c.category_type_id, c.is_disabled FROM category c "
				+ "INNER JOIN category_link l ON l.category_id = c.id "
				+ "WHERE l.synset_id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("SynsetCategories", pstmt);

		// Tags for a term.
		sql = "SELECT t.name FROM tag t "
				+ "INNER JOIN term_tag tt ON tt.tag_id = t.id "
				+ "WHERE tt.term_tags_id=?";
		pstmt = dbConnection.prepareStatement(sql);
		prepStatement.put("TermTags", pstmt);
	}

	/** Initialize Prepared Statements, that depend on case sensitivity.
	 *  @throws SQLException in case of database errors. */
	protected void initCaseSensitiveStatements() throws SQLException{
		String binary = (caseSensitive ? " BINARY " : "");

		// SelectTerm, 1-int: term.id; 2-String: normalized_word; 3-String: word; 4-int: synset_id
		String sql = "SELECT * FROM term WHERE ((normalized_word != '' AND "
				+ binary + "normalized_word=?)"
				+ " OR " + binary + "word=?)"
				+ " AND language_id=" + language;
		PreparedStatement pstmt = dbConnection.prepareStatement(sql);

		prepStatement.put("SelectTermByWord", pstmt);
	}

	/** Creates connection to the MySQL Database using the given database
	 *  configuration.
	 *  @param dbConfig The database configuration that is used to establish
	 *  		the database connection.
	 *  @return The JDBC database connection.
	 *  @throws SQLException in case of any errors. */
	public Connection getConnection(final DatabaseConfiguration dbConfig)
			throws SQLException {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}

		dbConnection = DriverManager.getConnection(
				"jdbc:mysql://" + dbConfig.getHost() + "/"
				+ dbConfig.getDatabase()
				+ "?user=" + dbConfig.getUser()
				+ "&password=" + dbConfig.getPassword());
		return dbConnection;
	}

	/** Returns the prepared statement with the given name.
	 *  @param name Name of Prepared Statement
	 *  @return PreparedStatement */
	public PreparedStatement getPreparedStatement(final String name) {
		return prepStatement.get(name);
	}

	/** @return Case Sensitivity setting */
	public boolean getIsCaseSensitive() {
		return this.caseSensitive;
	}

	/** Sets case sensitivity setting.
	  * @param caseSensitive TRUE or FALSE
	 *  @throws SQLException */
	public void setIsCaseSensitive(final boolean caseSensitive) throws SQLException {
		this.caseSensitive = caseSensitive;
		initCaseSensitiveStatements();
	}

	/** @return The current database configuration. */
	public DatabaseConfiguration getDatabaseConfiguration() {
		return dbConfig;
	}

	/** Sets DatabaseConfiguration and initializes the prepared statements.
	 *  @param dbConfig New database configuration settings.
	 *  @throws SQLException in case of database errors. */
	public void setDatabaseConfiguration(final DatabaseConfiguration dbConfig)
			throws SQLException {
		this.dbConfig = dbConfig;
		language = dbConfig.getLanguage();
		dbConnection = getConnection(dbConfig);
		initStatements();
		this.dbConfig = dbConfig;
	}

}
