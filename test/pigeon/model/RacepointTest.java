/*
    Copyright (c) 2005, 2006, 2007, 2008, 2012 Paul Richards <paul.richards@gmail.com>

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

package pigeon.model;

import junit.framework.*;

/**
 *
 * @author Paul
 */
public final class RacepointTest extends TestCase {

    public RacepointTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RacepointTest.class);

        return suite;
    }

    public void testEquality() throws ValidationException {
        Racepoint foo = Racepoint.createEmpty().repSetName("Foo");
        Racepoint foo2 = Racepoint.createEmpty().repSetName("Foo");
        Racepoint bar = Racepoint.createEmpty().repSetName("Bar");

        assertEquals(foo, foo);
        assertEquals(foo, foo2);
        assertFalse(foo.equals(bar));
    }

    public void testExceptions() {
        try {
            Racepoint foo = Racepoint.createEmpty().repSetName("");
            assertTrue("Should throw", false);
        } catch (ValidationException ex) {
            assertEquals("Racepoint name is empty", ex.toString());
        }
    }
}
