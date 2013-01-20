/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graph;

import database.models.*;
import java.util.*;

/**
 *
 * @author Epaminondas
 */
public class Graph {

    private final int MAX_VERTS = 50;
    public Vertex vertexList[]; // list of vertices
    private int adjMat[][]; // adjacency matrix
    private int nVerts; // current number of vertices
    private StackX theStack;
    private Queue theQueue;
    public Map<String, String> queryResult;
// -----------------------------------------------------------

    public Graph() // constructor
    {
        vertexList = new Vertex[MAX_VERTS];
        // adjacency matrix
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;
        for (int j = 0; j < MAX_VERTS; j++) // set adjacency
        {
            for (int k = 0; k < MAX_VERTS; k++) // matrix to 0
            {
                adjMat[j][k] = 0;
            }
        }
        theStack = new StackX();
    } // end constructor
// -----------------------------------------------------------

    public void addVertex(Map<String, String> lab) {
        vertexList[nVerts++] = new Vertex(lab);
    }
// -----------------------------------------------------------

    public void addEdge(int start, int end) {
        adjMat[start][end] = 1;
        adjMat[end][start] = 1;
    }
// ------------------------------------------------------------

    public void displayVertex(int v) {
        for (Map.Entry<String, String> entry : vertexList[v].label.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(">>['" + key + "'] = " + value);
        }
        //System.out.print("\n" + vertexList[v].label.getName());
        //System.out.print("\n" + vertexList[v].label);
    }
// ------------------------------------------------------------

    public void dfs(String considerAttribute, String searchKeyword) // depth-first search
    { // begin at vertex 0
        //String searchKeyword = "thara";

        vertexList[0].wasVisited = true; // mark it
        displayVertex(0); // display it
        theStack.push(0); // push it
        while (!theStack.isEmpty()) { // until stack empty,
            // get an unvisited vertex adjacent to stack top
            int v = getAdjUnvisitedVertex(theStack.peek());
            if (v == -1) { // if no such vertex,
                theStack.pop();
            } else {
                //System.out.print("\n INIT: ");

                try {
                    for (Map.Entry<String, String> entry : vertexList[v].label.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        //System.out.println(">>['" + key + "'] = " + value);

                        if (key.equalsIgnoreCase(considerAttribute) && value != null && value.matches(".*" + searchKeyword + ".*")) {
                            //System.out.println(v + " >>['" + key + "'] = " + value);
                            queryResult = vertexList[v].label;
                            //displayVertex(v); // display it
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Error :" + ex);
                }
                vertexList[v].wasVisited = true; // mark it
                theStack.push(v);
            }
        } // end while
        // stack is empty, so we’re done
        for (int j = 0; j < nVerts; j++) { // reset flags
            vertexList[j].wasVisited = false;
        }
        //System.out.println(theStack.peek());
    } // end dfs
// ------------------------------------------------------------

    public void bfs() // breadth-first search
    { // begin at vertex 0
        vertexList[0].wasVisited = true; // mark it
        displayVertex(0); // display it
        theQueue.insert(0); // insert at tail
        int v2;
        while (!theQueue.isEmpty()) // until queue empty,
        {
            int v1 = theQueue.remove(); // remove vertex at head
            // until it has no unvisited neighbors
            while ((v2 = getAdjUnvisitedVertex(v1)) != -1) { // get one,
                vertexList[v2].wasVisited = true; // mark it
                displayVertex(v2); // display it
                theQueue.insert(v2); // insert it
            } // end while
        } // end while(queue not empty)
        // queue is empty, so we’re done
        for (int j = 0; j < nVerts; j++) // reset flags
        {
            vertexList[j].wasVisited = false;
        }
    } // end bfs()
// -------------------------------------------------------------
// returns an unvisited vertex adj to v

    public int getAdjUnvisitedVertex(int v) {
        for (int j = 0; j < nVerts; j++) {
            if (adjMat[v][j] == 1 && vertexList[j].wasVisited == false) {
                return j;
            }
        }
        return -1;
    }    // end getAdjUnvisitedVertex()
// ------------------------------------------------------------
} // end class Graph

