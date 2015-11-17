package org.wikipathways.wp2rdf;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractConvertorTest {

	protected static Model model;

	protected static String toString(Model model) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		model.write(output, "TURTLE");
		return new String(output.toByteArray());
	}

	@Test
	public void testForResourceURIsWithWhitespace() {
		String errors = "";
		int errorCount = 0;
		for (Resource resource : model.listSubjects().toSet()) {
			String resourceURI = resource.getURI();
			if (containsWhiteSpace(resourceURI)) {
				errors += "<" + resourceURI + ">\n";
				errorCount++;
			}
		}
		Assert.assertEquals(
			"Resource URIs with whitespace:\n" + errors,
			0, errorCount
		);
	}

	private boolean containsWhiteSpace(String string) {
		for (int i=0; i<string.length(); i++) {
			if (Character.isWhitespace(string.charAt(i))) return true;
		}
		return false;
	}
}
