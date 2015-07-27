package com.google.research.ic.ferret.data.attributes;

import com.google.research.ic.ferret.data.ResultSet;
import com.google.research.ic.ferret.data.Snippet;
import com.google.research.ic.ferret.data.SubSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Insert description here. (generated by marknewman)
 */
public class UserNameAttributeHandler implements AttributeHandler {

  public static final String KEY = "username";
  public static final String DISPLAY_NAME = "User Name";
  
  @Override
  public void updateAttribute(Snippet s) {
    if (s.getAttribute(KEY) == null) {
      s.setAttribute(new CategoricalAttribute(KEY, s.getUserName()));
    }
  }
  
  @Override
  public String getKeyDisplayString(Attribute attribute) {
    return DISPLAY_NAME;
  }

  @Override
  public String getValueDisplayString(Attribute attribute) {
    return attribute.getValue().toString();
  }
  
  @Override
  public String getKey() {
    return KEY;
  }
  
  @Override
  public List<Bin> computeSummary(ResultSet rs) {
    List<Bin> binList = new ArrayList<Bin>();
    List<SubSequence> subSequences = rs.getResults();
    
    Map<String, Integer> userNameCounters = new HashMap<String, Integer>();
    
    for (SubSequence s : subSequences) {
      String uName = (String) s.getSnippet().getAttribute(KEY).getValue();
      Integer i = userNameCounters.get(uName);
      if (i == null) {
        i = Integer.valueOf(0);
      }
      i++;
      userNameCounters.put(uName, i);
    }
    
    for (String s : userNameCounters.keySet()) {
      binList.add(new Bin(s, s, s, CategoricalAttribute.TYPE, 
          userNameCounters.get(s).intValue()));
    }
    return binList;
  }
}