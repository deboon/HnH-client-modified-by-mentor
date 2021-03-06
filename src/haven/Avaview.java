/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Bj�rn Johannessen <johannessen.bjorn@gmail.com>
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

public class Avaview extends Widget {
	static final String POSKEY = "pava_pos";
    public static final Coord dasz = new Coord(74, 74);
    private Coord asz;
    int avagob;
    boolean none = false;
    AvaRender myown = null;
    public Color color = Color.WHITE;
    public static final Coord unborder = new Coord(2, 2);
    public static final Tex missing = Resource.loadtex("gfx/hud/equip/missing");
	
	boolean dm = false;
	Coord doff;
	
    static {
	Widget.addtype("av", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
			if(UI.instance.mainview != null && UI.instance.mainview.playergob == (Integer)args[0]){
				c = new Coord(Config.window_props.getProperty(POSKEY,c.toString()));
			}
		    return(new Avaview(c, parent, (Integer)args[0]));
		}
	    });
	Widget.addtype("av2", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
		    List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
		    for(Object arg : args)
			rl.add(parent.ui.sess.getres((Integer)arg));
		    return(new Avaview(c, parent, rl));
		}
	    });
    }
	
    private Avaview(Coord c, Widget parent, Coord asz) {
	super(c, asz.add(Window.wbox.bisz()).add(unborder.mul(2).inv()), parent);
	this.asz = asz;
    }
        
    public Avaview(Coord c, Widget parent, int avagob, Coord asz) {
	this(c, parent, asz);
	this.avagob = avagob;
    }
	
    public Avaview(Coord c, Widget parent, int avagob) {
	this(c, parent, avagob, dasz);
    }
        
    public Avaview(Coord c, Widget parent, List<Indir<Resource>> rl) {
	this(c, parent, dasz);
	if(rl.size() == 0)
	    none = true;
	else
	    this.myown = new AvaRender(rl);
    }
	
    public void uimsg(String msg, Object... args) {
	if(msg == "upd") {
	    this.avagob = (Integer)args[0];
	    return;
	}
	if(msg == "ch") {
	    List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
	    for(Object arg : args)
		rl.add(ui.sess.getres((Integer)arg));
	    if(rl.size() == 0) {
		this.myown = null;
		none = true;
	    } else {
		if(myown != null)
		    myown.setlay(rl);
		else
		    myown = new AvaRender(rl);
		none = false;
	    }
	    return;
	}
	super.uimsg(msg, args);
    }
        
    public void draw(GOut g) {
	Tex at = null;
	if(none) {
	} else if(myown != null) {
	    at = myown;
	} else {
	    Gob gob = ui.sess.glob.oc.getgob(avagob);
	    Avatar ava = null;
	    if(gob != null)
		ava = gob.getattr(Avatar.class);
	    if(ava != null)
		at = ava.rend;
	}
	GOut g2 = g.reclip(Window.wbox.tloff().add(unborder.inv()), asz);
	int yo;
	if(at == null) {
	    at = missing;
	    yo = 0;
	} else {
	    g2.image(Equipory.bg, new Coord(Equipory.bg.sz().x / 2 - asz.x / 2, 20).inv());
	    yo = (20 * asz.y) / dasz.y;
	}
	Coord tsz = new Coord((at.sz().x * asz.x) / dasz.x, (at.sz().y * asz.y) / dasz.y);
	g2.image(at, new Coord(tsz.x / 2 - asz.x / 2, yo).inv(), tsz);
	g.chcolor(color);
	Window.wbox.draw(g, Coord.z, asz.add(Window.wbox.bisz()).add(unborder.mul(2).inv()));
    }
	
    public boolean mousedown(Coord c, int button) {
		switch(button){
		case 1: System.out.printf("PARENT %s",parent);
				ui.grabmouse(this);
				dm = true;
				doff = c;
				wdgmsg("click", button); break;
		case 3: 
		    if(parent instanceof Partyview || parent instanceof Fightview){
			wdgmsg("click",button);
		    }else if(avagob > 0 && ui.sess.glob.oc.getgob(avagob) != null && ui.sess.glob.oc.getgob(avagob).getattr(Avatar.class) != null){
			new XAvaGear(this.c.add(c),ui.root,avagob);
		    } else if(myown != null){
			new XAvaGear(this.c.add(c),ui.root,myown);
		    }
		}
		return(true);
    }
	
	public boolean mouseup(Coord c,int button){
		if(dm){
			ui.grabmouse(null);
			dm = false;
			if(ui.mainview != null && avagob == ui.mainview.playergob){
				Config.setWindowOpt(POSKEY,this.c.toString());
			} else if(parent instanceof Partyview) {
				((Partyview)parent).saveparty();
			}
		} else {
			super.mouseup(c,button);
		}
		return(true);
	}
	
    public void mousemove(Coord c) {
		if(dm && !Config.global_ui_lock) {
			if(ui.mainview != null && avagob == ui.mainview.playergob){
				this.c = this.c.add(c.add(doff.inv()));
			} else if(parent instanceof Partyview)  {
				((Partyview)parent).moveparty(c.add(doff.inv()));
			} 
		} else {
		    super.mousemove(c);
		}
    }
}
