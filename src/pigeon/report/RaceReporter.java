/*
    Copyright (c) 2005, 2006, 2007, 2008, 2011, 2012, 2013 Paul Richards <paul.richards@gmail.com>

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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import pigeon.competitions.Competition;
import pigeon.model.Clock;
import pigeon.model.Constants;
import pigeon.model.Organization;
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
        this.resultsFooter = resultsFooter;
    }

    @Override
    public void write(StreamProvider streamProvider) throws IOException
    {
        writeRaceReport(streamProvider.createNewStream("Race.html", true));
    }

    private void writeRaceReport(final OutputStream raceReportStream) throws IOException {
        String raceDate = pigeon.view.Utilities.DATE_FORMAT.format(race.getLiberationDate());
        String raceTime = pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(race.getLiberationDate());
        PrintStream out = Utilities.writeHtmlHeader(raceReportStream, race.getRacepoint().toString() + " on " + raceDate, season.getOrganization().getType());
        Organization club = season.getOrganization();
        
        List<String> sections = pigeon.model.Utilities.allSections(club, true);

        for (String section: sections) {
            final boolean isOpenSection = section.equals(pigeon.model.Utilities.OPEN_SECTION);
            if (!section.equals(sections.get(sections.size() - 1))) {
                out.println("<div class=\"outer\">");
            } else {
                out.println("<div class=\"outer last\">");
            }

            out.println("<h1>" + club.getName() + "</h1>");
            if (!isOpenSection) {
                out.println("<h2>Section: " + section + "</h2>");
            }
            out.println("<h2>Race from " + race.getRacepoint().toString() + "</h2>");
            out.println("<h3>Liberated at " + raceTime + " on " + raceDate + " in a " + race.getWindDirection() + " wind</h3>");
            SortedSet<BirdResult> results = new TreeSet<BirdResult>();

            for (Clock clock: race.getClocks()) {
                if (!isOpenSection && !clock.getMember().getSection(club.getType()).equals(section)) {
                    continue;
                }
                for (Time time: clock.getTimes()) {
                    BirdResult row = Utilities.calculateVelocity(club, race, clock, time);

                    row.html.append("<td>" + clock.getMember().getName() + "</td>");
                    if (listClubNames()) {
                        row.html.append("<td>" + clock.getMember().getClub(club.getType()) + "</td>");
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
            if (!isOpenSection) {
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
            out.print("<th>Time</th><th>Miles</th><th>Yards</th><th>Ring No.</th><th>Colour</th><th>Sex</th>");
            if (club.getType() == Organization.Type.FEDERATION) {
                out.print("<th>Pools</th><th/>");
            }
            if (!isOpenSection) {
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
                if (!isOpenSection || c.isAvailableInOpen()) {
                    int entrants = birdsInPools.get(section).get(c.getName());
                    numberOfWinners.put(c.getName(), c.maximumNumberOfWinners(entrants));
                }
            }

            List<Double> prizes = (isOpenSection) ? null : race.getPrizes().get(section);
            int pos = 0;
            for (BirdResult row: results) {
                pos ++;
                out.print("<tr><td>" + pos + "</td>");

                double totalPrizeWonByThisBird = 0.0;

                Collection<String> competitionsEnteredByThisBird = null;
                if (isOpenSection) {
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
                            int entrants = birdsInPools.get(section).get(c.getName());
                            double prize = c.prize(position, entrants);
                            totalPrizeWonByThisBird += prize;
                            competitionPositions.put(c.getName(), position);
                            competitionsWonByThisBird.append(c.getName());
                        }
                    }
                }
                if (club.getType() == Organization.Type.FEDERATION) {
                    if (totalPrizeWonByThisBird > 0) {
                        row.html.append("<td>" + competitionsWonByThisBird + "</td>");
                        row.html.append("<td class='numeric'>" + String.format("%.2f", totalPrizeWonByThisBird) + "</td>");
                    } else {
                        row.html.append("<td/>");
                        row.html.append("<td/>");
                    }
                }

                if (!isOpenSection) {
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
}
