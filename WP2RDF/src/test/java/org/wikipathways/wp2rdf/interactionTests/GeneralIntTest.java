package org.wikipathways.wp2rdf.interactionTests;

import org.junit.BeforeClass;

/**
 * Based on test data by Ryan Miller. See <a href="https://github.com/RyAMiller/WikiPathwaysInteractions">this repository</a>.
 *
 * @author Egon Willighagen
 * @author Ryan Miller
 */
public class GeneralIntTest extends AbstractInteractionTest {

	@BeforeClass
	public static void createModel() throws Exception {
		createModel("GeneralInt");
	}

}
