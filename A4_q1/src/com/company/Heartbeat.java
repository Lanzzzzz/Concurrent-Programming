package com.company;
import java.util.Random;

import mpi.*;

public class Heartbeat
{
	public static void main(String[] args) throws Exception
	{

		MPI.Init(args);
		int rank=MPI.COMM_WORLD.Rank();		//process ID
		int size=MPI.COMM_WORLD.Size();		//size of nodes
	    int[][] relationship = new int[size][size]; //the relationship matrix
    	int[] relationship_info = new int[size*size];

    	//creat a random relationship information
	    if (rank==0)
	    {
	    	//link 1, no link 0.
    		Random r = new Random();
	    	int[][] temp_array= new int[size][size];
	    	for (int i = 0; i < size; i++)
	    	{
				for (int j = 0; j < size; j++)
				{
					if (i!=j)
					{
						temp_array[i][j] = r.nextInt(2);
						temp_array[j][i] = temp_array[i][j];
					}
				}
			}

	    	//copy array for sending
	    	for (int i = 0; i < size; i++)
	    	{
				for (int j = 0; j < size; j++)
				{
					relationship_info[i*size+j]=temp_array[i][j];
				}
			}
		}

	    //all nodes use the relationship to create their own neighbour info
	    MPI.COMM_WORLD.Bcast(relationship_info, 0, size*size, MPI.INT, 0);

	    for (int i = 0; i <size; i++)
	    {
	    	for (int j = i *size; j < i *size+size; j++)
	    	{
	    		relationship[i][(j %size)]= relationship_info[j];
	    	}
	    }

	    // creating neighbour info
	    int[] links = new int[size*size];
	    int[] link = new int[size];
	    for (int i=0; i<size; i++)
	    {
	    	for (int j=0; j<size; j++)
	    	{
	    		if (rank==i)
	    		{
	    			link[j]= relationship[i][j];
	    			if (rank==0) {
	    				links[j]= relationship[0][j];
	    			}
	    		}
	    	}
	    }

	    //send to neighbour
	    if (rank!=0)
	    	MPI.COMM_WORLD.Send(link, 0, size, MPI.INT, 0, rank);

	    //receive from neighbour
	    if (rank==0)
	    {
	    	for (int i = 1; i <size; i++) {
	    		MPI.COMM_WORLD.Recv(links, i *size, size, MPI.INT, i, i);
	    	}
	    }

	    //creating the final relationship matrix from received info
	    int[][] final_relationship =new int[size][size];
	    if (rank==0)
	    {
	    	for (int i = 0; i <size; i++) {
	    		for (int j = i *size; j < i *size+size; j++)
	    		{
	    			final_relationship[i][(j %size)]= links[j];
	    		}
	    	}
	    }

	    //print the final relationship
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				System.out.print(final_relationship[i][j]);
			}
			System.out.println();
		}


		MPI.Finalize();

	}

}
