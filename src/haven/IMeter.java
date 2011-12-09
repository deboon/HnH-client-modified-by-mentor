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

import java.awt.Color;
import java.util.*;

public class IMeter extends Widget {
    static Coord off = new Coord(13, 7);
    static Coord fsz = new Coord(63, 18);
    static Coord msz = new Coord(49, 4);
    Resource bg;
    List<Meter> meters;
    String bgname;
	
	boolean dm;
	Coord doff;
	
    static {
	Widget.addtype("im", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
			String bgname = (String)args[0];
		    Resource bg = Resource.load(bgname);
		    List<Meter> meters = new LinkedList<Meter>();
		    for(int i = 1; i < args.length; i += 2)
			meters.add(new Meter((Color)args[i], (Integer)args[i + 1]));
			c = new Coord(Config.window_props.getProperty(bgname,c.toString()));
		    return(new IMeter(c, parent, bg,bgname, meters));
		}
	    });
    }
    
    public IMeter(Coord c, Widget parent, Resource bg,String bgname, List<Meter> meters) {
	super(c, fsz, parent);
	this.bg = bg;
	this.bgname = bgname;
	this.meters = meters;
    }
    
    public static class Meter {
	Color c;
	int a;
	
	public Meter(Color c, int a) {
	    this.c = c;
	    this.a = a;
	}
    }
    
    public void draw(GOut g) {
	if(!bg.loading) {
	    Tex bg = this.bg.layer(Resource.imgc).tex();
	    g.chcolor(0, 0, 0, 255);
	    g.frect(off, msz);
	    g.chcolor();
	    for(Meter m : meters) {
		int w = msz.x;
		w = (w * m.a) / 100;
		g.chcolor(m.c);
		g.frect(off, new Coord(w, msz.y));
	    }
	    g.chcolor();
	    g.image(bg, Coord.z);
	}
    }
    
    public void uimsg(String msg, Object... args) {
	if(msg == "set") {
	    List<Meter> meters = new LinkedList<Meter>();
	    for(int i = 0; i < args.length; i += 2)
		meters.add(new Meter((Color)args[i], (Integer)args[i + 1]));
	    this.meters = meters;
	    if (bgname.equals("gfx/hud/meter/nrj")) {
            	Ment.aw.Stamina = meters.get(0).a;
            	Ment.aw.send("Stamina|" + Ment.aw.Stamina);
            }
	} else if(msg == "tt") {
	    tooltip = args[0];
	    if (bgname.equals("gfx/hud/meter/hp") && (args[0].toString().indexOf("Health:")>=0)) {
                String s = args[0].toString();
                String [] temp = null;
                s = s.replaceAll("Health: ", "");
                temp = s.split("/");
                if (temp != null) {
                    Ment.aw.HP = Integer.parseInt(temp[0]);
                    Ment.aw.send("HP|"+Ment.aw.HP);
                }
            }
            if (bgname.equals("gfx/hud/meter/hngr")) {
                String s = (String)args[0];
                String r = "";
                for (int j = s.indexOf('(')+1; j < s.length(); j++) {
                    if (s.charAt(j) == '%')
                        break;
                    r += s.charAt(j);
                }
                if (r.length() > 0) {
                    Ment.aw.Hunger = Integer.parseInt(r);
                    Ment.aw.send("Hunger|" + Ment.aw.Hunger);
                }
            }
	} else {
	    super.uimsg(msg, args);
	}
    }
	
	public boolean mousedown(Coord c, int button) {
		super.mousedown(c,button);
		parent.setfocus(this);
		raise();
		if (button == 1) {
			ui.grabmouse(this);
			doff = c;
			dm = true;
		}
		return (true);
	}

	public boolean mouseup(Coord c, int button) {
		if (dm) {
			ui.grabmouse(null);
			dm = false;
			Config.setWindowOpt(bgname, this.c.toString());
		} else {
			super.mouseup(c, button);
		}
		return (true);
	}

	public void mousemove(Coord c) {
		if (dm && !Config.global_ui_lock) {
			this.c = this.c.add(c.add(doff.inv()));
		} else {
			super.mousemove(c);
		}
	}
}
