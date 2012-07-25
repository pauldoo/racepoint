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
package pigeon.report;

import pigeon.model.Distance;
import pigeon.model.Member;

final class AverageResult implements Comparable<AverageResult> {
    public final Distance totalDistance;
    public final double totalTimeInSeconds;
    public final Member member;

    private AverageResult(Distance totalDistance, double totalTimeInSeconds, Member member) {
        this.totalDistance = totalDistance;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.member = member;
    }
    
    public static AverageResult createEmpty(final Member member) {
        return new AverageResult(Distance.createFromMetric(0.0), 0.0, member);
    }
    
    public AverageResult repAccumulate(final BirdResult raceResult) {
        return new AverageResult(
                Distance.createFromMetric(totalDistance.getMetres() + raceResult.distance.getMetres()),
                totalTimeInSeconds + raceResult.flyTimeInSeconds,
                member);
    }

    @Override
    public int compareTo(final AverageResult other) {
        return compare(this, other);
    }

    private static int compare(final AverageResult lhs, final AverageResult rhs) {
        if (lhs == rhs) {
            return 0;
        }

        int result = -Double.compare(
                lhs.averageVelocityInMetresPerSecond(),
                rhs.averageVelocityInMetresPerSecond());

        if (result == 0) {
            result = lhs.member.compareTo(rhs.member);
        }
        return result;
    }
    
    public double averageVelocityInMetresPerSecond() {
        return totalDistance.getMetres() / totalTimeInSeconds;
    }
}
