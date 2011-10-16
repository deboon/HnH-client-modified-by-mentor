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
import java.util.List;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class KeyBindWnd extends Window{
	static Text.Foundry cf = new Text.Foundry(new Font("Serif", Font.PLAIN, 12));
	public static Coord DEF_POS = new Coord(20,30);
	RootWidget root;
	List<GKeyWdg> keywdgs = new ArrayList<GKeyWdg>();
	public KeyBindWnd(){
		super(DEF_POS,new Coord(300,400),UI.instance.root,"Key Binding");
		root = UI.instance.root;
		int ax = 0;
		int ay = 0;
		int spacing = 23;
		Coord sz = new Coord(150,20);
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Move North:",KeyFunction.MOVE_NORTH)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Move South:",KeyFunction.MOVE_SOUTH)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Move East:",KeyFunction.MOVE_EAST)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Move West:",KeyFunction.MOVE_WEST)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Profile MV:",KeyFunction.PROFILE_MV)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Profile GLOB:",KeyFunction.PROFILE_GLOB)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Profile ILM:",KeyFunction.PROFILE_ILM)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Enter CMD:",KeyFunction.ENTER_CMD)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open KBW:",KeyFunction.OPEN_KEYBINDWND)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open Inv:",KeyFunction.OPEN_INV)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open Equip:",KeyFunction.OPEN_EQUI)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open CHRWND:",KeyFunction.OPEN_CHRW)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open KIN:",KeyFunction.OPEN_BUDDY)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open OPTWND:",KeyFunction.OPEN_OPT)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open LNDWND:",KeyFunction.OPEN_LNDW)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Open GCHAT:",KeyFunction.OPEN_GCHAT)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"ScreenShot:",KeyFunction.SS_KEY)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Reset Cam:",KeyFunction.RESETCAM)); ay+=spacing;
		ax = 155; ay = 0; sz = new Coord(145,20);
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Grid:",KeyFunction.GRID)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Hide:",KeyFunction.HIDE)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"NightVision:",KeyFunction.NIGHTVISION)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"XRay:",KeyFunction.XRAY)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Sprint:",KeyFunction.SPEED_3)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Run:",KeyFunction.SPEED_2)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Walk:",KeyFunction.SPEED_1)); ay+=spacing;
		keywdgs.add(new GKeyWdg(new Coord(ax,ay),sz,this,"Crawl:",KeyFunction.SPEED_0)); ay+=spacing;
	}
	
	public void draw(GOut g){
		super.draw(g);
	}
	
	public boolean checkconflict(int k,int s){
		for(GKeyWdg kb: keywdgs)
			if(kb.checkconflict(k,s))
				return true;
		return false;
	}
	
	public void saveall(){
		for(GKeyWdg kb: keywdgs) {
			kb.ofunc.setdata(kb.key,kb.special);
		}
		RootWidget.reloadkeys();
	}
	
    public void wdgmsg(Widget sender, String msg, Object... args) {
		if(sender == cbtn) {
			ui.kbw = null;
			ui.destroy(this);
		}
    }
	
	public boolean type(char key, java.awt.event.KeyEvent ev) {
		if(key == 27) {
			ui.kbw = null;
			ui.destroy(this);
			return(true);
		}
		return(super.type(key, ev));
    }
	
	class GKeyWdg extends Widget {
		Coord crnd;
		int rot;
		int key;
		boolean rdy = false;
		int special;
		IKeyFunction ofunc;
		Text cur;
		Text lbl;
		
		public GKeyWdg(Coord c,Coord sz,Widget p,String lbl,IKeyFunction func){
			super(c,sz,p);
			this.lbl = KeyBindWnd.cf.render(lbl);
			ofunc = func;
			key = func.getkey();
			special = func.getspec();
			if(special == KeyFunction.CTRL || special == KeyFunction.SPECIAL)
				setText(translateCode(key,special),key,special);
			else
				setText(translateChar((char)key,special),key,special);
		}
		
		public GKeyWdg(Coord c,Coord sz,Widget p,int k,int s){
			super(c,sz,p);
			if(s == KeyFunction.CTRL || s == KeyFunction.SPECIAL)
				setText(translateCode(k,s),k,s);
			else
				setText(translateChar((char)k,s),k,s);
		}
		
		public boolean checkconflict(int k,int s){
			return (key==k && special==s);
		}
		
		public void draw(GOut g){
			g.image(lbl.tex(),Coord.z);
			g.chcolor(0,0,0,128);
			g.frect(new Coord(lbl.sz().x+2,0),new Coord(sz.x-lbl.sz().x,sz.y));
			g.chcolor();
			if(rdy){
				g.chcolor(256,256,256,256);
				g.rect(new Coord(lbl.sz().x+2,0),new Coord(sz.x-lbl.sz().x,sz.y));
				g.chcolor();
			}
			g.image(cur.tex(),crnd);
		}
		
		public void lostfocus() {
			rdy = false;
			super.lostfocus();
		}
		public boolean mousedown(Coord c,int button) {
			parent.setfocus(this);
			rdy = true;
			return(true);
		}
		
		public boolean type(char key, KeyEvent ev) {
			if(ev.isControlDown())
				return(false);
			int spec = (ev.isAltDown())?KeyFunction.ALT:KeyFunction.NORMAL;
			if(rdy && key <= 127 && isallowed(key,ev,spec)){
				setText(translateChar(key,spec),(int)key,spec);
				ui.kbw.saveall();
				lostfocus();
				return(true);
			}
			return(false);
		}
		
		public boolean keydown(KeyEvent ev){
			int spec = (ev.isControlDown())?KeyFunction.CTRL:KeyFunction.SPECIAL;
			int tk = ev.getKeyCode();
			switch(ev.getKeyCode()){
			case 15:
			case 16:
			case 17: return(false);
			}
			if(rdy && isallowed(ev.getKeyCode(),ev,spec)){
				setText(translateCode(tk,spec),tk,spec);
				ui.kbw.saveall();
				lostfocus();
				return(true);
			}
			return(false);
		}
		
		private void setText(String s,int k,int c){
			cur = KeyBindWnd.cf.render(s);
			//crnd = new Coord(lbl.sz().x+((sz.x-lbl.sz().x)/2-s.length()/2*6),0);
			crnd = new Coord((lbl.sz().x+2)+((sz.x-lbl.sz().x-2)/2)-(cur.sz().x/2),0);
			key = k;
			special = c;
			rdy = false;
		}
		
		private boolean isallowed(int key,KeyEvent ev,int c){
			if(ui.kbw.checkconflict(key,c))
				return false;
			if(c == KeyFunction.CTRL || c == KeyFunction.SPECIAL){
				if(key >= 65 && key <= 90)	//allow letters
					return true;
				switch(ev.getKeyCode()){
				case java.awt.event.KeyEvent.VK_UP:
				case java.awt.event.KeyEvent.VK_DOWN:
				case java.awt.event.KeyEvent.VK_LEFT:
				case java.awt.event.KeyEvent.VK_RIGHT:
				case java.awt.event.KeyEvent.VK_HOME:
				case java.awt.event.KeyEvent.VK_END:
				case java.awt.event.KeyEvent.VK_DELETE:
				case java.awt.event.KeyEvent.VK_PAGE_DOWN:
				case java.awt.event.KeyEvent.VK_PAGE_UP:
				case java.awt.event.KeyEvent.VK_INSERT:
					return true;
				}
				return false;
			} else {
				if(key <= 32 && !(key == 9 || key == 8 || key == 10))
					return(false);
				switch(key){
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				//Base Menu grid shit... 
				//there's a ton more, but i figured you wouldn't have your
				//menu grid opened past the base one
				case 'a':
				case 'b':
				case 'g':
				case 'm':
				case 'c':
				case 'G':
				case 'A':
				case 'M':
				case 'B':
				case 'C': return false;
				}
			}
			return true;
		}
		
		private String translateCode(int key,int s){
			if(s != KeyFunction.CTRL){
				switch(key){
				case java.awt.event.KeyEvent.VK_UP: 		return "VK_UP";
				case java.awt.event.KeyEvent.VK_DOWN: 		return "VK_DOWN";
				case java.awt.event.KeyEvent.VK_LEFT: 		return "VK_LEFT";
				case java.awt.event.KeyEvent.VK_RIGHT: 		return "VK_RIGHT";
				case java.awt.event.KeyEvent.VK_HOME:		return "VK_HOME";
				case java.awt.event.KeyEvent.VK_END:		return "VK_END";
				case java.awt.event.KeyEvent.VK_DELETE:		return "VK_DELETE";
				case java.awt.event.KeyEvent.VK_PAGE_DOWN:	return "VK_PG_DOWN";
				case java.awt.event.KeyEvent.VK_PAGE_UP:	return "VK_PG_UP";
				case java.awt.event.KeyEvent.VK_INSERT:		return "VK_INSERT";
				}
			} else if(key >= 65 && key <= 90){
				String str = "CTRL+"+java.awt.event.KeyEvent.getKeyText(key);
				return str;
			}
			return "ERROR";
		}
		
		private String translateChar(char key,int s){
			String str = "";
			if(s == KeyFunction.ALT)
				str = "ALT+";

			switch(key){
				case 10: 	return str+"ENTER";
				case 9:		return str+"TAB";
				case 8:  	return str+"BACKSPACE";
				case 127:	return str+"DELETE";
				default:   	return str+Character.toString(key);
			}
		}
	}
}