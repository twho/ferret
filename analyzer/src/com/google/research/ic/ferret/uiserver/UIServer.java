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
package com.google.research.ic.ferret.uiserver;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.research.ic.ferret.Config;
import com.google.research.ic.ferret.data.LogLoader;
import com.google.research.ic.ferret.data.SearchEngine;
import com.google.research.ic.ferret.data.Snippet;
import com.google.research.ic.ferret.data.attributes.AttributeManager;
import com.google.research.ic.ferret.data.attributes.DurationAttributeHandler;
import com.google.research.ic.ferret.data.attributes.UserNameAttributeHandler;
import com.google.research.ic.ferret.test.Debug;

public class UIServer {

  public static void startServer() throws Exception {

    Thread t = new Thread() {
      @Override
      public void run() { 
        try {
          Debug.log("1");          
          
          // Jetty setup
          ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
          context.setContextPath("/");

          FilterHolder filter = new FilterHolder();
          filter.setInitParameter("allowedOrigins", "*");
          filter.setInitParameter("allowedMethods", "POST,GET,OPTIONS,PUT,DELETE,HEAD");
          filter.setInitParameter("allowedHeaders", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
          filter.setInitParameter("preflightMaxAge", "728000");
          filter.setInitParameter("allowCredentials", "true");
          CrossOriginFilter corsFilter = new CrossOriginFilter();
          filter.setFilter(corsFilter);
          context.addFilter(filter, "/*", EnumSet.of(DispatcherType.REQUEST));

          Debug.log("2");          
          Server server = new Server(8080);
          server.setHandler(context);

          Debug.log("3");          
          ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
          jerseyServlet.setInitOrder(0);
          jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", RESTHandler.class.getCanonicalName());

          Debug.log("4");          

          server.start();
       
          Debug.log("5");
          
          //Ferret setup
          if (Config.loadLogs) {
            if (Config.indexLogs) {
              String logDirName = Config.logDir;
              File logDir = new File(logDirName);
              List<Snippet> snippets = null;
              if (logDir.exists() && logDir.isDirectory()) {
                snippets = LogLoader.getLogLoader().loadLogs(logDirName);            
              } else {
                Debug.log("ERROR! Invalid log directory: " + logDir);
              }
              if (snippets != null) {
                Debug.log("Starting to index " + logDirName);
                long t = System.currentTimeMillis();
                SearchEngine.getSearchEngine().indexLogs(snippets, Config.nGramSize);
                Debug.log("Finished indexing " + logDirName + " in " + (System.currentTimeMillis() - t) + " ms");
              } else {
                Debug.log("Nothing indexed in " + logDirName);
              }
            }
          }     
          //AttributeManager.getManager().addHandler(new UserNameAttributeHandler());
          AttributeManager.getManager().addHandler(new DurationAttributeHandler());
          server.join();

        } catch (Exception e) {
          Debug.log("Error starting uiserver: " + e);
        }
      }
    };
    t.start();


  }

  public static void main(String[] args) throws Exception {

    Config.parseArgs(args);
    startServer();

  }


}
