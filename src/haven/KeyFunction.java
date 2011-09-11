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
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

public class KeyFunction implements IKeyFunction{
	static final int NORMAL 	= 0;
	static final int SPECIAL 	= 1;
	static final int ALT		= 2;
	static final int CTRL		= 3;

	static String MOVE_NORTH_SKEY 	= "MOVE_NORTH_KEY";
	static String MOVE_SOUTH_SKEY 	= "MOVE_SOUTH_KEY";
	static String MOVE_EAST_SKEY  	= "MOVE_EAST_KEY";
	static String MOVE_WEST_SKEY  	= "MOVE_WEST_KEY";
	static String PROFILE_MV_SKEY 	= "PROFILE_MV_KEY";
	static String PROFILE_GLOB_SKEY	= "PROFILE_GLOB_KEY";
	static String PROFILE_ILM_SKEY 	= "PROFILE_ILM_KEY";
	static String ENTERCMD_SKEY		= "ENTERCMD_KEY";
	static String OPEN_KBW_SKEY		= "OPEN_KEYBINDWND_KEY";
	static String OPEN_INV_SKEY		= "OPEN_INV_KEY";
	static String OPEN_EQUI_SKEY	= "OPEN_EQUI_KEY";
	static String OPEN_CHRW_SKEY	= "OPEN_CHRW_KEY";
	static String OPEN_OPT_SKEY		= "OPEN_OPT_KEY";
	static String OPEN_BUDDY_SKEY	= "OPEN_BUD_KEY";
	static String OPEN_LNDW_SKEY	= "OPEN_LNDW_KEY";
	static String OPEN_GCHAT_SKEY 	= "OPEN_GCHAT_KEY";
	static String TOGGLE_HUD_SKEY	= "TOGGLE_HUD_KEY";
	static String SCREEN_SHOT_SKEY	= "SCREEN_SHOT_SKEY";	//
	static String RESET_CAM_SKEY	= "RESET_CAM_SKEY";
	static String GRID_TOG_SKEY		= "GRID_TOG_SKEY";
	static String SPEED_3_SKEY		= "SPEED_3_SKEY";
	static String SPEED_2_SKEY		= "SPEED_2_SKEY";
	static String SPEED_1_SKEY		= "SPEED_1_SKEY";
	static String SPEED_0_SKEY		= "SPEED_0_SKEY";
	static String HIDE_TOG_SKEY		= "HIDE_TOG_SKEY";
	static String XRAY_TOG_SKEY		= "XRAY_TOG_SKEY";
	static String NV_TOG_SKEY		= "NV_TOG_SKEY";
	
	String skey;
	int key;
	int special;
	
