/*
    Copyright (c) 2005, 2006, 2007, 2008, 2011, 2012 Paul Richards <paul.richards@gmail.com>

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

package pigeon.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import pigeon.competitions.Competition;
import pigeon.model.Average;
import pigeon.model.Clock;
import pigeon.model.Organization;
import pigeon.model.Constants;
import pigeon.model.Member;
import pigeon.model.Race;
import pigeon.model.Season;
import pigeon.model.Time;

/**
    Generates an HTML report for a single race.

    Federation and section results are generated on the same page.
*/
public final class RaceReporter implements Reporter {


    private final Season season;
    private final Race race;
    private final List<Competition> competitions;
    // Section -> Pool name -> Bird count
    private final Map<String, Map<String, Integer>> entrantsCount;
    private final String resultsFooter;

    /** Creates a new instance of RaceReporter */
    public RaceReporter(
        Season season,
        Race race,
        List<Competition> competitions,
        String resultsFooter
    ) {
        this.season = season;
        this.race = race;
        this.competitions = competitions;
        this.entrantsCount = race.getBirdsEnteredInPools();
        this.resultsFooter = resultsFooter;
    }

    @Override
    public void write(StreamProvider streamProvider) throws IOException
    {
        writeRaceReport(streamProvider.createNewStream("Race.html", true));
        writeCompetitionReport(streamProvider.createNewStream("Pools.html", true));
        if (season.getOrganization().getType() == Organization.Type.CLUB) {
            writeAveragesReport(streamProvider.createNewStream("Averages.html", true));
        }
    }

