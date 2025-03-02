/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2012 Oracle and/or its affiliates.  All rights reserved.
 *
 * $Id$
 */

package collections.ship.factory;

import com.sleepycat.bind.tuple.MarshalledTupleEntry;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * A PartKey serves as the key in the key/data pair for a part entity.
 *
 * <p> In this sample, PartKey is bound to the stored key tuple entry by
 * implementing the MarshalledTupleEntry interface, which is called by {@link
 * SampleViews.MarshalledKeyBinding}. </p>
 *
 * @author Mark Hayes
 */
public class PartKey implements MarshalledTupleEntry {

    private String number;

    public PartKey(String number) {

        this.number = number;
    }

    public final String getNumber() {

        return number;
    }

    @Override
    public String toString() {

        return "[PartKey: number=" + number + ']';
    }

    // --- MarshalledTupleEntry implementation ---

    public PartKey() {

        // A no-argument constructor is necessary only to allow the binding to
        // instantiate objects of this class.
    }

    @Override
    public void marshalEntry(TupleOutput keyOutput) {

        keyOutput.writeString(this.number);
    }

    @Override
    public void unmarshalEntry(TupleInput keyInput) {

        this.number = keyInput.readString();
    }
}
