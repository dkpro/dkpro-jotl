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
 * A configuration class holding the database connection settings.
 */
public class DatabaseConfiguration {

	protected String host;
	protected String database;
	protected String user;
	protected String password;
	protected int language;

	/** Instanciates a new, empty database configuration. Use the setters to
	 *  define the configuration values. */
	public DatabaseConfiguration() {}

	/** Instanciates a new database configuration with the given settings.
	 *  @param host The host name.
	 *  @param database The name of the database.
	 *  @param user The username.
	 *  @param password The password.
	 *  @param language The language of the OpenThesaurus data to be accessed.
	 *  		Use one of the constants in {@link OTLanguage} to define this
	 *  		parameter value.
	 *  @see OTLanguage */
	public DatabaseConfiguration(final String host, final String database,
			final String user, final String password, final int language) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.language = language;
	}

	/** @return The host where the database is running. */
	public String getHost() {
		return host;
	}

	/** @param host The host where the database is running. Set to "localhost",
	 *  if the database is running locally. */
	public void setHost(final String host) {
		this.host = host;
	}

	/** @return The name of the database. */
	public String getDatabase() {
		return database;
	}

	/** @param database The name of the database. */
	public void setDatabase(final String database) {
		this.database = database;
	}

	/** @return The database username. */
	public String getUser() {
		return user;
	}

	/** @param user The database user. */
	public void setUser(final String user) {
		this.user = user;
	}

	/** @return The password to access the database. */
	public String getPassword() {
		return password;
	}

	/** @param password The password to access the database. */
	public void setPassword(final String password) {
		this.password = password;
	}

	/** @return The language */
	public int getLanguage() {
		return language;
	}

	/** @param language The language */
	public void setLanguage(final int language) {
		this.language = language;
	}

}
