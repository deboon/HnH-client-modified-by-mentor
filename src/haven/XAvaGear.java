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

/* Imports */


public class XAvaGear extends Widget
{
    /* Variables */
    int avagob = -1;
	AvaRender avar;
    Tex gear = null;
	
	boolean dm;
	Coord doff;
	IButton cbtn;
	Coord cy = new Coord(0,15);
	
    /* Constructors */
    public XAvaGear(Coord c,Widget parent,int gob) {
		super(c,Coord.z,parent);
		avagob = gob;
		avar = ui.sess.glob.oc.getgob(avagob).getattr(Avatar.class).rend;
		gear = avar;
		sz = gear.sz();
		cbtn = new IButton(Coord.z,this,Window.cbtni[0],Window.cbtni[1],Window.cbtni[2]);
		//cbtn.c.x = sz.x/2-10;
    }
	
	public XAvaGear(Coord c,Widget parent,AvaRender at){
		super(c,at.sz(),parent);
		avar = at;
		gear = at;
		cbtn = new IButton(Coord.z,this,Window.cbtni[0],Window.cbtni[1],Window.cbtni[2]);
		//cbtn.c.x = sz.x/2-10;
	}

    public void draw(GOut g){
		cy.x = 0; cy.y = 15;
		g.chcolor(0,0,0,128);
		g.frect(Coord.z,sz);
		g.chcolor();
		synchronized(gear){
			g.image(gear,Coord.z);
		}
		synchronized(avar){
			for(Text t : avar.lnames){
				g.image(t.tex(),cy);
				cy.y+=t.tex().sz().y+1;
				if(cy.y+t.tex().sz().y> sz.y){
					cy.y = 0;
					cy.x = sz.x/2+5;
				}
			}
		}
		super.draw(g);
    }

    public boolean mousedown(Coord c,int button){
		parent.setfocus(this);
		raise();
		if(super.mousedown(c,button))
			return (true);
		if(button == 1){
			ui.grabmouse(this);
			dm = true;
			doff = c;
			return(true);
		} else if(button == 3){
			ui.destroy(this);
			return(true);
		}
		return(false);
    }
	
	public boolean mouseup(Coord c,int button){
		if(dm){
			ui.grabmouse(null);
			dm = false;
		}
		return(true);
	}

	public void mousemove(Coord c){
		if(dm){
			this.c = this.c.add(c.add(doff.inv()));
		} else
			super.mousemove(c);
	}
	
	public void wdgmsg(Widget sender, String msg, Object... args) {
		if(cbtn != null && sender == cbtn) {
		    ui.destroy(this);
		} else {
		    super.wdgmsg(sender, msg, args);
		}
    }
}