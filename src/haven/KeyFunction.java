/*
 * Copyright (c) 2011 Corey Furmanski
 *
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
			if(ui.keys[translateCode(MOVE_EAST)-1])
				rel_move(ui,Config.tiles_per_click,Config.tiles_per_click);
			else if(ui.keys[translateCode(MOVE_WEST)-1])
				rel_move(ui,-Config.tiles_per_click,Config.tiles_per_click);
			else
				rel_move(ui,0,Config.tiles_per_click);			
		}
	};
	public static KeyFunction MOVE_SOUTH = new KeyFunction(MOVE_SOUTH_SKEY){
		public void dokey(UI ui,KeyEvent ev){
			if(ui.keys[translateCode(MOVE_EAST)-1])
				rel_move(ui,Config.tiles_per_click,-Config.tiles_per_click);
			else if(ui.keys[translateCode(MOVE_WEST)-1])
				rel_move(ui,-Config.tiles_per_click,-Config.tiles_per_click);
			else
				rel_move(ui,0,-Config.tiles_per_click);
		}
	};
	public static KeyFunction MOVE_EAST = new KeyFunction(MOVE_EAST_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			if(ui.keys[translateCode(MOVE_NORTH)-1])
				rel_move(ui,Config.tiles_per_click,Config.tiles_per_click);
			else if(ui.keys[translateCode(MOVE_SOUTH)-1])
				rel_move(ui,Config.tiles_per_click,-Config.tiles_per_click);
			else
				rel_move(ui,Config.tiles_per_click,0);
		}
	};
	public static KeyFunction MOVE_WEST = new KeyFunction(MOVE_WEST_SKEY) {
		public void dokey(UI ui,KeyEvent ev){
			if(ui.keys[translateCode(MOVE_NORTH)-1])
				rel_move(ui,-Config.tiles_per_click,Config.tiles_per_click);
			else if(ui.keys[translateCode(MOVE_SOUTH)-1])
				rel_move(ui,-Config.tiles_per_click,-Config.tiles_per_click);
			else
				rel_move(ui,-Config.tiles_per_click,0);
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
	
	public static void rel_move(UI ui,int x,int y){
		ui.mainview.wdgmsg("click",Coord.fake,ui.sess.glob.oc.getgob(ui.mainview.playergob).getc().add(x*ui.mainview.east*MapView.ONE_TILE,y*ui.mainview.north*MapView.ONE_TILE),1,ui.modflags());
	}	
	
	public static int translateCode(KeyFunction kf){
		switch(kf.special){
		case SPECIAL:
		case CTRL:  return kf.key;
		case ALT:
		case NORMAL: if(kf.key >= 97 && kf.key <= 122)
					   return kf.key -= 32;
				switch((char)kf.key){
				case '~':
				case '`': return KeyEvent.VK_BACK_QUOTE;
				case '!': return KeyEvent.VK_EXCLAMATION_MARK;
				case '@': return KeyEvent.VK_AT;
				case '#': return KeyEvent.VK_3;
				case '$': return KeyEvent.VK_DOLLAR;
				case '%': return KeyEvent.VK_5;
				case '^': return KeyEvent.VK_CIRCUMFLEX;
				case '&': return KeyEvent.VK_AMPERSAND;
				case '*': return KeyEvent.VK_ASTERISK;
				case '(': return KeyEvent.VK_LEFT_PARENTHESIS;
				case ')': return KeyEvent.VK_RIGHT_PARENTHESIS;
				case '-': return KeyEvent.VK_SUBTRACT;
				case '_': return KeyEvent.VK_UNDERSCORE;
				case '=': return KeyEvent.VK_EQUALS;
				case '+': return KeyEvent.VK_PLUS;
				case '[': return KeyEvent.VK_OPEN_BRACKET;
				case '{': return KeyEvent.VK_BRACELEFT;
				case ']': return KeyEvent.VK_CLOSE_BRACKET;
				case '}': return KeyEvent.VK_BRACERIGHT;
				case ':': return KeyEvent.VK_COLON;
				case ';': return KeyEvent.VK_SEMICOLON;
				case '"': return KeyEvent.VK_QUOTE;
				case '<': return KeyEvent.VK_COMMA;
				case '>': return KeyEvent.VK_PERIOD;
				case '|': return KeyEvent.VK_BACK_SLASH;
				case '\\': return KeyEvent.VK_BACK_SLASH;
				case ',': return KeyEvent.VK_COMMA;
				case '.': return KeyEvent.VK_PERIOD;
				case '/': return KeyEvent.VK_DIVIDE;
				case '?': return KeyEvent.VK_SLASH;
				case ' ': return KeyEvent.VK_SPACE;
				case 127: return KeyEvent.VK_DELETE;
				}
		}
		return 0;
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
