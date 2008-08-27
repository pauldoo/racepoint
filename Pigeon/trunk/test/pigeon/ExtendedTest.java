/*
    Copyright (C) 2005, 2006, 2007, 2008  Paul Richards.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package pigeon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;
import pigeon.competitions.Competition;
import pigeon.model.Clock;
import pigeon.model.Constants;
import pigeon.model.Distance;
import pigeon.model.Member;
import pigeon.model.Organization;
import pigeon.model.Race;
import pigeon.model.Racepoint;
import pigeon.model.Season;
import pigeon.model.Sex;
import pigeon.model.Time;
import pigeon.model.ValidationException;
import pigeon.report.DistanceReporter;
import pigeon.report.MembersReporter;
import pigeon.report.RaceReporter;
import pigeon.view.Configuration;
import pigeon.view.Utilities;

/**
    Some extended regression based tests for the app.
*/
public final class ExtendedTest extends TestCase
{
    private static final boolean UPDATE_OK_FILES = false;

    final Random random = new Random(0);
    Configuration configuration;
    Season season;

    static final int RACEPOINT_COUNT = 13;
    static final int CLUB_COUNT = 17;
    static final int BIRDS_PER_MEMBER = 19;
    static final int MEMBER_COUNT = 23;

    public ExtendedTest(String testName) {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(ExtendedTest.class);

        return suite;
    }

    @Override
    protected void setUp() throws ValidationException, IOException
    {
        BufferedInputStream configIn = null;
        try {
            configIn = new BufferedInputStream(new FileInputStream("regression/configuration.xml"));
            configuration = new Configuration(configIn);
        } finally {
            if (configIn != null) {
                configIn.close();
            }
        }

        season = Season.createEmpty();
        season = season.repSetOrganization(createOraganization());
        addRaces();
    }

    private void addRaces() throws ValidationException
    {
        for (Racepoint r: season.getOrganization().getRacepoints()) {
            Race race = new Race();
            race.setRacepoint(r);
            final int year = random.nextInt(10) + Utilities.YEAR_DISPLAY_START;
            final int month = random.nextInt(12) + GregorianCalendar.JANUARY;
            final int day = random.nextInt(20) + 1;
            // Released between 6am and 9:59am
            final int hour = random.nextInt(4) + 6;
            final int minute = random.nextInt(60);

            long liberation = new GregorianCalendar(year, month, day, hour, minute).getTimeInMillis();
            race.setLiberationDate(new Date(liberation));
            int daysCovered = random.nextInt(3) + 1; // 1, 2, or 3
            race.setDaysCovered(daysCovered);

            int darknessBegins = (int)((random.nextDouble() * 6 + 15) * Constants.MILLISECONDS_PER_HOUR);
            int darknessEnds = (int)((random.nextDouble() * 6 + 3) * Constants.MILLISECONDS_PER_HOUR);
            if (daysCovered >= 2) {
                race.setHoursOfDarkness(darknessBegins, darknessEnds);
            }
            
            {
                Map<String, Integer> membersEntered = new TreeMap<String, Integer>();
                membersEntered.put("East", random.nextInt(50) + 50);
                membersEntered.put("West", random.nextInt(50) + 50);
                race.setMembersEntered(membersEntered);
            }
            {
                Map<String, Integer> birdsEntered = new TreeMap<String, Integer>();
                birdsEntered.put("East", random.nextInt(150) + 50);
                birdsEntered.put("West", random.nextInt(150) + 50);
                race.setBirdsEntered(birdsEntered);
            }

            for (int i = 0; i < season.getOrganization().getNumberOfMembers(); i++) {
                Member m = season.getOrganization().getMembers().get(i);
                Clock clock = new Clock();
                clock.setMember(m);

                long setTime = liberation - random.nextInt((int)Constants.MILLISECONDS_PER_DAY);
                clock.setTimeOnMasterWhenSet(new Date(setTime));
                clock.setTimeOnMemberWhenSet(new Date(setTime));

                long masterOpenTime = (long)(liberation + (daysCovered + random.nextDouble()) * Constants.MILLISECONDS_PER_DAY);
                clock.setTimeOnMasterWhenOpened(new Date(masterOpenTime));

                long memberOpenTime = (long)(masterOpenTime + (random.nextDouble() - 0.5) * Constants.MILLISECONDS_PER_MINUTE);
                clock.setTimeOnMemberWhenOpened(new Date(memberOpenTime));

                clock.setBirdsEntered(30);

                for (int j = 0; j < BIRDS_PER_MEMBER; j++) {
                    Time t = new Time();
                    String ringNumber = "M" + i + "B" + j;
                    t.setRingNumber(ringNumber);

                    long clockInTime = (long)(
                        random.nextInt(daysCovered) * Constants.MILLISECONDS_PER_DAY +
                        (random.nextDouble() * 6 + 9) * Constants.MILLISECONDS_PER_HOUR);
                    assert(setTime + clockInTime <= masterOpenTime);
                    t.setMemberTime(clockInTime, daysCovered);
                    Time previous = Utilities.findBirdEntry(season, ringNumber);
                    if (previous != null) {
                        t.setColor(previous.getColor());
                        t.setSex(previous.getSex());
                    } else {
                        String color;
                        int colorCode = random.nextInt(5);
                        switch (colorCode) {
                            case 0:
                                color = "Pink";
                                break;
                            case 1:
                                color = "Purple";
                                break;
                            case 2:
                                color = "Green";
                                break;
                            case 3:
                                color = "Blue";
                                break;
                            case 4:
                                color = "Brown";
                                break;
                            default:
                                throw new IllegalArgumentException("Bug in test: " + colorCode);
                        }
                        t.setColor(color);
                        t.setSex(Sex.values()[random.nextInt(Sex.values().length)]);
                    }

                    for (Competition c: configuration.getCompetitions()) {
                        if (random.nextBoolean()) {
                            if (c.isAvailableInOpen()) {
                                Collection<String> set = new ArrayList<String>(t.getOpenCompetitionsEntered());
                                set.add(c.getName());
                                t.setOpenCompetitionsEntered(set);
                            }
                        }
                        if (random.nextBoolean()) {
                            Collection<String> set = new ArrayList<String>(t.getSectionCompetitionsEntered());
                            set.add(c.getName());
                            t.setSectionCompetitionsEntered(set);
                        }
                    }

                    clock.addTime(t);
                }
                race.addClock(clock);
            }
            // Need to randomly decide how many birds entered each pool.
            Map<String, Map<String, Integer>> entrantsCount = new TreeMap<String, Map<String, Integer>>();
            String[] sections = new String[]{"Open", "East", "West"};
            for (String section: sections) {
                entrantsCount.put(section, new TreeMap<String, Integer>());
                for (Competition pool: configuration.getCompetitions()) {
                    entrantsCount.get(section).put(pool.getName(), (int)((random.nextDouble()) * MEMBER_COUNT * BIRDS_PER_MEMBER));
                }
            }
            race.setBirdsEnteredInPools(entrantsCount);
            
            Map<String, List<Double>> prizes = new TreeMap<String, List<Double>>();
            for (int s = 1; s < sections.length; s++) {
                prizes.put(sections[s], new ArrayList<Double>());
                for (int i = 0; i < 20; ++i) {
                    prizes.get(sections[s]).add(new Double((20 - i) * s));
                }
            }
            race.setPrizes(prizes);
            
            season = season.repAddRace(race);
        }
    }

