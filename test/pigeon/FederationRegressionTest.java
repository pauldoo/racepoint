/*
    Copyright (c) 2012 Paul Richards <paul.richards@gmail.com>

    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package pigeon;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import junit.framework.Test;
import junit.framework.TestSuite;
import pigeon.model.Organization;
import pigeon.model.Race;
import pigeon.model.ValidationException;
import pigeon.report.CompetitionReporter;
import pigeon.report.Reporter;

public class FederationRegressionTest extends RegressionTestBase {

    public FederationRegressionTest(String testName) {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(FederationRegressionTest.class);
        return suite;
    }
    
    @Override
    protected Organization createNamedOrganisation() throws ValidationException {
        return Organization.createEmpty().repSetName("Test fed").repSetType(Organization.Type.FEDERATION);
    }

    @Override
    protected String getPrefix() {
        return "Federation_";
    }

    @Override
    protected Race populateMembersEntered(Race race, Random random) {
        {
            Map<String, Integer> membersEntered = new TreeMap<String, Integer>();
            membersEntered.put("East", random.nextInt(50) + 50);
            membersEntered.put("West", random.nextInt(50) + 50);
            race = race.repSetMembersEntered(membersEntered);
        }
        {
            Map<String, Integer> birdsEntered = new TreeMap<String, Integer>();
            birdsEntered.put("East", random.nextInt(150) + 50);
            birdsEntered.put("West", random.nextInt(150) + 50);
            race = race.repSetBirdsEntered(birdsEntered);
        }
        return race;
    }
    
    public void testPoolReports() throws IOException
    {
        for (Race race: season.getRaces()) {
            Reporter reporter = new CompetitionReporter(season, race, configuration.getCompetitions(), configuration.getResultsFooter());
            RegressionStreamProvider streamProvider = new RegressionStreamProvider();
            reporter.write(streamProvider);

            checkRegression(streamProvider.getBytes("Pools.html"), "Pools_" + race.getRacepoint());
        }
    }    
}
