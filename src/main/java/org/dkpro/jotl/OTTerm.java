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
 * This class models a term encoded in OpenThesaurus. A term corresponds to
 * word senses or lexical units used in other lexical resources. It consists
 * of a unique term id, a synsets (i.e., concept) that the sense represents,
 * and a written word form. The latter is constructed from the database fields
 * "normalized_word" or (if empty) "word".
 */
public class OTTerm {

	protected int termId;
	protected OTSynset synset;
	protected int languageId;
	protected String word;
	protected String normalizedWord;
	protected int termLevel;
	protected boolean isAcronym;
	protected boolean isShortForm;
	protected int grammarId;

	protected DatabaseStatements dbStatements;

	/*
	private int languageId;
	private int wordGrammarId;
	*/

	/** Instanciates a new term with the given term id, synset, and word.
	 *  @param termId The unique term id used internally by OpenThesaurus.
	 *  @param synset The synset containing this term.
	 *  @param word The word form of this term. This is built from the database
	 *  		fields "normalized_word" or (if empty) "word"
	 *  @param dbStatements Internal object containing prepared statements. */
	protected OTTerm(final int termId, final OTSynset synset,
			final String word, final String normalizedWord, final int termLevel,
			final boolean isAcronym, final boolean isShortForm,
			final int languageId, final int grammarId,
			DatabaseStatements dbStatements) {
		this.termId = termId;
		this.synset = synset;
		this.word = word;
		this.normalizedWord = normalizedWord;
		this.termLevel = termLevel;
		this.isAcronym = isAcronym;
		this.isShortForm = isShortForm;
		this.languageId = languageId;
		this.grammarId = grammarId;
		this.dbStatements = dbStatements;
	}

	/** Returns a set of related terms. That is, word senses that are connected
	 *  with the current word sense by means of a term link of the given type
	 *  (e.g., antonymy).
	 *  @param termLinkType The type of link; use the constants defined in
	 *  		{@link OTTermLinkType} for choosing this parameter.
	 *  @throws JOTLException in case of any errors.
	 *  @see OTTermLinkType */
	public Set<OTTerm> getTermLinks(int termLinkType) throws JOTLException {
		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		Set<OTTerm> result = new HashSet<OTTerm>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("TermLinks");
			pstmt.setInt(1, this.termId);
			pstmt.setInt(2, termLinkType);

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
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to load term links for term "
					+ termId + " (type: " + termLinkType + ")", e);
		}
		return result;
	}

	public Set<String> getTags() throws JOTLException {
		if (dbStatements == null)
			throw new JOTLException("Please initialize DBStatements");

		Set<String> result = new LinkedHashSet<String>();
		try {
			PreparedStatement pstmt = dbStatements.getPreparedStatement("TermTags");
			pstmt.setInt(1, this.termId);

			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next())
					result.add(rs.getString(1));
			} finally {
				rs.close();
				pstmt.clearParameters();
			}
		} catch(SQLException e) {
			throw new JOTLException("Unable to load term tags for term "
					+ termId, e);
		}
		return result;
	}

	/** Returns a unique identifier for this term that is used internally
	 *  by OpenThesaurus.
	 *  @return The term identifier. */
	public int getTermId() {
		return termId;
	}

	/** Returns the synset containing this term. */
	public OTSynset getSynset() {
		return synset;
	}

	/** Returns the word that this term represents, i.e., the written form of
	 *  this word sense. */
	public String getWord() {
		return word;
	}

	public String getNormalizedWord() {
		return normalizedWord;
	}

	public int getTermLevel() {
		return termLevel;
	}

	public boolean getIsAcronym() {
		return isAcronym;
	}

	public boolean getIsShortForm() {
		return isShortForm;
	}

	public int getLanguageId() {
		return languageId;
	}

	public int getGrammarId() {
		return grammarId;
	}

	/** Returns a string representation of this term, which
	 *  consists of the term id and the word. */
	@Override
	public String toString() {
		return this.word+"#"+this.termId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + termId;
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
		OTTerm other = (OTTerm) obj;
		if (termId != other.termId)
			return false;
		return true;
	}

}
