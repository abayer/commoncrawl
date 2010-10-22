/*
NGramJ - n-gram based text classification
Copyright (C) 2001 Frank S. Nestel (frank at spieleck.de)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published 
by the Free Software Foundation; either version 2.1 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program (lesser.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package de.spieleck.app.ngramj.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import de.spieleck.app.ngramj.*;
import de.spieleck.app.ngramj.phoner.*;
import de.spieleck.app.ngramj.lm.LMDataProfile;

/**
 * Mini-Application to create a "useful" word from
 * a phonenumber.
 */
public class PhoneServlet
    extends HttpServlet
{
    protected String basePath = "WEB-INF/lm";
    protected String forcedReferer  = null;

    public PhoneServlet() { }

    public void init()
    {
        String h;
        h = getInitParameter("basePath");
        if ( h != null )
            basePath = h;
        basePath = getServletConfig()
                    .getServletContext()
                        .getRealPath(basePath)+"/";
        forcedReferer = getInitParameter("forcedReferer");
            
        System.err.println("bp="+basePath);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException
    {
        doGet(req,res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException
    {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        if ( forcedReferer != null )
        {
            String referer = req.getHeader("Referer");
            if ( !forcedReferer.equals(referer) )
            {
                out.println("Sorry! You came from "+referer+".");
                out.println("This page is costly in terms of CPU time.");
                out.println("You can only access via "+forcedReferer+".");
                return;
            }
        }
        String tel = req.getParameter("tel");
        if ( tel.length() >= 9 )
        {
            out.println("Sorry! Your number is too long.");
            out.println("The current implementation is costly in terms of CPU time.");
            return;
        }
        String lang = req.getParameter("lang");
        System.out.println("Using language resource "+lang+" on <"+tel+">("+tel.length()+").");
        try
        {
            File ifi = new File(basePath+lang);
            InputStream in = new FileInputStream(ifi);
            IterableProfile prof = new LMDataProfile(lang, in);
            //
            Phoner p = new Phoner(prof, tel);
            out.println("Using language resource "+lang);
            p.show(out);
        }
        catch ( Exception e )
        {
            out.println("Error: "+e);
        }
    }
}
