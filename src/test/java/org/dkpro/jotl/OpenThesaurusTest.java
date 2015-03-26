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

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.dkpro.jotl.JOTLException;
import org.dkpro.jotl.OTCategory;
import org.dkpro.jotl.OTLanguage;
import org.dkpro.jotl.OTSynset;
import org.dkpro.jotl.OTSynsetLinkType;
import org.dkpro.jotl.OTTerm;
import org.dkpro.jotl.OTTermLevelType;
import org.dkpro.jotl.OTTermLinkType;
import org.dkpro.jotl.OpenThesaurus;

public class OpenThesaurusTest extends TestCase {

	protected OpenThesaurus ot;

	@Override
	protected void setUp() throws Exception {
		// Activate test by commenting in your database configuration.
//		DatabaseConfiguration dc = new DatabaseConfiguration("localhost", "openthesaurus_20150322", "root", "", OTLanguage.GERMAN);
//		ot = new OpenThesaurus(dc);
	};

	public void testGetNumberOfSynsets() throws JOTLException {
		if (ot == null)
			return; // Skip test.

		assertTrue(ot.getNumberOfSynsets() > 0);

		Set<OTSynset> syn = ot.getAllSynsets();
		assertEquals(ot.getNumberOfSynsets(), syn.size());
	}

	public void testGetSynset() throws JOTLException {
		if (ot == null)
			return; // Skip test.

		OTSynset synset = ot.getSynsetById(1);
		Iterator<OTTerm> terms = synset.getTerms().iterator();
		assertEquals("Fission", terms.next().getWord());
		assertEquals("Atomspaltung", terms.next().getWord());
		assertEquals("Kernspaltung", terms.next().getWord());
		assertEquals("Kernfission", terms.next().getWord());
		assertFalse(terms.hasNext());

		//assertEquals(0, s.getSynsetLinks(SynsetLinkType.HYPERNYMY).size());
		Iterator<OTSynset> synsets = synset.getSynsetLinks(OTSynsetLinkType.HYPERNYMY).iterator();
		terms = synsets.next().getTerms().iterator();
		assertEquals("Kernreaktor", terms.next().getWord());
		assertEquals("Atomreaktor", terms.next().getWord());
		assertEquals("Atombrenner", terms.next().getWord());
		assertEquals("Atommeiler", terms.next().getWord());
		assertEquals("Nuklearmeiler", terms.next().getWord());
		assertFalse(terms.hasNext());
		terms = synsets.next().getTerms().iterator();
		assertEquals("A-Bombe", terms.next().getWord());
		assertEquals("Atombombe", terms.next().getWord());
		assertEquals("Nuklearwaffe", terms.next().getWord());
		assertEquals("Kernwaffe", terms.next().getWord());
		assertEquals("die Bombe", terms.next().getWord());
		assertEquals("Nuklearbombe", terms.next().getWord());
		assertFalse(terms.hasNext());
		assertFalse(synsets.hasNext());

		Set<OTCategory> categories = synset.getCategories();
		assertEquals(1, categories.size());
		assertEquals("Physik", categories.iterator().next().getName());

		synset = ot.getSynsetById(7);
		terms = synset.getTerms().iterator();
		assertEquals("Mangelhaftigkeit", terms.next().getWord());
		assertEquals("Defizienz", terms.next().getWord());
		assertFalse(terms.hasNext());

		synsets = synset.getSynsetLinks(OTSynsetLinkType.HYPERNYMY).iterator();
		terms = synsets.next().getTerms().iterator();
		assertEquals("Stand", terms.next().getWord());
		assertEquals("Zustand", terms.next().getWord());
		assertEquals("Status", terms.next().getWord());
		assertEquals("Konstitution", terms.next().getWord());
		assertEquals("Verfassung", terms.next().getWord());
		assertEquals("Befindlichkeit", terms.next().getWord());
		assertFalse(synsets.hasNext());

		assertNull(ot.getSynsetById(454545));

		assertEquals(synset, ot.getSynsetByTermId(synset.getTerms().iterator().next().getTermId()));
		assertTrue(ot.getSynsetsByWord("Defizienz").contains(synset));
	}

	public void testGetTerm() throws JOTLException {
		if (ot == null)
			return; // Skip test.

		OTTerm term = ot.getTermById(89100);
		assertEquals("Konzentration", term.getWord());
		assertEquals(null, term.getNormalizedWord());
		assertEquals(OTLanguage.GERMAN, term.getLanguageId());
		assertEquals(0, term.getGrammarId());
		assertFalse(term.getIsAcronym());
		assertFalse(term.getIsShortForm());
		assertEquals(0, term.getTermLevel());

		Iterator<OTTerm> antonyms = term.getTermLinks(OTTermLinkType.ANTONYMY).iterator();
		assertEquals("Ablenkung", antonyms.next().getWord());
		assertFalse(antonyms.hasNext());

		assertNull(ot.getTermById(454545));

		assertTrue(ot.getTermsByWord("Konzentration").contains(term));

		assertEquals(OTTermLevelType.JARGON, ot.getTermsByWord("Pneumonie").iterator().next().getTermLevel());

		assertEquals("figurativ", ot.getTermById(103319).getTags().iterator().next());
	}

	public void testSetCaseSensitive() throws JOTLException {
		if (ot == null)
			return; // Skip test.

		ot.setIsCaseSensitive(true);
		assertTrue(ot.getTermsByWord("fission").size() == 0);
		ot.setIsCaseSensitive(false);
		assertTrue(ot.getTermsByWord("fission").size() > 0);
	}

	public static OTSynset getRoot(OTSynset start) throws JOTLException {
		Set<OTSynset> parents = start.getSynsetLinks(OTSynsetLinkType.HYPERNYMY);
		OTSynset result = start;
		if(!parents.isEmpty()){
			for(OTSynset s : parents)
				result = getRoot(s);
		}
		return result;
	}

}
