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
 * This class models a category encoded in OpenThesaurus.
 */
public class OTCategory {

	protected int categoryId;
	protected String name;
	protected int type;
	protected boolean disabled;

	/** Instanciates a new category object. */
	public OTCategory(final int categoryId, final String name,
			final int type, final boolean disabled) {
		this.categoryId = categoryId;
		this.name = name;
		this.type = type;
		this.disabled = disabled;
	}

	/** Returns the category's unique id. */
	public int getCategoryId() {
		return categoryId;
	}

	/** Returns the category's name. */
	public String getName() {
		return name;
	}

	/** Returns the numerical type of the category. */
	public int getType() {
		return type;
	}

	/** Returns true if the category has been disabled. */
	public boolean isDisabled() {
		return disabled;
	}

}
