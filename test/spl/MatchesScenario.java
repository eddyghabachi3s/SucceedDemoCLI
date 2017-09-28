package spl;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class MatchesScenario {

	@Test
	public void test() throws GitAPIException, IOException {
		Spl matchesSpl = new Spl();
		matchesSpl.printProducts();
		matchesSpl.printFeatures();
		matchesSpl.printAssets();
		matchesSpl.printFeatureToAssetCorrelations();
		matchesSpl.printAssetInstances();
		matchesSpl.printFeatureToAssetInstancesCorrelations();
		matchesSpl.printSPL();
		matchesSpl.createConfiguration();
		matchesSpl.createConfiguration();
	}

}
