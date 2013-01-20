package com.graph;

import database.models.*;
import java.util.*;

public class Vertex {

    //public char label; // label (e.g. ‘A’)
    public Map<String, String> label; // label (e.g. ‘A’)
    public boolean wasVisited;
// ------------------------------------------------------------

    public Vertex(Map<String, String> lab) {
        label = lab;
        wasVisited = false;
    }
// ------------------------------------------------------------
} // end class Vertex