	public static List<IKeyFunction> funcs = new ArrayList<IKeyFunction>();
	public static KeyFunction MOVE_NORTH = new KeyFunction(MOVE_NORTH_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.mainview.wdgmsg("click",Coord.fake,ui.sess.glob.oc.getgob(ui.mainview.playergob).getc().add(0,Config.tiles_per_click*ui.mainview.north*MapView.ONE_TILE),1,ui.modflags());
		}
	};
	public static KeyFunction MOVE_SOUTH = new KeyFunction(MOVE_SOUTH_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.mainview.wdgmsg("click",Coord.fake,ui.sess.glob.oc.getgob(ui.mainview.playergob).getc().add(0,-Config.tiles_per_click*ui.mainview.north*MapView.ONE_TILE),1,ui.modflags());
		}
	};
	public static KeyFunction MOVE_EAST = new KeyFunction(MOVE_EAST_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			ui.mainview.wdgmsg("click",Coord.fake,ui.sess.glob.oc.getgob(ui.mainview.playergob).getc().add(Config.tiles_per_click*ui.mainview.east*MapView.ONE_TILE,0),1,ui.modflags());
		}
	};
	public static KeyFunction MOVE_WEST = new KeyFunction(MOVE_WEST_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			ui.mainview.wdgmsg("click",Coord.fake,ui.sess.glob.oc.getgob(ui.mainview.playergob).getc().add(-Config.tiles_per_click*ui.mainview.east*MapView.ONE_TILE,0),1,ui.modflags());
		}
	};
	public static KeyFunction PROFILE_MV = new KeyFunction(PROFILE_MV_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			new Profwnd(ui.root.findchild(SlenHud.class), ui.root.findchild(MapView.class).prof, "MV prof");
		}
	};
	public static KeyFunction PROFILE_GLOB = new KeyFunction(PROFILE_GLOB_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			new Profwnd(ui.root.findchild(SlenHud.class),ui.root.gprof, "Glob prof");
		}
	};
	public static KeyFunction PROFILE_ILM = new KeyFunction(PROFILE_ILM_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			new Profwnd(ui.root.findchild(SlenHud.class),ui.root.findchild(MapView.class).mask.prof,"ILM prof");
		}
	};
	public static KeyFunction ENTER_CMD = new KeyFunction(ENTERCMD_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			ui.root.entercmd();
		}
	};
	public static KeyFunction OPEN_KEYBINDWND = new KeyFunction(OPEN_KBW_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			if(ui.kbw == null){
				ui.kbw = new KeyBindWnd();
			} else {
				ui.destroy(ui.kbw);
				ui.kbw = null;
			}
		}
	};
	public static KeyFunction OPEN_INV = new KeyFunction(OPEN_INV_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.wdgmsg("gk",9);
		}
	};
	public static KeyFunction OPEN_EQUI = new KeyFunction(OPEN_EQUI_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.wdgmsg("gk",5);
		}
	};
	public static KeyFunction OPEN_BUDDY = new KeyFunction(OPEN_BUDDY_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			//ui.root.wdgmsg("gk",2);
			BuddyWnd.instance.visible = !BuddyWnd.instance.visible;
		}
	};
	public static KeyFunction OPEN_CHRW = new KeyFunction(OPEN_CHRW_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			//ui.root.wdgmsg("gk",20);
			CharWnd.instance.toggle();
		}
	};
	public static KeyFunction OPEN_OPT = new KeyFunction(OPEN_OPT_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.findchild(SlenHud.class).toggleopts();
		}
	};
	public static KeyFunction OPEN_LNDW = new KeyFunction(OPEN_LNDW_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.wdgmsg("gk",12);
		}
	};
	public static KeyFunction OPEN_GCHAT = new KeyFunction(OPEN_GCHAT_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.wdgmsg("gk",3);
		}
	};
	public static KeyFunction TOGGLE_HUD = new KeyFunction(TOGGLE_HUD_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.root.findchild(SlenHud.class).vc.toggle();
		}
	};
	public static KeyFunction SS_KEY = new KeyFunction(SCREEN_SHOT_SKEY){ //
		public void dokey(UI ui,KeyEvent ev){
			ui.root.screenshot = true;
		}
	};
	public static KeyFunction RESETCAM = new KeyFunction(RESET_CAM_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.mainview.resetcam();
		}
	};
	public static KeyFunction GRID = new KeyFunction(GRID_TOG_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			Config.grid = !Config.grid;
		}
	};
	public static KeyFunction SPEED_3 = new KeyFunction(SPEED_3_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.spd.setspeed(3,true);
		}
	};
	public static KeyFunction SPEED_2 = new KeyFunction(SPEED_2_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.spd.setspeed(2,true);
		}
	};
	public static KeyFunction SPEED_1 = new KeyFunction(SPEED_1_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.spd.setspeed(1,true);
		}
	};
	public static KeyFunction SPEED_0 = new KeyFunction(SPEED_0_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			ui.spd.setspeed(0,true);
		}
	};
	public static KeyFunction HIDE = new KeyFunction(HIDE_TOG_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			Config.hide = !Config.hide;
		}
	};
	public static KeyFunction XRAY = new KeyFunction(XRAY_TOG_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			Config.xray = !Config.xray;
		}
	};
	public static KeyFunction NIGHTVISION = new KeyFunction(NV_TOG_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			Config.nightvision = !Config.nightvision;
		}
	};
	static 
	{
		funcs.add(MOVE_NORTH);
		funcs.add(MOVE_SOUTH);
		funcs.add(MOVE_EAST);
		funcs.add(MOVE_WEST);
		funcs.add(PROFILE_MV);
		funcs.add(PROFILE_GLOB);
		funcs.add(PROFILE_ILM);
		funcs.add(ENTER_CMD);
		funcs.add(OPEN_KEYBINDWND);
		funcs.add(OPEN_INV);
		funcs.add(OPEN_EQUI);
		funcs.add(OPEN_BUDDY);
		funcs.add(OPEN_CHRW);
		funcs.add(OPEN_OPT);
		funcs.add(OPEN_LNDW);
		funcs.add(OPEN_GCHAT);
		//funcs.add(TOGGLE_HUD);
		funcs.add(SS_KEY);
		funcs.add(RESETCAM);
		funcs.add(GRID);
		funcs.add(SPEED_3);
		funcs.add(SPEED_2);
		funcs.add(SPEED_1);
		funcs.add(SPEED_0);
		funcs.add(HIDE);
		funcs.add(XRAY);
		funcs.add(NIGHTVISION);
	}
	
	public KeyFunction(String sk){
		skey = sk;
		key = Config.getkopt_int(skey+"_INT",Config.default_key_int(skey+"_INT"));
		special = Config.getkopt_int(skey+"_BOOL",Config.default_key_bool(skey+"_BOOL"));
	}
	
	public void dokey(UI ui, KeyEvent ev){}
	public int getkey(){ return key; }
	public int getspec() { return special; }
	public void setdata(int k,int s) { 
		key = k; special = s; 
		Config.setkopt_int(skey+"_INT",key);
		Config.setkopt_int(skey+"_BOOL",special);
	}
}