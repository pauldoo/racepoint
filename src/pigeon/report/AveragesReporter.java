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
import java.util.List;
import java.util.SortedSet;
import pigeon.competitions.Competition;
import pigeon.model.Average;
import pigeon.model.Constants;
import pigeon.model.Organization;
import pigeon.model.Race;
import pigeon.model.Season;

/**
    Generates an HTML report for a single race.

    Federation and section results are generated on the same page.
*/
public final class AveragesReporter implements Reporter {


    private final Season season;
    private final Race race;
    private final List<Competition> competitions;
    private final String resultsFooter;

    /** Creates a new instance of RaceReporter */
    public AveragesReporter(
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
        if (season.getOrganization().getType() == Organization.Type.CLUB) {
            writeAveragesReport(streamProvider.createNewStream("Averages.html", true));
        } else {
            throw new IllegalArgumentException("Averages only available in Club mode.");
        }
    }

    private void writeAveragesReport(OutputStream stream) throws IOException {
        if (season.getOrganization().getType() != Organization.Type.CLUB) {
            throw new IllegalArgumentException("Averages only used in Club setting");
        }
        
        String raceDate = pigeon.view.Utilities.DATE_FORMAT.format(race.getLiberationDate());
        String raceTime = pigeon.view.Utilities.TIME_FORMAT_WITH_LOCALE.format(race.getLiberationDate());
        PrintStream out = Utilities.writeHtmlHeader(stream, race.getRacepoint().toString() + " on " + raceDate);

        out.println("<div class=\"outer last\">");
        out.println("<h1>" + season.getOrganization().getName() + "</h1>");
        out.println("<h2>Race from " + race.getRacepoint().toString() + "</h2>");
        out.println("<h3>Liberated at " + raceTime + " on " + raceDate + " in a " + race.getWindDirection() + " wind</h3>");
        
        SortedSet<Average> averages = pigeon.view.Utilities.getAverages(season.getRaces());
        for (Average avg: averages) {
            out.println("<h3>" + avg.name + "</h3>");
            out.println("<table>");
            out.println("<tr><th>Member</th><th>Miles</th><th>Yards</th><th>Time</th><th>Average</th></tr>");
            SortedSet<AverageResult> averagesResult = Averages.resultsForAverage(avg, season);
            for (AverageResult r: averagesResult) {
                out.print("<tr>");
                out.print("<td>" + r.member.getName() + "</td>");
                out.print("<td>" + r.totalDistance.getMiles() + "</td>");
                out.print("<td>" + r.totalDistance.getYardsRemainder() + "</td>");
                out.print("<td>" + formatTimespan(r.totalTimeInSeconds) + "</td>");
                out.print("<td class='numeric'>" + String.format("%.3f", r.averageVelocityInMetresPerSecond() * Constants.METRES_PER_SECOND_TO_YARDS_PER_MINUTE) + "</td>");
                out.print("</tr>");
                out.println();
            }
            out.println("</table>");
        }
        out.println("</div>");
        
        if (resultsFooter != null) {
            out.println("<h4>" + resultsFooter + "</h4>");
        }
        Utilities.writeHtmlFooter(out);
    }
    
    private static String formatTimespan(double totalTimeInSeconds) {
        final int seconds = (int)Math.round(totalTimeInSeconds);
        return String.format("%d:%02d:%02d",
                seconds / (60 * 60),
                (seconds / 60) % 60,
                seconds % 60);
    }
}
