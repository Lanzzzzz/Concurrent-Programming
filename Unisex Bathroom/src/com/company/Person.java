/*****************************************************************
 * Memorial University of Newfoundland
 * 7894 Concurrent Programming
 * Final Exam Q3 Sample Solution
 *
 * Person.java -- Models a person.
 *
 * @author Dennis Peters ($Author: dpeters $)
 * @version $Revision: 870 $ $Date: 2009-08-07 09:29:53 -0230 (Fri, 07 Aug 2009) $
 * 
 * $Id: Person.java 870 2009-08-07 11:59:53Z dpeters $
 *
 */
package com.company;

/**
 * @author dpeters
 *
 */
public class Person implements Runnable {
	  private Bathroom door; // Shared by all Persons
	  private int myId; // Person Id
	  private Sex mySex;
	  

	  public Person(Bathroom d, Sex s, int id)
	    { door = d; myId = id; mySex = s; }

	  public void run()
	  {
		  try {
			  while (true)
			  {
				  Thread.sleep((int)Math.round(Math.random()*2000));
				  System.out.println("Person " + myId + " is has to go.");
				  door.enter(myId, mySex);
				  System.out.println("Person " + myId + " (" + mySex + ") is in the loo. current gender = "+ door.current_g);
				  Thread.sleep((int)Math.round(Math.random()*5000));
				  door.leave(mySex);
				  System.out.println("Person " + myId + " (" + mySex + ") done. current gender = "+ door.current_g);
			  }
		  }
		  catch (InterruptedException e) {}
	  }


}
