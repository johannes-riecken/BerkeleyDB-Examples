/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2012 Oracle and/or its affiliates.  All rights reserved.
 *
 * $Id$
 */

package collections.ship.entity;

import java.io.Serializable;

/**
 * Weight represents a weight amount and unit of measure.
 *
 * <p> In this sample, Weight is embedded in part data values which are stored
 * as Serial serialized objects; therefore Weight must be Serializable. </p>
 *
 * @author Mark Hayes
 */
public class Weight implements Serializable {

    public final static String GRAMS = "grams";
    public final static String OUNCES = "ounces";

    private final double amount;
    private final String units;

    public Weight(double amount, String units) {

        this.amount = amount;
        this.units = units;
    }

    public final double getAmount() {

        return amount;
    }

    public final String getUnits() {

        return units;
    }

    @Override
    public String toString() {

        return "[" + amount + ' ' + units + ']';
    }
}
