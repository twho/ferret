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

import com.google.research.ic.ferret.data.ResultSet;
import com.google.research.ic.ferret.data.SearchEngine;
import com.google.research.ic.ferret.data.Snippet;
import com.google.research.ic.ferret.data.SubSequence;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Insert description here. (generated by marknewman)
 */
public class SearchEngineTest {

  Snippet addButterSnippet = null;
  Snippet addKaleSnippet = null;
  Snippet addKaleHummusSnippet = null;
  Snippet addAllItemsSnippet = null;
  Snippet logSnippet1 = null;
  
  private static final String ADD_ITEM_BUTTER_FILENAME = "AddItem-Butter.json";
  private static final String ADD_ITEM_KALE_FILENAME = "AddItem-Kale.json";
  private static final String ADD_ITEMS_KALEHUMMUS_FILENAME = "AddItems-Kale+Hummus.json";
  private static final String ADD_ITEMS_ALL_FILENAME = "AddItems-All.json";
  private static final String LOG_SNIPPET1_FILENAME = "ALoggerLog1.json";
  
  private static final String EVENT_TEST_DIR = "testdata/events";
  private static final String SNIPPET_TEST_DIR = "testdata/snippets";
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
        
    addButterSnippet = TestALogLoader.createSnippetFromFile(SNIPPET_TEST_DIR, 
        ADD_ITEM_BUTTER_FILENAME);
    assert(addButterSnippet != null);
    addKaleSnippet = TestALogLoader.createSnippetFromFile(SNIPPET_TEST_DIR, 
        ADD_ITEM_KALE_FILENAME);
    assert(addKaleSnippet != null);
    addKaleHummusSnippet = TestALogLoader.createSnippetFromFile(SNIPPET_TEST_DIR, 
        ADD_ITEMS_KALEHUMMUS_FILENAME);
    assert(addKaleHummusSnippet != null);
    addAllItemsSnippet = TestALogLoader.createSnippetFromFile(SNIPPET_TEST_DIR, 
        ADD_ITEMS_ALL_FILENAME);
    assert(addAllItemsSnippet != null);
    logSnippet1 = TestALogLoader.createSnippetFromFile(SNIPPET_TEST_DIR, 
        LOG_SNIPPET1_FILENAME);
    assert(logSnippet1 != null);    
  }
  
  @Test
  public void testSelfMatch() {
    SearchEngine engine = SearchEngine.getSearchEngine();
    ArrayList<Snippet> snippets = new ArrayList<Snippet>();
        
    Debug.log("query sequence has " + addButterSnippet.getEvents().size() +
        " events");
    snippets.add(addButterSnippet);
    engine.indexSubSequences(snippets, true);    
    ResultSet results = engine.searchMatches(addButterSnippet).getCloseMatches();
    List<SubSequence> subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);
    
    //for (SubSequence s : subSequences) {
    //  Debug.log("Subsequence (" + s.getStartIndex() + "," + 
    //      s.getEndIndex() + ") dist: " + s.getDistance());
    //}
    
    Debug.log("query sequence has " + addKaleHummusSnippet.getEvents().size() +
        " events");
    snippets.clear();
    snippets.add(addKaleHummusSnippet);
    engine.indexSubSequences(snippets, true); // reset the engine's indexes
    results = engine.searchMatches(addKaleHummusSnippet).getCloseMatches();
    subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);

    Debug.log("query sequence has " + addAllItemsSnippet.getEvents().size() +
        " events");
    snippets.clear();
    snippets.add(addAllItemsSnippet);
    engine.indexSubSequences(snippets, true); // reset the engine's indexes
    results = engine.searchMatches(addAllItemsSnippet).getCloseMatches();
    subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);    
  }

  @Test 
  public void testSearchLogs1() {
    SearchEngine engine = SearchEngine.getSearchEngine();
    List<Snippet> snippets = new ArrayList<Snippet>();
    List<SubSequence> subSequences = null;
    ResultSet results = null;

    Debug.log("query sequence has " + addKaleSnippet.getEvents().size() +
        " events");
    Debug.log("log has " + addKaleHummusSnippet.getEvents().size() +
        " events");    
    snippets.add(addKaleHummusSnippet);
    engine.indexSubSequences(snippets, true); // reset the engine's indexes
    results = engine.searchMatches(addKaleSnippet).getCloseMatches();
    subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);
    
    Debug.log("query sequence has " + addKaleSnippet.getEvents().size() +
        " events");
    Debug.log("log has " + addKaleHummusSnippet.getEvents().size() +
        " events");    
    snippets.add(addAllItemsSnippet);
    engine.indexSubSequences(snippets, true); // reset the engine's indexes
    results = engine.searchMatches(addKaleSnippet).getCloseMatches();
    subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);

    results = engine.searchMatches(addKaleHummusSnippet).getCloseMatches();
    subSequences = results.getResults();

    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    assert(subSequences.get(0).getDistance() == 0.0f);

    
    //Even though "add butter," "add kale," and "add hummus" are different, the
    //difference in text shouldn't matter
    snippets.clear();
    snippets.add(addKaleHummusSnippet);
    engine.indexSubSequences(snippets, true); // reset the engine's indexes   
    results = engine.searchMatches(addButterSnippet).getCloseMatches();
    
    subSequences = results.getResults();
    Debug.log("" + subSequences.size() + " subsequences were evaluated");
    Debug.log("" + subSequences.get(0).getDistance() + " was the smallest distance");
    assert(subSequences.get(0).getDistance() == 0.0d); 
    
    for (SubSequence s : subSequences) {
      Debug.log("Subsequence (" + s.getStartIndex() + "," + 
          s.getEndIndex() + ") dist: " + s.getDistance());
    }
  }
  
}
