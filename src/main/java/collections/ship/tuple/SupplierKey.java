/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2012 Oracle and/or its affiliates.  All rights reserved.
 *
 * $Id$
 */

package collections.ship.tuple;

/**
 * A SupplierKey serves as the key in the key/data pair for a supplier entity.
 *
 * <p> In this sample, SupplierKey is bound to the key's tuple storage entry
 * using a TupleBinding.  Because it is not used directly as storage data, it
 * does not need to be Serializable. </p>
 *
 * @author Mark Hayes
 */
public class SupplierKey {

    private final String number;

    public SupplierKey(String number) {

        this.number = number;
    }

    public final String getNumber() {

        return number;
    }

    @Override
    public String toString() {

        return "[SupplierKey: number=" + number + ']';
    }
}
