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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class models a synset encoded in OpenThesaurus. A synset is a set
 * of synonymous word senses, which is modeled as a set of {@link OTTerm}s
 * in OpenThesaurus.
 */
public class OTSynset {

	protected int synsetId;
	protected Set<OTTerm> terms;
	protected DatabaseStatements dbStatements;

	/** Instanciates a new synset with the given synset id.
	 *  @param synsetId The unique synset id used internally by OpenThesaurus.
	 *  @param dbStatements Internal object containing prepared statements. */
	protected OTSynset(int synsetId, DatabaseStatements dbStatements) {
		this.synsetId = synsetId;
		this.dbStatements = dbStatements;
		this.terms = null;
	}

	/** Returns the set of {@link OTTerm}s the synsets contains. That is, a list
	 *  synonymous word senses. The terms are loaded on demand (lazy
	 *  initialization).
	 *  @return The set of terms; never null.
	 *  @throws JOTLException in case of any errors. */
	public Set<OTTerm> getTerms() throws JOTLException {
		if (this.terms != null)
			return terms;

		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		terms = new HashSet<OTTerm>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SelectTermBySynset");
			pstmt.setInt(1, this.synsetId);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					terms.add(new OTTerm(rs.getInt("id"), this,
							rs.getString("word"), rs.getString("normalized_word"),
							rs.getInt("level_id"),
							rs.getInt("is_acronym") > 0, rs.getInt("is_short_form") > 0,
							rs.getInt("language_id"), rs.getInt("word_grammar_id"),
							dbStatements));
				}
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to initialize set of terms in syset " + synsetId, e);
		}
		return terms;
	}

	/** Returns a set of related synsets. That is, synsets that are connected
	 *  with the current synsets by means of a synset link of the given type
	 *  (e.g., hypernymy). Note that links are considered in a directed manner.
	 *  Use {@link #getSynsetLinksBackwards(int)} for incoming links.
	 *  @param synsetLinkType The type of link; use the constants defined in
	 *    {@link OTSynsetLinkType} for choosing this parameter.
	 *  @return The set of related synsets by this type.
	 *  @throws JOTLException in case of any errors.
	 *  @see OTSynsetLinkType */
	public Set<OTSynset> getSynsetLinks(int synsetLinkType) throws JOTLException {
		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		Set<OTSynset> result = new HashSet<OTSynset>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SynsetLinks");
			pstmt.setInt(1, this.synsetId);
			pstmt.setInt(2, 0);
			pstmt.setInt(3, synsetLinkType);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result.add(new OTSynset(rs.getInt("target_synset_id"), this.dbStatements));
				}
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to load synset links for synset "
					+ synsetId + " (type: " + synsetLinkType + ")", e);
		}
		return result;
	}

	/** Returns a set of backwardly related synsets. That is, synsets that are
	 *  connected with the current synsets by means of an inverse synset link
	 *  of the given type (e.g., hypernymy). Note that links are considered in
	 *  a directed manner. Use {@link #getSynsetLinks(int)} for outgoing links.
	 *  @param synsetLinkType The type of link; use the constants defined in
	 *    {@link OTSynsetLinkType} for choosing this parameter.
	 *  @return The set of related synsets by this type.
	 *  @throws JOTLException in case of any errors.
	 *  @see OTSynsetLinkType */
	public Set<OTSynset> getSynsetLinksBackwards(int synsetLinkType)
			throws JOTLException {
		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		Set<OTSynset> result = new HashSet<OTSynset>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SynsetLinks");
			pstmt.setInt(1, 0);
			pstmt.setInt(2, this.synsetId);
			pstmt.setInt(3, synsetLinkType);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					result.add(new OTSynset(rs.getInt("synset_id"), this.dbStatements));
				}
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to load backward synset links for synset "
					+ synsetId + " (type: " + synsetLinkType + ")", e);
		}
		return result;
	}

	public Set<OTCategory> getCategories() throws JOTLException {
		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		Set<OTCategory> result = new LinkedHashSet<OTCategory>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("SynsetCategories");
			pstmt.setInt(1, this.synsetId);
			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next())
					result.add(new OTCategory(rs.getInt(1), rs.getString(2),
							rs.getInt(3), rs.getInt(4) > 0));
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to load categories for synset "
					+ synsetId, e);
		}
		return result;
	}

	/** Returns a unique identifier for this synset that is used internally
	 *  by OpenThesaurus. */
	public int getSynsetId() {
		return synsetId;
	}

	/** Assigns a new ID to this synset. */
	protected void setSynsetId(int synsetId) {
		this.synsetId = synsetId;
	}

	/** Returns a string representation of this synset that contains the synset
	 *  id and the terms contained in the synset. Note that the method loads
	 *  uninitialized data from the database and hence might yield a performance
	 *  bottleneck. */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(synsetId).append(": ");
		try {
			for (OTTerm t : getTerms())
				result.append(t).append("|");
		} catch (JOTLException e) {
			result.append(e.getMessage());
		}
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + synsetId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OTSynset other = (OTSynset) obj;
		if (synsetId != other.synsetId)
			return false;
		return true;
	}

}
