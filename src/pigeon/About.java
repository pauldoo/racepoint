/*
    Copyright (c) 2005, 2006, 2007, 2008, 2009, 2012, 2013 Paul Richards <paul.richards@gmail.com>

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import pigeon.report.Utilities;

/**
    Provides access to the public information about the program.
*/
public final class About
{
    public static final String VERSION = getBuildId();
    public static final String TITLE = "RacePoint " + VERSION;
    public static final String CREDITS = "Created by Paul Richards <paul.richards@gmail.com>.";
    public static final String WEBSITE = "http://pauldoo.com/racepoint/";

    // NonCreatable
    private About()
    {
    }

    /**
        Attempt to read the Git ID from the BuildID.txt file inside the Jar file.

        Returns "unknown" if this cannot be found.
    */
    private static String getBuildId()
    {
        InputStream in = ClassLoader.getSystemResourceAsStream("BuildID.txt");
        try {
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String buildId = reader.readLine();
                if (buildId != null) {
                    return buildId;
                }
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return "unknown";
    }

    /**
        Returns a copy of the license.
    */
    public static String getLicense()
    {
        final InputStream licenseStream = ClassLoader.getSystemResourceAsStream("COPYING.txt");
        try {
            return new String( Utilities.slurpStream(licenseStream) );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
