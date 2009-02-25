/*  This file is part of org.thanlwinsoft.myanmar and is distributed under the 
 *  terms of the GNU General Public License. 
 *
 *  org.thanlwinsoft.myanmar is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  org.thanlwinsoft.myanmar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with XQEngine; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.thanlwinsoft.myanmar.junitTest;

import junit.framework.*;

	/**
	 * The main driver for the junit test suite. 
	 * 
	 * <P>Can be invoked as:
	 * <BR>
	 * <PRE>
	 *        org.thanlwinsoft.myanmar.test.TestDriver
	 * </PRE>
	 * 
	 * <P>Make sure that junit.jar is on the classpath.
	 * 
	 */

public class TestDriver extends TestCase
{
	/**
	 * 
	 * @return test
	 */
	public static Test suite()
	//------------------------
	{
		TestSuite suite = new TestSuite();

		suite.addTest( LineBreakTest.suite() );
		
    return suite;
	}
	/**
	 * run tests
	 * @param args
	 */
	public static void main( String[] args )
	//--------------------------------------
	{
		junit.textui.TestRunner.run( suite() );
	}
}
