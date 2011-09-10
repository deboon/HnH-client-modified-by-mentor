/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.media.opengl.GL;

public class ILM extends TexRT {
	public final static BufferedImage ljusboll;
    final static int HF = -460100982;
    final static int FIRE = 1104478640;
    final static int TORCHPOST = 168393716;
    final static int CANDLETHINGY = -594851989;
	final static int SPECIAL = 0;
	
    OCache oc;
    TexI lbtex;
    Color amb;
	
    static {
	int sz = 200, min = 50;
	BufferedImage lb = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
	Graphics g = lb.createGraphics();
	for(int y = 0; y < sz; y++) {
	    for(int x = 0; x < sz; x++) {
		double dx = sz / 2 - x;
		double dy = sz / 2 - y;
		double d = Math.sqrt(dx * dx + dy * dy);
		int gs;
		if(d > sz / 2)
		    gs = 255;
		else if(d < min)
		    gs = 0;
		else
		    gs = (int)(((d - min) / ((sz / 2) - min)) * 255);
		gs /= 2;
		Color c = new Color(gs, gs, gs, 128 - gs);
		g.setColor(c);
		g.fillRect(x, y, 1, 1);
	    }
	}
	ljusboll = lb;
    }
	
    public ILM(Coord sz, OCache oc) {
	super(sz);
	this.oc = oc;
	amb = new Color(0, 0, 0, 0);
	lbtex = new TexI(ljusboll);
    }
	
    protected Color setenv(GL gl) {
	gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
	return(amb);
    }
	
    protected boolean subrend(GOut g) {
	if(Config.nightvision){
	    return false;
	}
	GL gl = g.gl;
	gl.glClearColor(255, 255, 255, 255);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	synchronized(oc) {
	    for(Gob gob : oc) {
		if(gob.sc == null) {
		    /* Might not have been set up by the MapView yet */
		    continue;
		}
		Lumin lum = gob.getattr(Lumin.class);
		switch(gob.resname().hashCode()){
		case HF: gob.setattr((lum = new Lumin(gob,new Coord(0,0),200,96))); break;
		case FIRE: gob.setattr((lum = new Lumin(gob,new Coord(0,0),200,192))); break;
		case TORCHPOST: gob.setattr((lum = new Lumin(gob,new Coord(0,-28),300,192))); break;
		case CANDLETHINGY: gob.setattr((lum = new Lumin(gob,new Coord(0,-28),400,192))); break;
		case SPECIAL:
		{
			Gob p = oc.getgob(UI.instance.mainview.playergob);
			if(gob.getneg() != null && p.getneg() != null && // default client seems like it needs this
			   gob.getneg().bc.x == p.getneg().bc.x &&
			   gob.getneg().bs.x == p.getneg().bs.x)
					gob.setattr((lum = new Lumin(gob,new Coord(0,-15),200,192))); break;
		}
		}
		if(lum == null)
		    continue;
		Coord sc = gob.sc.add(lum.off).add(-lum.sz, -lum.sz);
		g.image(lbtex, sc, new Coord(lum.sz * 2, lum.sz * 2));
	    }
	}
	g.chcolor();
	return(true);
    }
    
    protected byte[] initdata() {
	return(null);
    }
}
