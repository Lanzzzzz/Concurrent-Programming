//package com.company;
//
//import mpi.MPI;
//
//import java.util.Random;
//
//public class Main {
//
//    public static void main(String[] args) {
//	// write your code here
//        int rank=0;		//process ID
//        int size=4;		//size of nodes
//        int[][] relationship = new int[size][size]; //the relationship matrix
//        int[] exchange_info = new int[size*size];
//
//
//        //creat a relationship matrix
//        if (rank==0)
//        {
//            //1 shows there is a link between two nodes, otherwise 0.
//            Random r = new Random();
//            int[][] temp_array= new int[size][size];
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    if (i!=j) {
//                        temp_array[i][j] = r.nextInt(2);
//                        temp_array[j][i] = temp_array[i][j];
//                    }
//                }
//            }
//
//            //Transforming 2-dimensional matrix into a 1-dimensional array
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    exchange_info[i*size+j]=temp_array[i][j];
//                }
//            }
//        }
//
//
//        //broadcasting the array to all nodes just for extracting require information
//       // MPI.COMM_WORLD.Bcast(exchange_info, 0, size*size, MPI.INT, 0);
//
//        for (int r=0; r<size; r++) {
//            for (int c=r*size; c<r*size+size; c++)
//            {
//                relationship[r][(c%size)]= exchange_info[c];
//            }
//        }
//
//
//        // by following commands each nodes will know about the number of its neighbors
//        int[] neighbours = new int[size*size];
//        int[] neighbour = new int[size];
//        for (int i=0; i<size; i++) {
//            for (int j=0; j<size; j++){
//                if (rank==i) {
//                    neighbour[j]= relationship[i][j];
//                    if (rank==0) {
//                        neighbours[j]= relationship[0][j];
//                    }
//                }
//            }
//        }
//
//        // node with rank 0 is central node. Therefore, all other nodes must sent their information to node 0
//       // if (rank!=0)
//       //     MPI.COMM_WORLD.Send(neighbour, 0, size, MPI.INT, 0, rank);
//
//        //node 0 will receive all information
////        if (rank==0) {
////            for (int r=1; r<size; r++) {
////                MPI.COMM_WORLD.Recv(neighbours, r*size, size, MPI.INT, r, r);
////            }
////        }
//
//        //Transforming a 1-dimensional array to a 2-dimensional array by node 0
//        int[][] final_array=new int[size][size];
//        if (rank==0) {
//            for (int r=0; r<size; r++) {
//                for (int c=r*size; c<r*size+size; c++)
//                {
//                    final_array[r][(c%size)]=neighbours[c];
//                }
//            }
//        }
//     //   MPI.Finalize();
//
//
//    }
//}
