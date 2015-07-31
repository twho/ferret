/*******************************************************************************
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.google.research.ic.ferret.test;

import static org.junit.Assert.assertTrue;

import com.google.research.ic.ferret.data.Event;
import com.google.research.ic.ferret.data.FilterSpec;
import com.google.research.ic.ferret.data.FilteredResultSet;
import com.google.research.ic.ferret.data.LogLoader;
import com.google.research.ic.ferret.data.ResultSet;
import com.google.research.ic.ferret.data.SearchEngine;
import com.google.research.ic.ferret.data.Snippet;
import com.google.research.ic.ferret.data.SubSequence;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: Insert description here. (generated by marknewman)
 */
public class NewSearchEngineTest {

  private List<Snippet> logs = null;
  private List<Snippet> queries = null;
  private Snippet simpleSnippet = null;
  private Snippet simpleLog = null;
  private int nGramLength = 2;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // load logs and queries
    LogLoader.getLogLoader().setLogType(LogLoader.ACCESSIBILITIY_LOG);
    logs = LogLoader.getLogLoader().loadLogs("search-engine-test-1");
    queries = LogLoader.getLogLoader().loadLogs("search-engine-test-1" + File.separator + "query");
    simpleSnippet = LogLoader.getLogLoader().loadLogFile("q4.log").get(0);
    simpleLog = LogLoader.getLogLoader().loadLogFile("new.log").get(0);
    
//    long t = System.currentTimeMillis();
//    Debug.log("Indexing...");
//    SearchEngine.getSearchEngine().indexLogs(logs, nGramLength);
//    Debug.log("Done indexing in " + (System.currentTimeMillis() - t) + "ms");
  }

  //@Test
  public void testIt() {
    Debug.log("Loaded logs, there are " + SearchEngine.getSearchEngine().getLogSnippets().size() + " snippets");
    printLog();
    printSpace();
    printQuery();
    
    for (int i = 0; i < queries.size(); i++) {
      ResultSet rs = SearchEngine.getSearchEngine().findMatches(queries.get(i)).getCloseMatches();
      printSpace();
      FilteredResultSet frs = rs.filter(new FilterSpec(0.0, 0.3, -1));
      System.err.println("Filtered results for " + i);
      printResults(frs);      
    }
    
    assertTrue(true);
  }

  //@Test
  public void testComputeEditDistance() {
//    int dist = SearchEngine.getSearchEngine().computeEditDistance(simpleSnippet, simpleSnippet, 0, simpleSnippet.getEvents().size());
//    Debug.log("Self-similar distance is " + dist);
//    assertTrue(dist == 0);
//    
//    dist = SearchEngine.getSearchEngine().computeEditDistance(simpleSnippet, simpleSnippet, 1, simpleSnippet.getEvents().size());
//    Debug.log("Self-similar distance is " + dist);
//    assertTrue(dist == 1);
    
    int dist = SearchEngine.getSearchEngine().computeEditDistance(simpleSnippet, simpleLog, 3, 3 + simpleSnippet.getEvents().size());
    Debug.log("Self-similar distance is " + dist);
    assertTrue(dist == 0);
    
  }
  
  @Test
  public void testBasicSearch() {
    SearchEngine se = SearchEngine.getSearchEngine();
    List<Snippet> snips = new ArrayList<Snippet>();
    snips.add(simpleSnippet);
    se.indexLogs(snips, nGramLength);

    ResultSet rs = se.findMatches(simpleSnippet).getCloseMatches();
    Debug.log("found " + rs.getResults().size() + " results");
    for (SubSequence subS : rs.getResults()) {
      System.out.println(subS.toString());
    }
  }
  
  public void printSpace() {
    for (int i = 0; i < 6; i++) {
      System.err.println("--------");
    }
  }
  
  public void printLog() {
    List<Snippet> snippets =  SearchEngine.getSearchEngine().getLogSnippets();
    int i = 0;
    for (Snippet s : snippets) {
      for (Event e : s.getEvents()) {
        System.err.println(i++ + "-" + e.getTimeStamp() + "-" + e.getIdentifier());
      }
    }
  }
  
  public void printQuery() {
    int i = 0;
    for (Snippet s : queries) {
      printSpace();
      int j = 0;
      for (Event e : s.getEvents()) {
        System.err.println(i + ":" + j++ + "-" + e.getTimeStamp() + "-" + e.getIdentifier() + "-" + e.getDisplayExtra());
      }
      i++;
    }
  }  
  
  public void printResults(ResultSet rs) {
    for (int i = 0; i < Math.min(10, rs.getResults().size() - 1); i++) {
      SubSequence subS = rs.getResults().get(i);
      System.err.println("result " + i + ": " + subS.getStartIndex() + "-" + 
          subS.getEndIndex() + " dist = " + subS.getDistance());
    }
  }
}
