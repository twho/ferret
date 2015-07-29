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
package com.google.research.ic.ferret;

import com.google.research.ic.ferret.comm.DeviceEventReceiver;
import com.google.research.ic.ferret.data.Event;
import com.google.research.ic.ferret.data.LogLoader;
import com.google.research.ic.ferret.data.ResultSet;
import com.google.research.ic.ferret.data.Snippet;
import com.google.research.ic.ferret.data.attributes.UserNameAttributeHandler;
import com.google.research.ic.ferret.test.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Insert description here. (generated by marknewman)
 */
public class Session {
  
  private static Session currentSession = null;
  private List<String> attrKeysToShow = new ArrayList<String>();
  private List<SessionListener> listeners = new ArrayList<SessionListener>();
  private Snippet currentQuery = null;
  private List<Event> demoEventQueue = null;
  private ResultSet currentResults = null;  
  private boolean recording = true;
  
  private Object queryLock = new Object();
  private boolean inited = false;
  private boolean deviceMode = true;
  
  /**
   * private for Singleton pattern. Use getCurrentSession().
   */
  private Session () { }

  
  /**
   * Sets up default attributes to show
   */
  public void init() {
    if (inited) { // only init once
      return;
    }
    attrKeysToShow.add(UserNameAttributeHandler.KEY);
    DemoEventListener deListener = new DemoEventListener() {

      @Override
      public void onEventReceived(Event event) {
        boolean doNotify = false;
        //Debug.log("Event received: " + event);
        if (recording) {
          synchronized(queryLock) {
            if (currentQuery == null) {
              resetCurrentQuery();
              doNotify = true;
            }
            currentQuery.addEvent(event);
            Debug.log("Adding event before compress " + event);
            if (!LogLoader.getLogLoader().getParser().compressSnippet(currentQuery, false)) {
              Debug.log("Adding event " + event);
              demoEventQueue.add(event);    
              doNotify = true;
            }
          }
          if (doNotify) {
            notifyListeners(); 
          }
        }// if (!recording) ignore
      }
    };
    
    if (deviceMode) {
      DeviceEventReceiver.getReceiver().addDemoEventListener(deListener);
    }
    inited = true;

  }

  public void setDeviceMode(boolean b) {
    deviceMode = b;
  }
  
  public boolean getDeviceMode() {
    return deviceMode;
  }
  
  public void setRecordingMode(boolean newMode) {
    if (newMode != recording) {
      recording = newMode;
      if (recording == true) {
        resetCurrentQuery();
      } else {
        // recording was turned off
      }
    }
  }

  public Snippet getCurrentQuery() {
    return currentQuery;
  }
  
  public void setCurrentQuery(Snippet s) {    
    currentQuery = s;
  }

  public void resetCurrentQuery() {
    synchronized(queryLock) {
      currentQuery = new Snippet();
      demoEventQueue = new ArrayList<Event>();
    }
  }
  
  public ResultSet getCurrentResultSet() {
    return currentResults;
  }
  
  public void setCurrentResultSet(ResultSet rs) {
    this.currentResults = rs;
  }
  
  public static Session getCurrentSession() {
    if (currentSession == null) {
      currentSession = new Session();
    }
    return currentSession;
  }

  public void setPropertiesToShow(List<String> attrs) {
    attrKeysToShow = attrs;
    notifyListeners();
  }

  public List<String> getAttributesToShow() {
    return attrKeysToShow;
  }

  
  public void addListener(SessionListener l) {
    synchronized (listeners) {
      if (!listeners.contains(l)) {
        listeners.add(l);        
      }
    }
  }
  
  public void removeListener(SessionListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }
  
  public void notifyListeners() {
    synchronized (listeners) {
      for (SessionListener l : listeners) {
        l.sessionUpdated(currentSession);
      }      
    }
  }
  
  public List<Event> dequeueDemoEvents() {
    synchronized (queryLock) {
      if (!demoEventQueue.isEmpty()) {
        List<Event> evts = new ArrayList<Event>();
        for(Event e : demoEventQueue) {
          evts.add(e);
        }
        demoEventQueue.clear();
        return evts;
      }
      return null;
    }
  }  
}