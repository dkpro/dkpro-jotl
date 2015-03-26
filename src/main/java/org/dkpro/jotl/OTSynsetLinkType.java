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

/**
 * Set of constants for synset link types used by OpenThesaurus.
 */
public final class OTSynsetLinkType {

	/** Hypernymy links (i.e., synsets with a broader meaning). */
	public static final int HYPERNYMY = 1;

	/** Associative links (i.e., synsets with a related meaning). */
	public static final int ASSOCIATION = 2;

}