    private Organization createOraganization() throws ValidationException
    {
        Organization org = Organization.createEmpty();
        org = org.repSetName("Test fed");

        final String[][] clubs = new String[CLUB_COUNT][];
        for (int i = 0; i < CLUB_COUNT; i++) {
            clubs[i] = new String[2];
            clubs[i][0] = "Club #" + i;
            clubs[i][1] = random.nextBoolean() ? "East" : "West";
        }

        for (int i = 0; i < MEMBER_COUNT; i++) {
            Member m = new Member();
            m.setName("Member #" + i);
            StringBuffer address = new StringBuffer();
            address.append("Line 1" + "\n");
            address.append("Line 2" + "\n");
            m.setAddress(address.toString());
            m.setSHUNumber("SHU" + (i * 7));
            m.setTelephone("" + (i * 11));
            int clubIndex = random.nextInt(clubs.length);
            m.setClub(clubs[clubIndex][0]);
            m.setSection(clubs[clubIndex][1]);
            org = org.repAddMember(m);
        }

        for (int i = 0; i < RACEPOINT_COUNT; i++) {
            Racepoint r = new Racepoint();
            r.setName("Racepoint #" + i);
            org = org.repAddRacepoint(r);
        }

        for (Member m: org.getMembers()) {
            for (Racepoint r: org.getRacepoints()) {
                int miles = random.nextInt(200) + 600;
                int yards = random.nextInt(Constants.YARDS_PER_MILE);
                Distance d = Distance.createFromImperial(miles, yards);
                org = org.repSetDistance(m, r, d);
            }
        }

        return org;
    }

    @Override
    protected void tearDown()
    {
    }

