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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TODO: Insert description here. (generated by marknewman)
 */
public class ArrowPanel extends JPanel {

  private ImageIcon arrowIcon = null;
      
  public ArrowPanel() {
    arrowIcon = ResourceManager.getResourceManager().getArrowImageIcon();
    JLabel arrowLabel = new JLabel(arrowIcon);
    setBackground(Color.white);
    setOpaque(true);
    setPreferredSize(new Dimension(arrowIcon.getIconWidth(), 100));
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(arrowLabel);
  }
  
}
