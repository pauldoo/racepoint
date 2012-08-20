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
package pigeon.model;

import java.io.Serializable;

public final class Average implements Serializable, Comparable<Average> {
    private static final long serialVersionUID = 1875506902582432415L;
        
    public final String name;
    
    private Average(String name) {
        this.name = name;
    }
    
    public static Average create(String name) throws ValidationException {
        name = name.trim();
        if (name.length() == 0) {
            throw new ValidationException("Average name is empty");
        }
        return new Average(name);
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Average other) {
        return name.compareTo(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return equals((Average)other);
    }
    
    public boolean equals(Average other) {
        return (this == other) || this.name.equals(other.name);
    }
}
