package org.thanlwinsoft.myanmar;
/*
 * Copyright:   Copyright (c) 2009 http://www.thanlwinsoft.org
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Interface for a validator
 * @author keith
 *
 */
public interface Validator
{
	/**
     * @author keith
     * Validation status
     */
    public enum Status
    {
        /**
         * valid
         */
        Valid,
        /**
         * string modified to be correct
         */
        Corrected, 
        /**
         * invalid, don't know how to correct
         */
        Invalid
    }
	/**
	 * Perform validation
	 * @param r input reader
	 * @param w output writer
	 * @return validation status
	 */
	public Status validate(BufferedReader r, BufferedWriter w);
	
	/**
     * 
     * @return line number
     */
    public long getLineNumber();
    /**
     * 
     * @return column index
     */
    public long getColumn();

    /**
     * number of errors found since reset
     * @return error count
     */
    public long getErrorCount();
    /**
     * reset line, col number
     */
    public void reset();
}
