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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Main access point to OpenThesaurus data. Use this class to establish a
 * database connection and to query for terms and synsets.
 */
public class OpenThesaurus {

	protected DatabaseConfiguration dbConfig;
	protected DatabaseStatements dbStatements;

	/** Initializes the OpenThesaurus API using the given database
	 *  configuration. Queries will be performed in a case insensitive
	 *  manner.
	 *  @param dbConfig Database configuration for accessing the
	 *  		OpenThesaurus data.
	 *  @throws JOTLException in case of any errors, e.g., unreachable
	 *  		database or invalid user credentials. */
	public OpenThesaurus(final DatabaseConfiguration dbConfig)
			throws JOTLException {
		this(dbConfig, false);
	}

	/** Initializes the OpenThesaurus API using the given database
	 *  configuration. The second parameter defines whether queries
	 *  should be performed in a case sensitive (true) or case insensitive
	 *  manner (false).
	 *  @param dbConfig Database configuration for accessing the
	 *  		OpenThesaurus data.
	 *  @param caseSensitive Perform database queries in a case sensitive or
	 *  		case insensitive manner.
	 *  @throws JOTLException in case of any errors, e.g., unreachable
	 *  		database or invalid user credentials. */
	public OpenThesaurus(final DatabaseConfiguration dbConfig,
			final boolean caseSensitive) throws JOTLException {
		this.dbConfig = dbConfig;
		try {
			dbStatements = new DatabaseStatements(dbConfig, caseSensitive);
		} catch (SQLException e) {
			throw new JOTLException("Error connecting to the OpenThesaurus database", e);
		}
	}

