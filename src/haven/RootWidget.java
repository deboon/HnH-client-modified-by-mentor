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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.media.opengl.GLException;

import com.sun.opengl.util.Screenshot;

public class RootWidget extends ConsoleHost {
    public static Resource defcurs = Resource.load("gfx/hud/curs/arw");
	public static Hashtable<Integer,IKeyFunction> skeys = new Hashtable<Integer,IKeyFunction>();
	public static Hashtable<Character,IKeyFunction> keys = new Hashtable<Character,IKeyFunction>();
	public static Hashtable<Character,IKeyFunction> akeys = new Hashtable<Character,IKeyFunction>();
	public static Hashtable<Integer,IKeyFunction> ckeys = new Hashtable<Integer,IKeyFunction>();
    Logout logout = null;
    Profile gprof;
    boolean afk = false;
    public static boolean screenshot = false;
    public static boolean names_ready = false;
	
	static{
	reloadkeys();
	}
	
	public static void reloadkeys(){
		skeys.clear();
		ckeys.clear();
		for(IKeyFunction kf: KeyFunction.funcs){
			switch(kf.getspec()){
			case KeyFunction.NORMAL: keys.put((char)kf.getkey(),kf);  break;
			case KeyFunction.SPECIAL:skeys.put(kf.getkey(),kf);		  break;
			case KeyFunction.ALT:	 akeys.put((char)kf.getkey(),kf); break;
			case KeyFunction.CTRL:	 ckeys.put(kf.getkey(),kf);		  break;
			}
		}
	}
	
    public RootWidget(UI ui, Coord sz) {
	super(ui, new Coord(0, 0), sz);
	setfocusctl(true);
	cursor = defcurs;
    }
	
    public boolean globtype(char key, KeyEvent ev) {
		if(!super.globtype(key, ev)) {
			if(ev.isAltDown() && akeys.get(key) != null)
				akeys.get(key).dokey(ui,ev);
			else if(keys.get(key) != null)
				keys.get(key).dokey(ui,ev);
			else if(key != 0)
				wdgmsg("gk",(int)key);
		}
		return(true);
    }
	
	public boolean keydown(KeyEvent ev){
		if(!super.keydown(ev)){
			if(MenuGrid.instance != null && (MenuGrid.instance.digitbar.checkKey((char)0,ev) || MenuGrid.instance.functionbar.checkKey((char)0,ev) || MenuGrid.instance.numpadbar.checkKey((char)0,ev)))
				return(true);
			if(ev.isControlDown() && ckeys.get(ev.getKeyCode()) != null)
				ckeys.get(ev.getKeyCode()).dokey(ui,ev);
			else if(skeys.get(ev.getKeyCode()) != null)
				skeys.get(ev.getKeyCode()).dokey(ui,ev);
		}
		return(true);
	}

    public void draw(GOut g) {
	if(screenshot&&Config.sshot_noui){visible = false;}
	super.draw(g);
	drawcmd(g, new Coord(20, 580));
	if(screenshot && (!Config.sshot_nonames || names_ready)){
	    visible = true;
	    screenshot = false;
	    try {
		Coord s = MainFrame.getInnerSize();
		String stamp = Utils.sessdate(System.currentTimeMillis());
		String ext = Config.sshot_compress?".jpg":".png";
		File f = new File("screenshots/SS_"+stamp+ext);
		f.mkdirs();
		Screenshot.writeToFile(f, s.x, s.y);
	    } catch (GLException e){e.printStackTrace();}
	    catch (IOException e){e.printStackTrace();}
	}
    }
    
    public void error(String msg) {
    }
}
