/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs.http;

import java.io.File;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.carrot2.dcs.AppBase;
import org.carrot2.dcs.Config;
import org.carrot2.dcs.ConfigConstants;
import org.carrot2.dcs.ControllerContext;
import org.carrot2.util.StringUtils;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * An in-process HTTP server exposing the clustering service (processing POSTed XMLs).
 * 
 * @author Dawid Weiss
 */
public final class DCSApp extends AppBase
{
    protected DCSApp()
    {
        super("dcs");
    }

    /**
     * Command line entry point (main).
     */
    public static void main(String [] args) throws Exception
    {
        new DCSApp().go(args);
    }

    /**
     * Command line entry point (after parsing arguments).
     */
    protected void go(CommandLine options)
    {
        final boolean verbose = options.hasOption("verbose");
        if (verbose)
        {
            logger.setLevel(Level.DEBUG);
        }

        // Initialize processes.
        final ControllerContext context;
        try
        {
            final File descriptors = (File) getOption(options, "algorithms", new File("algorithms"));
            context = initializeContext(descriptors);
        }
        catch (Exception e)
        {
            getLogger().fatal("Could not initialize clustering algorithms. Inspect log files.", e);
            return;
        }

        config.setDefaultValue(ConfigConstants.ATTR_DCS_LOGGER, logger);
        config.setDefaultValue(ConfigConstants.ATTR_CLUSTERS_ONLY, new Boolean(options.hasOption("co")));
        config.setDefaultValue(ConfigConstants.ATTR_DEFAULT_PROCESSID, options.getOptionValue("algorithm"));
        config.setDefaultValue(ConfigConstants.ATTR_CONTROLLER_CONTEXT, context);

        getLogger().info("Starting standalone DCS server.");
        try
        {
            final int port = ((Number) options.getOptionObject("port")).intValue();
            startJetty(port, config);
            getLogger().info("Accepting HTTP requests on port: " + port);
        }
        catch (Exception e)
        {
            getLogger().fatal("Could not start HTTP server: " + StringUtils.chainExceptionMessages(e));
            getLogger().debug("Could not start HTTP server (details)", e);
        }
    }

    /**
     * 
     */
    protected void initializeOptions(Options options)
    {
        final Option processName = new Option("algorithm", true,
            "Identifier of the default algorithm used for clustering.");
        processName.setArgName("identifier");
        processName.setRequired(true);
        processName.setType(String.class);
        options.addOption(processName);

        final Option descriptors = new Option("descriptors", true, "Descriptors folder (algorithms).");
        descriptors.setArgName("path");
        descriptors.setRequired(false);
        descriptors.setType(File.class);
        options.addOption(descriptors);

        final Option port = new Option("port", true, "Socket to bind to.");
        port.setArgName("number");
        port.setRequired(true);
        port.setType(Number.class);
        options.addOption(port);

        final Option clustersOnly = new Option("co", false, "Skips input documents in the response.");
        clustersOnly.setLongOpt("clusters-only");
        clustersOnly.setRequired(false);
        options.addOption(clustersOnly);

        final Option verbose = new Option("verbose", false, "Be more verbose.");
        options.addOption(verbose);
    }

    /**
     * Starts embedded JETTY server.
     */
    private void startJetty(final int port, final Config config) throws Exception
    {
        final Server server = new Server();
        server.setResolveRemoteHost(false);
        server.setStopGracefully(true);

        // Socket listener.
        final SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        final WebApplicationContext context = new WebApplicationContext(new File(".").getAbsolutePath());
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setContextPath("/");

        server.addContext(context);

        // Pass controller context as a global application attribute.
        context.setAttribute(InitializationServlet.ATTR_APPCONFIG, config);

        // Start the http server
        server.start();
    }
}