	/** Returns the {@link OTTerm} with the given id.
	 *  @param termId The numerical id used internally to uniquely
	 *  		identify terms in OpenThesaurus.
	 *  @return The term with the given id or null if no such term could
	 *  		be found.
	 *  @throws JOTLException in case of any errors. */
	public OTTerm getTermById(final int termId) throws JOTLException {
		OTTerm result = null;
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectTermById");
			pstmt.setInt(1, termId);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result = new OTTerm(rs.getInt("id"),
							new OTSynset(rs.getInt("synset_id"), dbStatements),
							rs.getString("word"), rs.getString("normalized_word"),
							rs.getInt("level_id"),
							rs.getInt("is_acronym") > 0, rs.getInt("is_short_form") > 0,
							rs.getInt("language_id"), rs.getInt("word_grammar_id"),
							dbStatements);
				}
				pstmt.clearParameters();
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw new JOTLException("Error while querying for a term with id " + termId, e);
		}
		return result;
	}


	/** Returns a set of terms with the given word. Both the database fields
	 *  "normalized_word" and "word" are used for the query. The query can
	 *  be executed in a case sensitive or insensitive manner depending on the
	 *  setting of {@link #setIsCaseSensitive(boolean)}.
	 *  @param word The word that is searched for.
	 *  @return The resulting set of terms. The list might be empty,
	 *  		but is never null.
	 *  @throws JOTLException in case of any errors. */
	public Set<OTTerm> getTermsByWord(final String word) throws JOTLException {
		Set<OTTerm> result = new HashSet<OTTerm>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectTermByWord");
			pstmt.setString(1, word);
			pstmt.setString(2, word);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result.add(new OTTerm(rs.getInt("id"),
							new OTSynset(rs.getInt("synset_id"), dbStatements),
							rs.getString("word"), rs.getString("normalized_word"),
							rs.getInt("level_id"),
							rs.getInt("is_acronym") > 0, rs.getInt("is_short_form") > 0,
							rs.getInt("language_id"), rs.getInt("word_grammar_id"),
							dbStatements));
				}
				pstmt.clearParameters();
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			throw new JOTLException("Error while querying for a term with word '" + word + "'", e);
		}
		return result;
	}

	/** Returns the {@link OTSynset} with the given id.
	 *  @param synsetId The numerical id used internally to uniquely
	 *  		identify synsets in OpenThesaurus.
	 *  @return The synset with the given id or null if no such synset could
	 *  		be found.
	 *  @throws JOTLException in case of any errors. */
	public OTSynset getSynsetById(final int synsetId) throws JOTLException {
		OTSynset result = null;
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectSynset");
			pstmt.setInt(1, synsetId);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result = new OTSynset(rs.getInt("id"), dbStatements);
				}
				pstmt.clearParameters();
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			throw new JOTLException("Error while querying for a synset with id " + synsetId, e);
		}

		return result;
	}


	/** Returns a set of synsets that each contain the given word as one of
	 *  their synonyms. Both the database fields "normalized_word" and "word"
	 *  are used for the query. The query can be executed in a case sensitive
	 *  or insensitive manner depending on the setting of
	 *  {@link #setIsCaseSensitive(boolean)}.
	 *  @param word The word that is to be contained in the synsets.
	 *  @return The resulting set of synsets. The list might be empty,
	 *  		but is never null.
	 *  @throws JOTLException in case of any errors. */
	public Set<OTSynset> getSynsetsByWord(final String word)
			throws JOTLException{
		Set<OTSynset> result = new HashSet<OTSynset>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectTermByWord");
			pstmt.setString(1, word);
			pstmt.setString(2, word);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result.add(new OTSynset(rs.getInt("synset_id"), dbStatements));
				}
				pstmt.clearParameters();
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			throw new JOTLException("Error while querying for synsets containing word '" + word + "'", e);
		}
		return result;
	}


	/** Returns the {@link OTSynset} containing the given term id.
	 *  @param termId The numerical id used internally to uniquely
	 *  		identify terms in OpenThesaurus.
	 *  @return The synset containing the given term id or null if no such
	 *  		synset could be found.
	 *  @throws JOTLException in case of any errors. */
	public OTSynset getSynsetByTermId(final int termId) throws JOTLException {
		OTSynset result = null;
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectTermById");
			pstmt.setInt(1, termId);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result = new OTSynset(rs.getInt("synset_id"), dbStatements);
				}
			} finally {
				rs.close();
			}
			pstmt.clearParameters();
		} catch(SQLException e) {
			throw new JOTLException("Error while querying for a synset with term id " + termId, e);
		}
		return result;
	}

	/** Returns a set of all synsets encoded in OpenThesaurus.
	 *  @return A set of all synsets; never null.
	 *  @throws JOTLException in case of any errors. */
	public Set<OTSynset> getAllSynsets() throws JOTLException {
		Set<OTSynset> result = new HashSet<OTSynset>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("AllSynsets");
			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result.add(new OTSynset(rs.getInt("id"), dbStatements));
				}
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to fetch all OpenThesaurus synsets", e);
		}
		return result;
	}

	/** Returns the total number of synsets encoded in OpenThesaurus.
	 *  @return Total number of synsets.
	 *  @throws JOTLException in case of any errors. */
	public int getNumberOfSynsets() throws JOTLException {
		int result = 0;
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("CountSynsets");
			ResultSet rs = pstmt.executeQuery();
			try {
				if (rs.next())
					result = rs.getInt("num");
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to count OpenThesaurus synsets", e);
		}
		return result;
	}

	/** Queries involving words can be carried out in a case sensitive or case
	 *  insensitive manner. The API will produce appropriate prepared
	 *  statements based on this setting.
	 *  @param caseSensitive Perform queries in a case sensitive (true) or
	 *  		insensitive manner (false).
	 *  @throws JOTLException in case of any errors. */
	public void setIsCaseSensitive(final boolean caseSensitive)
			throws JOTLException {
		try {
			dbStatements.setIsCaseSensitive(caseSensitive);
		} catch (SQLException e) {
			throw new JOTLException("Unable to prepare statements", e);
		}
	}

	/** Retrieve the current setting of case sensitivity.
	 *  @return true if queries are performed in a case sensitive manner. */
	public boolean getIsCaseSensitive() {
		return dbStatements.getIsCaseSensitive();
	}

	/** Switch the database configuration.
	 *  @param dbConfig New database configuration settings.
	 *  @throws JOTLException in case of any errors. */
	public void setDatabaseConfiguration(final DatabaseConfiguration dbConfig)
			throws JOTLException {
		try {
			dbStatements.setDatabaseConfiguration(dbConfig);
			this.dbConfig = dbConfig;
		} catch (SQLException e) {
			throw new JOTLException("Unable to change database configuration", e);
		}
	}

	/** Get the current database configuration settings.
	 *  @return current settings. */
	public DatabaseConfiguration getDatabaseConfiguration() {
		return dbConfig;
	}

}
