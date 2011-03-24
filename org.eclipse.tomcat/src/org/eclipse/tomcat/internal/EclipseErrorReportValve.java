/* ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portion of this source code are:
 *
 *     Copyright (c) 2003, 2004 IBM Corp.
 *     All rights reserved.
 *
 * Contributors:
 *     Remy Maucherat
 *     Craig R. McClanahan
 *     <a href="mailto:nicolaken@supereva.it">Nicola Ken Barozzi</a> Aisa
 *     <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 *     Konrad Kolosowski, IBM - friendly 404 message, no exception printed
 */


package org.eclipse.tomcat.internal;


import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.catalina.*;
import org.apache.catalina.util.*;
import org.apache.catalina.valves.*;
import org.eclipse.core.runtime.Platform;


/**
 * <p>Implementation of a Valve that outputs HTML error pages.</p>
 *
 * <p>This Valve should be attached at the Host level, although it will work
 * if attached to a Context.</p>
 * 
 * <p>HTML code from the Cocoon 2 project.</p>
 */

public class EclipseErrorReportValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The debugging detail level for this component.
     */
    private int debug = 0;


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
	"org.eclipse.tomcat.internal.EclipseErrorReportValve"; //$NON-NLS-1$


    /**
     * The StringManager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Invoke the next Valve in the sequence. When the invoke returns, check 
     * the response state, and output an error report is necessary.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void invoke(Request request, Response response,
                       ValveContext context)
        throws IOException, ServletException {

        // Perform the request
        context.invokeNext(request, response);

        ServletRequest sreq = (ServletRequest) request;
        Throwable throwable = 
            (Throwable) sreq.getAttribute(Globals.EXCEPTION_ATTR);

        ServletResponse sresp = (ServletResponse) response;
        if (sresp.isCommitted()) {
            return;
        }

        if (throwable != null) {

            // The response is an error
            response.setError();

            // Reset the response (if possible)
            try {
                sresp.reset();
            } catch (IllegalStateException e) {
                ;
            }

            ServletResponse sresponse = (ServletResponse) response;
            if (sresponse instanceof HttpServletResponse)
                ((HttpServletResponse) sresponse).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }

        response.setSuspended(false);

        try {
            report(request, response, throwable);
        } catch (Throwable tt) {
            // tt.printStackTrace();
        }

    }


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("EclipseErrorReportValve["); //$NON-NLS-1$
        sb.append(container.getName());
        sb.append("]"); //$NON-NLS-1$
        return (sb.toString());

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Prints out an error report.
     * 
     * @param request The request being processed
     * @param response The response being generated
     * @param exception The exception that occurred (which possibly wraps
     *  a root cause exception
     */
    protected void report(Request request, Response response,
                          Throwable throwable)
        throws IOException {

        // Do nothing on non-HTTP responses
        if (!(response instanceof HttpResponse))
            return;
        HttpResponse hresponse = (HttpResponse) response;
        if (!(response instanceof HttpServletResponse))
            return;
        HttpServletResponse hres = (HttpServletResponse) response;
        int statusCode = hresponse.getStatus();
        String message = RequestUtil.filter(hresponse.getMessage());
        if (message == null)
            message = ""; //$NON-NLS-1$

        // Do nothing on a 1xx, 2xx and 3xx status
        if (statusCode < 400)
            return;

        // FIXME: Reset part of the request
/*
        try {
            if (hresponse.isError())
                hresponse.reset(statusCode, message);
        } catch (IllegalStateException e) {
            ;
        }
*/

        Throwable rootCause = null;

        if (throwable != null) {

            if (throwable instanceof ServletException)
                rootCause = ((ServletException) throwable).getRootCause();

        }

        // Do nothing if there is no report for the specified status code
        String report = null;
        try {
            report = sm.getString("http." + statusCode, message); //$NON-NLS-1$
        } catch (Throwable t) {
            ;
        }
        if (report == null)
            return;

        StringBuffer sb = new StringBuffer();

        sb.append("<html><head><title>"); //$NON-NLS-1$
        sb.append(sm.getString("errorReportValve.errorReport")); //$NON-NLS-1$
        sb.append("</title>"); //$NON-NLS-1$
		if (statusCode == 404) {
			sb.append("</head><body>"); //$NON-NLS-1$
			sb.append(TomcatResources.noDocument);
		} else {
        sb.append("<STYLE><!--"); //$NON-NLS-1$
        sb.append("H1{font-family : sans-serif,Arial,Tahoma;color : white;background-color : black;} "); //$NON-NLS-1$
        sb.append("H3{font-family : sans-serif,Arial,Tahoma;color : white;background-color : black;} "); //$NON-NLS-1$
        sb.append("BODY{font-family : sans-serif,Arial,Tahoma;color : black;background-color : white;} "); //$NON-NLS-1$
        sb.append("B{color : white;background-color : black;} "); //$NON-NLS-1$
        sb.append("HR{color : black;} "); //$NON-NLS-1$
        sb.append("--></STYLE> "); //$NON-NLS-1$
        sb.append("</head><body>"); //$NON-NLS-1$
        sb.append("<h1>"); //$NON-NLS-1$
        sb.append(sm.getString("errorReportValve.statusHeader", //$NON-NLS-1$
                               "" + statusCode, message)).append("</h1>"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("<HR size=\"1\" noshade>"); //$NON-NLS-1$
        sb.append("<p><b>type</b> "); //$NON-NLS-1$
        if (throwable != null) {
            sb.append(sm.getString("errorReportValve.exceptionReport")); //$NON-NLS-1$
        } else {
            sb.append(sm.getString("errorReportValve.statusReport")); //$NON-NLS-1$
        }
        sb.append("</p>"); //$NON-NLS-1$
        sb.append("<p><b>"); //$NON-NLS-1$
        sb.append(sm.getString("errorReportValve.message")); //$NON-NLS-1$
        sb.append("</b> <u>"); //$NON-NLS-1$
        sb.append(message).append("</u></p>"); //$NON-NLS-1$
        sb.append("<p><b>"); //$NON-NLS-1$
        sb.append(sm.getString("errorReportValve.description")); //$NON-NLS-1$
        sb.append("</b> <u>"); //$NON-NLS-1$
        sb.append(report);
        sb.append("</u></p>"); //$NON-NLS-1$

        if (throwable != null) {
			boolean selfHostingMode = false;
			String[] args = Platform.getCommandLineArgs();
			for (int i = 0; i < args.length; i++) {
				if ("-pdelaunch".equals(args[i])) { //$NON-NLS-1$
					selfHostingMode = true;
					break;
				}
			}
			if (selfHostingMode) {
            StringWriter stackTrace = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTrace));
            sb.append("<p><b>"); //$NON-NLS-1$
            sb.append(sm.getString("errorReportValve.exception")); //$NON-NLS-1$
            sb.append("</b> <pre>"); //$NON-NLS-1$
            sb.append(RequestUtil.filter(stackTrace.toString()));
            sb.append("</pre></p>"); //$NON-NLS-1$
            if (rootCause != null) {
                stackTrace = new StringWriter();
                rootCause.printStackTrace(new PrintWriter(stackTrace));
                sb.append("<p><b>"); //$NON-NLS-1$
                sb.append(sm.getString("errorReportValve.rootCause")); //$NON-NLS-1$
                sb.append("</b> <pre>"); //$NON-NLS-1$
                sb.append(RequestUtil.filter(stackTrace.toString()));
                sb.append("</pre></p>"); //$NON-NLS-1$
            }
			}
        }

		}
        sb.append("</body></html>"); //$NON-NLS-1$

        try {

            Writer writer = response.getReporter();

            if (writer != null) {

                Locale locale = Locale.getDefault();

                try {
                    hres.setContentType("text/html"); //$NON-NLS-1$
                    hres.setLocale(locale);
                } catch (Throwable t) {
                    if (debug >= 1)
                        log("status.setContentType", t); //$NON-NLS-1$
                }

                // If writer is null, it's an indication that the response has
                // been hard committed already, which should never happen
                writer.write(sb.toString());
                writer.flush();

            }

        } catch (IOException e) {
            ;
        } catch (IllegalStateException e) {
            ;
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     */
    protected void log(String message) {

        Logger logger = container.getLogger();
        if (logger != null)
            logger.log(this.toString() + ": " + message); //$NON-NLS-1$
        else
            System.out.println(this.toString() + ": " + message); //$NON-NLS-1$

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    protected void log(String message, Throwable throwable) {

        Logger logger = container.getLogger();
        if (logger != null)
            logger.log(this.toString() + ": " + message, throwable); //$NON-NLS-1$
        else {
            System.out.println(this.toString() + ": " + message); //$NON-NLS-1$
            throwable.printStackTrace(System.out);
        }

    }


}