    private void writeRaceReport(final OutputStream raceReportStream) throws IOException {
        String raceDate = pigeon.view.Utilities.DATE_FORMAT.format(race.getLiberationDate());
        String raceTime = pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(race.getLiberationDate());
        PrintStream out = Utilities.writeHtmlHeader(raceReportStream, race.getRacepoint().toString() + " on " + raceDate);
        Organization club = season.getOrganization();
        
        List<String> sections = Utilities.participatingSections(club);
        // Push the null section to the front to guarantee we do the whole lot.
        sections.add(0, null);

        for (String section: sections) {
            final String sectionNotNull = (section == null) ? "Open" : section;
            if (section != sections.get(sections.size() - 1)) {
                out.println("<div class=\"outer\">");
            } else {
                out.println("<div class=\"outer last\">");
            }

            out.println("<h1>" + club.getName() + "</h1>");
            if (section != null) {
                out.println("<h2>Section: " + section + "</h2>");
            }
            out.println("<h2>Race from " + race.getRacepoint().toString() + "</h2>");
            out.println("<h3>Liberated at " + raceTime + " on " + raceDate + " in a " + race.getWindDirection() + " wind</h3>");
            SortedSet<BirdResult> results = new TreeSet<BirdResult>();

            for (Clock clock: race.getClocks()) {
                if (section != null && !clock.getMember().getSection().equals(section)) {
                    continue;
                }
                for (Time time: clock.getTimes()) {
                    BirdResult row = Utilities.calculateVelocity(club, race, clock, time);

                    row.html.append("<td>" + clock.getMember().getName() + "</td>");
                    if (listClubNames()) {
                        row.html.append("<td>" + clock.getMember().getClub() + "</td>");
                    }
                    if (clock.getBirdsEntered() > 0) {
                        row.html.append("<td>" + clock.getBirdsEntered() + "</td>");
                    } else {
                        row.html.append("<td/>");
                    }
                    if (race.getDaysCovered() > 1) {
                        int days = (int)((row.correctedClockTime.getTime() - race.liberationDayOffset().getTime()) / Constants.MILLISECONDS_PER_DAY);
                        row.html.append("<td>" + (days + 1) + "</td>");
                    }
                    row.html.append("<td>" + pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(row.correctedClockTime) + "</td>");
                    row.html.append("<td>" + row.distance.getMiles() + "</td>");
                    row.html.append("<td>" + row.distance.getYardsRemainder() + "</td>");
                    row.html.append("<td>" + time.getRingNumber() + "</td>");
                    row.html.append("<td>" + time.getColor() + "</td>");
                    row.html.append("<td>" + time.getSex().toString() + "</td>");
                    results.add(row);
                }
            }
            int memberCount;
            int birdCount;
            if (section != null) {
                memberCount = race.getMembersEntered().get(section);
                birdCount = race.getBirdsEntered().get(section);
            } else {
                memberCount = race.getTotalNumberOfMembersEntered();
                birdCount = race.getTotalNumberOfBirdsEntered();
            }
            out.println("<h3>" + memberCount + " members sent in a total of " + birdCount + " birds</h3>");
            out.println("<table>");
            out.print("<tr><th>Pos.</th><th>Member</th>");
            if (listClubNames()) {
                out.print("<th>Club</th>");
            }
            out.print("<th>No.<br/>birds</th>");
            if (race.getDaysCovered() > 1) {
                out.print("<th>Day</th>");
            }
            out.print("<th>Time</th><th>Miles</th><th>Yards</th><th>Ring No.</th><th>Colour</th><th>Sex</th><th>Pools</th><th/>");
            if (section != null) {
                out.print("<th class='numeric'>Prize</th>");
            }
            out.println("<th class='numeric'>Velocity</th></tr>");

            // For each competition name keep a track of how many of the winners we have found.
            Map<String, Integer> competitionPositions = new TreeMap<String, Integer>();
            for (Competition c: competitions) {
                competitionPositions.put(c.getName(), 0);
            }

            Map<String, Map<String, Integer>> birdsInPools = race.getBirdsEnteredInPools();
            // For each competition within this section, calculate the number of winners
            Map<String, Integer> numberOfWinners = new TreeMap<String, Integer>();
            for (Competition c: competitions) {
                if (section != null || c.isAvailableInOpen()) {
                    int entrants = birdsInPools.get(sectionNotNull).get(c.getName());
                    numberOfWinners.put(c.getName(), c.maximumNumberOfWinners(entrants));
                }
            }

            List<Double> prizes = (section == null) ? null : race.getPrizes().get(section);
            int pos = 0;
            for (BirdResult row: results) {
                pos ++;
                out.print("<tr><td>" + pos + "</td>");

                double totalPrizeWonByThisBird = 0.0;

                Collection<String> competitionsEnteredByThisBird = null;
                if (section == null) {
                    competitionsEnteredByThisBird = row.time.getOpenCompetitionsEntered();
                } else {
                    competitionsEnteredByThisBird = row.time.getSectionCompetitionsEntered();
                }

                StringBuffer competitionsWonByThisBird = new StringBuffer();
                // Check the competitions that this bird entered
                for (Competition c: competitions) {
                    if (competitionsEnteredByThisBird.contains(c.getName())) {
                        int position = competitionPositions.get(c.getName()) + 1;
                        if (position <= numberOfWinners.get(c.getName())) {
                            int entrants = birdsInPools.get(sectionNotNull).get(c.getName());
                            double prize = c.prize(position, entrants);
                            totalPrizeWonByThisBird += prize;
                            competitionPositions.put(c.getName(), position);
                            competitionsWonByThisBird.append(c.getName());
                        }
                    }
                }
                if (totalPrizeWonByThisBird > 0) {
                    row.html.append("<td>" + competitionsWonByThisBird + "</td>");
                    row.html.append("<td class='numeric'>" + String.format("%.2f", totalPrizeWonByThisBird) + "</td>");
                } else {
                    row.html.append("<td/>");
                    row.html.append("<td/>");
                }

                if (section != null) {
                    if (prizes != null && pos <= prizes.size() && prizes.get(pos-1) > 0) {
                        row.html.append("<td class='numeric'>" + String.format("%.2f", prizes.get(pos-1)) + "</td>");
                    } else {
                        row.html.append("<td/>");
                    }
                }

                row.html.append("<td class='numeric'>" + String.format("%.3f", row.velocityInMetresPerSecond * Constants.METRES_PER_SECOND_TO_YARDS_PER_MINUTE) + "</td>");

                out.print(row.html.toString());
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</div>");
        }
        if (resultsFooter != null) {
            out.println("<h4>" + resultsFooter + "</h4>");
        }
        Utilities.writeHtmlFooter(out);
    }

    private void writeCompetitionReport(final OutputStream competitionReportStream) throws IOException {
        String raceDate = pigeon.view.Utilities.DATE_FORMAT.format(race.getLiberationDate());
        String raceTime = pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(race.getLiberationDate());
        PrintStream out = Utilities.writeHtmlHeader(competitionReportStream, race.getRacepoint().toString() + " on " + raceDate);
        Organization club = season.getOrganization();

        List<String> sections = Utilities.participatingSections(club);
        // Push the null section to the front to guarantee we do the whole lot.
        sections.add(0, null);

        for (String section: sections) {
            final String sectionNotNull = (section == null) ? "Open" : section;

            if (section != sections.get(sections.size() - 1)) {
                out.println("<div class=\"outer\">");
            } else {
                out.println("<div class=\"outer last\">");
            }

            out.println("<h1>" + club.getName() + "</h1>");
            if (section != null) {
                out.println("<h2>Section: " + section + "</h2>");
            } else {
                out.println("<h2>Open</h2>");
            }
            out.println("<h2>Race from " + race.getRacepoint().toString() + "</h2>");
            out.println("<h3>Liberated at " + raceTime + " on " + raceDate + " in a " + race.getWindDirection() + " wind</h3>");
            int memberCount = 0;
            int birdCount = 0;
            SortedSet<BirdResult> results = new TreeSet<BirdResult>();

            for (Clock clock: race.getClocks()) {
                if (section != null && !clock.getMember().getSection().equals(section)) {
                    continue;
                }
                memberCount ++;
                for (Time time: clock.getTimes()) {
                    birdCount ++;
                    BirdResult row = Utilities.calculateVelocity(club, race, clock, time);

                    row.html.append("<td>" + clock.getMember().getName() + "</td>");
                    if (listClubNames()) {
                        row.html.append("<td>" + clock.getMember().getClub() + "</td>");
                    }
                    row.html.append("<td>" + time.getRingNumber() + "</td>");
                    results.add(row);
                }
            }

            out.println("<table>");
            out.print("<tr><th>Member</th>");
            if (listClubNames()) {
                out.print("<th>Club</th>");
            }
            out.print("<th>Ring Number</th>");
            for (Competition c: competitions) {
                if (section != null || c.isAvailableInOpen()) {
                    out.print("<th class='numeric'>" + c.getName() + "</th>");
                }
            }
            out.print("<th class='numeric'>Total</th>");
            out.println("</tr>");

            // For each competition name keep a track of how many of the winners we have found.
            Map<String, Integer> competitionPositions = new TreeMap<String, Integer>();
            for (Competition c: competitions) {
                if (section != null || c.isAvailableInOpen()) {
                    competitionPositions.put(c.getName(), 0);
                }
            }

            // For each competition within this section, calculate the number of winners
            Map<String, Integer> numberOfWinners = new TreeMap<String, Integer>();
            for (Competition c: competitions) {
                if (section != null || c.isAvailableInOpen()) {
                    int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                    numberOfWinners.put(c.getName(), c.maximumNumberOfWinners(entrants));
                }
            }

            // Iterate each of the birds, in order they would appear in the race result.
            for (BirdResult row: results) {
                double totalPrizeWonByThisBird = 0.0;

                Collection<String> competitionsEnteredByThisBird = null;
                if (section == null) {
                    competitionsEnteredByThisBird = row.time.getOpenCompetitionsEntered();
                } else {
                    competitionsEnteredByThisBird = row.time.getSectionCompetitionsEntered();
                }

                // Check the competitions that this bird entered
                for (Competition c: competitions) {
                    if (section != null || c.isAvailableInOpen()) {
                        if (competitionsEnteredByThisBird.contains(c.getName())) {
                            int position = competitionPositions.get(c.getName()) + 1;
                            if (position <= numberOfWinners.get(c.getName())) {
                                int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                                double prize = c.prize(position, entrants);
                                row.html.append("<td class='numeric'>" + String.format("%.2f", prize) + "</td>");
                                totalPrizeWonByThisBird += prize;
                                competitionPositions.put(c.getName(), position);
                                continue;
                            }
                        }
                        row.html.append("<td/>");
                    }
                }
                if (totalPrizeWonByThisBird > 0) {
                    // If this member has taken a place in any competition, print their line.
                    out.println("<tr>");
                    out.print(row.html.toString());
                    out.print("<td class='numeric'>" + String.format("%.2f", totalPrizeWonByThisBird) + "</td>");
                    out.println("</tr>");
                }
            }

            Map<String, Double> totalForCompetition = new TreeMap<String, Double>();
            {
                // Print totals for each competition
                out.print("<tr><td/><td/><td>Total</td>");
                double totalPrizeGivenToEveryone = 0.0;
                for (Competition c: competitions) {
                    if (section != null || c.isAvailableInOpen()) {
                        double totalPrizeGivenForThisCompetition = 0.0;
                        int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                        for (int pos = 1; pos <= competitionPositions.get(c.getName()); ++pos) {
                            totalPrizeGivenForThisCompetition += c.prize(pos, entrants);
                        }
                        totalForCompetition.put(c.getName(), totalPrizeGivenForThisCompetition);
                        totalPrizeGivenToEveryone += totalPrizeGivenForThisCompetition;
                        out.print("<td class='numeric'>" + String.format("%.2f", totalPrizeGivenForThisCompetition) + "</td>");
                    }
                }
                out.print("<td class='numeric'>" + String.format("%.2f", totalPrizeGivenToEveryone) + "</td>");
                out.println("</tr>");
            }

            {
                // Print unclaimed row
                out.print("<tr><td/><td/><td>Unclaimed</td>");
                double totalUnclaimed = 0.0;
                for (Competition c: competitions) {
                    if (section != null || c.isAvailableInOpen()) {
                        int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                        double unclaimed = c.totalPoolMoney(entrants) - c.totalClubTake(entrants) - totalForCompetition.get(c.getName());
                        totalUnclaimed += unclaimed;
                        out.print("<td class='numeric'>" + String.format("%.2f", unclaimed) + "</td>");
                    }
                }
                out.print("<td class='numeric'>" + String.format("%.2f", totalUnclaimed) + "</td>");
                out.println("</tr>");
            }

            {
                // Print club take row
                out.print("<tr><td/><td/><td>" + clubTakeString(competitions) + "</td>");
                double totalClubTake = 0.0;
                for (Competition c: competitions) {
                    if (section != null || c.isAvailableInOpen()) {
                        int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                        double clubTake = c.totalClubTake(entrants);
                        totalClubTake += clubTake;
                        out.print("<td class='numeric'>" + String.format("%.2f", clubTake) + "</td>");
                    }
                }
                out.print("<td class='numeric'>" + String.format("%.2f", totalClubTake) + "</td>");
                out.println("</tr>");
            }

            {
                // Print total pool money
                out.print("<tr><td/><td/><td>Total pool money</td>");
                double totalPoolMoney = 0.0;
                for (Competition c: competitions) {
                    if (section != null || c.isAvailableInOpen()) {
                        int entrants = entrantsCount.get(sectionNotNull).get(c.getName());
                        double poolMoney = c.totalPoolMoney(entrants);
                        totalPoolMoney += poolMoney;
                        out.print("<td class='numeric'>" + String.format("%.2f", poolMoney) + "</td>");
                    }
                }
                out.print("<td class='numeric'>" + String.format("%.2f", totalPoolMoney) + "</td>");
                out.println("</tr>");
            }

            // Done!
            out.println("</table>");
            out.println("</div>");
        }
        if (resultsFooter != null) {
            out.println("<h4>" + resultsFooter + "</h4>");
        }
        Utilities.writeHtmlFooter(out);
    }
    
    private void writeAveragesReport(OutputStream stream) throws IOException {
        if (season.getOrganization().getType() != Organization.Type.CLUB) {
            throw new IllegalArgumentException("Averages only used in Club setting");
        }
        
        String raceDate = pigeon.view.Utilities.DATE_FORMAT.format(race.getLiberationDate());
        String raceTime = pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(race.getLiberationDate());
        PrintStream out = Utilities.writeHtmlHeader(stream, race.getRacepoint().toString() + " on " + raceDate);

        SortedSet<Average> averages = pigeon.view.Utilities.getAverages(season.getRaces());
        for (Average avg: averages) {
            Collection<Race> relevantRaces = relevantRaces(season.getRaces(), avg);
            Collection<Member> survivingMembers = completedAllRaces(relevantRaces);
            SortedSet<AverageResult> averagesResult = averageResults(season.getOrganization(), relevantRaces, survivingMembers);
            for (AverageResult r: averagesResult) {
                String foo = String.format("<p>For average '%s', member '%s', has avg velocity '%f' m/s.</p>", 
                        avg.name,
                        r.member.getName(),
                        r.averageVelocityInMetresPerSecond());
                out.println(foo);
            }
        }
        
        if (resultsFooter != null) {
            out.println("<h4>" + resultsFooter + "</h4>");
        }
        Utilities.writeHtmlFooter(out);
    }
    
    /**
        Returns the club take as a string, ie "5.0%".

        If the competitions have different settings for the club takes, then
        the string "Club Take" is returned.
    */
    private static String clubTakeString(Collection<Competition> competitions)
    {
        if (competitions.isEmpty()) {
            throw new IllegalArgumentException("No competitions");
        }
        final double clubTakeOfFirstCompetition = competitions.iterator().next().getClubTake();
        for (Competition c: competitions) {
            if (c.getClubTake() != clubTakeOfFirstCompetition) {
                return "Club Take";
            }
        }
        return String.format("%.1f%%", clubTakeOfFirstCompetition * 100.0);
    }

    private boolean listClubNames() {
        switch (season.getOrganization().getType()) {
            case FEDERATION:
                return true;
            case CLUB:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Collection<Race> relevantRaces(List<Race> races, Average avg) {
        List<Race> result = new ArrayList<Race>();
        for (Race r: races) {
            if (r.getAverages().contains(avg)) {
                result.add(r);
            }
        }
        return Collections.unmodifiableList(result);
    }

    private static Collection<Member> completedAllRaces(Collection<Race> races) {
        Set<Member> result = null;
        for (Race r: races) {
            Set<Member> membersWhoCompleted = membersWhoCompleted(r);
            if (result == null) {
                result = new HashSet<Member>(membersWhoCompleted);
            } else {
                result.retainAll(membersWhoCompleted);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private static Set<Member> membersWhoCompleted(Race r) {
        Set<Member> result = new HashSet<Member>();
        for (Clock c: r.getClocks()) {
            if (c.getTimes().isEmpty() == false) {
                result.add(c.getMember());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private static SortedSet<AverageResult> averageResults(Organization club, Collection<Race> relevantRaces, Collection<Member> members) {
        SortedSet<AverageResult> allResults = new TreeSet<AverageResult>();
        for (Member m: members) {
            AverageResult result = AverageResult.createEmpty(m);
            for (Race r: relevantRaces) {
                result = result.repAccumulate(bestBirdInRaceForMember(r, m, club));
            }
            allResults.add(result);
        }
        return Collections.unmodifiableSortedSet(allResults);
    }
    
    private static BirdResult bestBirdInRaceForMember(Race r, Member m, Organization club) {
        BirdResult result = null;
        for (Clock c: r.getClocks()) {
            if (c.getMember().equals(m)) {
                for (Time t: c.getTimes()) {
                    BirdResult br = Utilities.calculateVelocity(club, r, c, t);
                    if (result == null ||
                            br.compareTo(result) < 0) {
                        result = br;
                    }
                }
            }
        }
        
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException("Member did not have a finishing bird in this race");
        }
    }

}