    private void checkRegression(final byte[] tmpData, String name) throws IOException
    {
        final File tmpFile = new File("regression/" + name + (UPDATE_OK_FILES ? ".ok" : ".tmp"));
        final File okFile = new File("regression/" + name + ".ok");

        OutputStream tmpOut = new FileOutputStream(tmpFile);
        try {
            tmpOut.write(tmpData);
        } finally {
            tmpOut.close();
        }

        final byte[] okData = new byte[(int)okFile.length()];
        InputStream okIn = new FileInputStream(okFile);
        try {
            okIn.read(okData);
        } finally {
            okIn.close();
        }

        assertTrue("Regression failure: '" + name + "'", Arrays.equals(okData, tmpData));
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(season);
        objectOut.close();

        final byte[] savedOnce = out.toByteArray();
        checkRegression(savedOnce, "Serialization");

        OutputStream pcsOut = null;
        try {
            pcsOut = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream("regression/Serialization.pcs")));
            pcsOut.write(savedOnce);
        } finally {
            if (pcsOut != null) {
                pcsOut.close();
            }
        }

        ByteArrayInputStream in = new ByteArrayInputStream(savedOnce);
        ObjectInputStream objectIn = new ObjectInputStream(in);

        Season reloaded = (Season)objectIn.readObject();

        out = new ByteArrayOutputStream();
        objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(reloaded);
        objectOut.close();

        final byte[] savedAgain = out.toByteArray();

        assertTrue("Resaving should not break anything", Arrays.equals(savedOnce, savedAgain));
    }

    public void testMemberDistanceReports() throws IOException
    {
        for (Member member: season.getOrganization().getMembers()) {
            DistanceReporter<Racepoint> reporter = new DistanceReporter<Racepoint>(
                season.getOrganization().getName(),
                member.toString(),
                "Racepoint",
                season.getOrganization().getDistancesForMember(member));
            
            RegressionStreamProvider streamProvider = new RegressionStreamProvider();
            reporter.write(streamProvider);
            checkRegression(streamProvider.getBytes(), "Distance_" + member.getName());
        }
    }

    public void testRacepointDistanceReports() throws IOException
    {
        for (Racepoint racepoint: season.getOrganization().getRacepoints()) {
            DistanceReporter<Member> reporter = new DistanceReporter<Member>(
                season.getOrganization().getName(),
                racepoint.toString(),
                "Member",
                season.getOrganization().getDistancesForRacepoint(racepoint));

            RegressionStreamProvider streamProvider = new RegressionStreamProvider();
            reporter.write(streamProvider);
            checkRegression(streamProvider.getBytes(), "Distance_" + racepoint.getName());
        }
    }

    public void testRaceReports() throws IOException
    {
        for (Race race: season.getRaces()) {
            RaceReporter reporter = new RaceReporter(season.getOrganization(), race, true, configuration.getCompetitions(), configuration.getResultsFooter());
            RegressionStreamProvider streamProvider = new RegressionStreamProvider();
            reporter.write(streamProvider);

            checkRegression(streamProvider.getBytes("Race.html"), "Race_" + race.getRacepoint());
        }
    }

    public void testPoolReports() throws IOException
    {
        for (Race race: season.getRaces()) {
            RaceReporter reporter = new RaceReporter(season.getOrganization(), race, true, configuration.getCompetitions(), configuration.getResultsFooter());
            RegressionStreamProvider streamProvider = new RegressionStreamProvider();
            reporter.write(streamProvider);

            checkRegression(streamProvider.getBytes("Pools.html"), "Pools_" + race.getRacepoint());
        }
    }

    public void testMembersReport() throws IOException
    {
        MembersReporter reporter = new MembersReporter(
            season.getOrganization().getName(),
            season.getOrganization().getMembers(),
            configuration.getMode());
        RegressionStreamProvider streamProvider = new RegressionStreamProvider();
        reporter.write(streamProvider);

        checkRegression(streamProvider.getBytes("members.xml"), "MembersXml");
        checkRegression(streamProvider.getBytes("members.csv"), "MembersCsv");

        applyXslTransforms(streamProvider);
        checkRegression(streamProvider.getBytes("members.xhtml"), "MembersXhtml");
    }
    
    private static void applyXslTransforms(RegressionStreamProvider streams) throws IOException
    {
        try {
            Set<String> filenames = streams.getFilenames();
            for (String filename: filenames) {
                if (filename.endsWith(".xml")) {
                    byte[] xmlFile = streams.getBytes(filename);
                    verifyXmlStylesheet(xmlFile);
                    byte[] xslFile = streams.getBytes(pigeon.report.Constants.XSL_FOR_XHTML_FILENAME);

                    Transformer xslTransformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new ByteArrayInputStream(xslFile)));

                    String outputFilename = filename.substring(0, filename.length() - 4) + ".xhtml";

                    xslTransformer.transform(
                            new StreamSource(new ByteArrayInputStream(xmlFile)),
                            new StreamResult(streams.createNewStream(outputFilename, false)));
                }
            }
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }
    
    private static void verifyXmlStylesheet(byte[] xmlFile) throws IOException
    {
        try {
            InputStream xmlStream = new ByteArrayInputStream(xmlFile);
            Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlStream);

            Node child = xmlDocument.getFirstChild();
            while (child != null) {
                if (child instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)child;
                    if (pi.getTarget().equals("xml-stylesheet")) {
                        if (pi.getData().equals("type=\"text/xsl\" href=\"" + pigeon.report.Constants.XSL_FOR_XHTML_FILENAME + "\"")) {
                            return;
                        } else {
                            throw new IOException("xml-stylesheet processing instrction is incorrect");
                        }
                    }
                }
                child = child.getNextSibling();
            }

            throw new IOException("xml-stylesheet processing instruction is missing");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }
}
