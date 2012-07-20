/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pigeon.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import pigeon.model.Average;
import pigeon.model.Clock;
import pigeon.model.Member;
import pigeon.model.Organization;
import pigeon.model.Race;
import pigeon.model.Season;
import pigeon.model.Time;

final class Averages {
    private Averages() {
    }
    
    static SortedSet<AverageResult> resultsForAverage(Average avg, Season season) {
        Collection<Race> relevantRaces = relevantRaces(season.getRaces(), avg);
        Collection<Member> survivingMembers = completedAllRaces(relevantRaces);
        SortedSet<AverageResult> averagesResult = averageResults(season.getOrganization(), relevantRaces, survivingMembers);
        return averagesResult;
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
