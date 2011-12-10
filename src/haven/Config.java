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

import static haven.Utils.getprop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.awt.event.KeyEvent;
import java.awt.Color;

import ender.GoogleTranslator;

public class Config {
    public static byte[] authck;
    public static String authuser;
    public static String authserv;
    public static String defserv;
    public static URL resurl, mapurl;
    public static boolean fullscreen;
    public static boolean dbtext;
    public static boolean bounddb;
    public static boolean profile;
    public static boolean nolocalres;
    public static String resdir;
    public static boolean nopreload;
    public static String loadwaited, allused;
    public static boolean xray;
    public static boolean hide;
    public static boolean grid;
    public static boolean timestamp;
    public static boolean new_chat;
    public static boolean highlight = false;
    public static boolean use_smileys;
    public static boolean zoom;
    public static boolean noborders;
    public static boolean new_minimap;
    public static boolean simple_plants = false;
    public static Set<String> hideObjectList;
    public static Set<String> foragObjectList;
    public static HashMap<Pattern, String> smileys;
    public static boolean nightvision;
    public static String currentCharName;
    public static Properties options, window_props,keys;
    public static int sfxVol;
    public static int musicVol;
    public static boolean isMusicOn = false;
    public static boolean isSoundOn = false;
    public static boolean showRadius = false;
    public static boolean showHidden = false;
    public static boolean showBeast = false;
    public static boolean showHP = false;
    public static boolean showForag = false;
    public static boolean showDirection;
    public static boolean showNames;
    public static boolean showOtherNames;
    public static boolean fastFlowerAnim;
    public static boolean sshot_compress;
    public static boolean sshot_noui;
    public static boolean sshot_nonames;
    public static boolean newclaim;
    public static boolean sysCursor;
    public static boolean showq;
    public static boolean showpath;
    public static Map<String, Map<String, Float>> FEPMap = new HashMap<String,Map<String, Float>>();	
		public static Map<Integer,Color> tile_c = new HashMap<Integer,Color>(50);
    public static int tiles_per_click = 5;
    public static int wheel_to_real = 20;
    public static Map<String,Resource.Neg> PAca = new HashMap<String,Resource.Neg>();
    public static boolean global_ui_lock = false;
    public static boolean render_enable = true;

    static {
	try {
	    String p;
	    if((p = getprop("haven.authck", null)) != null)
		authck = Utils.hex2byte(p);
	    authuser = getprop("haven.authuser", null);
	    authserv = getprop("haven.authserv", null);
	    defserv = getprop("haven.defserv", null);
	    if(!(p = getprop("haven.resurl", "http://www.havenandhearth.com/res/")).equals(""))
		resurl = new URL(p);
	    if(!(p = getprop("haven.mapurl", "http://www.havenandhearth.com/mm/")).equals(""))
		mapurl = new URL(p);
	    fullscreen = getprop("haven.fullscreen", "off").equals("on");
	    loadwaited = getprop("haven.loadwaited", null);
	    allused = getprop("haven.allused", null);
	    dbtext = getprop("haven.dbtext", "off").equals("on");
	    bounddb = getprop("haven.bounddb", "off").equals("on");
	    profile = getprop("haven.profile", "off").equals("on");
	    nolocalres = getprop("haven.nolocalres", "").equals("yesimsure");
	    resdir = getprop("haven.resdir", null);
	    nopreload = getprop("haven.nopreload", "no").equals("yes");
	    xray = false;
	    hide = false;
	    grid = false;
	    timestamp = false;
	    nightvision = false;
	    zoom = false;
	    new_minimap = true;
	    GoogleTranslator.lang = "en";
	    GoogleTranslator.turnedon = false;
	    currentCharName = "";
	    options = new Properties();
	    window_props = new Properties();
		keys = new Properties();
	    hideObjectList = Collections.synchronizedSet(new HashSet<String>());
	    foragObjectList = Collections.synchronizedSet(new HashSet<String>());
	    loadOptions();
	    loadWindowOptions();
	    loadkopts();
	    loadSmileys();
	    loadFEP();
			loadTilec();
	} catch(java.net.MalformedURLException e) {
	    throw(new RuntimeException(e));
	}
    }

    public static void loadPA()
    {
	try{
	    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("pa"))));
	    String l;
	    Resource r;
	    Resource.Neg ne;
	    while( (l=br.readLine()) != null){
		r = Resource.load(l);
		r.loadwait();
		PAca.put((ne=r.layer(Resource.negc)).toString(),ne);
	    }
	    ne = null;
	    r = null;
	    br.close();
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

private static void loadTilec() {
				try{
						BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("tilec.conf"))));
						String l;
						String k;
						String objs[];
						while( (l=br.readLine()) != null) {
								objs = l.split("=");
								k = objs[0];
								objs = objs[1].split(",");
								tile_c.put(Integer.parseInt(k),
													 new Color(Integer.parseInt(objs[0]),
																		 Integer.parseInt(objs[1]),
																		 Integer.parseInt(objs[2]),
																		 Integer.parseInt(objs[3])));
						}
						br.close();
				}catch(Exception e){
						e.printStackTrace();
						System.exit(1);
				}
    }
    
    private static void loadFEP() {
	try{
	    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("fep.conf"))));
	    String l;
	    while( (l=br.readLine()) != null) {
		Map<String,Float> fep = new HashMap<String,Float>();
		String tmp[] = l.split("=");
		String name = tmp[0].toLowerCase();
		tmp = tmp[1].split(" ");
		for(String itm : tmp){
		    String tmp2[] = itm.split(":");
		    fep.put(tmp2[0],Float.valueOf(tmp2[1]).floatValue());
		}
		FEPMap.put(name,fep);
	    }
	    br.close();
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public static String mksmiley(String str){
	synchronized (smileys) {
	    for(Pattern p : Config.smileys.keySet()){
		String res = Config.smileys.get(p);
		str = p.matcher(str).replaceAll(res);
	    }
	}
	return str;
    }
    
    private static void usage(PrintStream out) {
	out.println("usage: haven.jar [-hdPf] [-u USER] [-C HEXCOOKIE] [-r RESDIR] [-U RESURL] [-A AUTHSERV] [SERVER]");
    }

    public static void cmdline(String[] args) {
	PosixArgs opt = PosixArgs.getopt(args, "hdPU:fr:A:u:C:");
	if(opt == null) {
	    usage(System.err);
	    System.exit(1);
	}
	for(char c : opt.parsed()) {
	    switch(c) {
	    case 'h':
		usage(System.out);
		System.exit(0);
		break;
	    case 'd':
		dbtext = true;
		break;
	    case 'P':
		profile = true;
		break;
	    case 'f':
		fullscreen = true;
		break;
	    case 'r':
		resdir = opt.arg;
		break;
	    case 'A':
		authserv = opt.arg;
		break;
	    case 'U':
		try {
		    resurl = new URL(opt.arg);
		} catch(java.net.MalformedURLException e) {
		    System.err.println(e);
		    System.exit(1);
		}
		break;
	    case 'u':
		authuser = opt.arg;
		break;
	    case 'C':
		authck = Utils.hex2byte(opt.arg);
		break;
	    }
	}
	if(opt.rest.length > 0)
	    defserv = opt.rest[0];
    }
    
    public static double getSFXVolume()
    {
    	return (double)sfxVol/100;
    }
    
    public static int getMusicVolume()
    {
    	return isMusicOn?musicVol:0;
    }
    
    private static void loadSmileys() {
	smileys = new HashMap<Pattern, String>();
	try {
	    FileInputStream fstream;
	    fstream = new FileInputStream("smileys.conf");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    while ((strLine = br.readLine()) != null)   {
		String [] tmp = strLine.split("\t");
		String smile, res;
		smile = tmp[0];
		res = "\\$img\\[smiley\\/"+tmp[1]+"\\]";
		smileys.put(Pattern.compile(smile, Pattern.CASE_INSENSITIVE|Pattern.LITERAL), res);
	    }
	    in.close();
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
	}
	
    }
    
    private static void loadWindowOptions() {
	File inputFile = new File("windows.conf");
        if (!inputFile.exists()) {
            return;
        }
        try {
            window_props.load(new FileInputStream(inputFile));
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private static void loadOptions() {
        File inputFile = new File("haven.conf");
        if (!inputFile.exists()) {
            return;
        }
        try {
            options.load(new FileInputStream("haven.conf"));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        String hideObjects = options.getProperty("hideObjects", "");
        String foragObjects = options.getProperty("foragObjects", "");
        GoogleTranslator.apikey = options.getProperty("GoogleAPIKey", "AIzaSyCuo-ukzI_J5n-inniu2U7729ZfadP16_0");
        zoom = options.getProperty("zoom", "false").equals("true");
        noborders = options.getProperty("noborders", "false").equals("true");
        new_minimap = options.getProperty("new_minimap", "true").equals("true");
        new_chat = options.getProperty("new_chat", "true").equals("true");
        use_smileys = options.getProperty("use_smileys", "true").equals("true");
        isMusicOn = options.getProperty("music_on", "true").equals("true");
        isSoundOn = options.getProperty("sound_on", "true").equals("true");
        showDirection = options.getProperty("show_direction", "true").equals("true");
        showNames = options.getProperty("showNames", "true").equals("true");
        showOtherNames = options.getProperty("showOtherNames", "false").equals("true");
        showBeast = options.getProperty("showBeast", "false").equals("true");
        showHP = options.getProperty("showHP", "false").equals("true");
        showForag = options.getProperty("showForag", "false").equals("true");
        showRadius = options.getProperty("showRadius", "false").equals("true");
        showHidden = options.getProperty("showHidden", "false").equals("true");
        simple_plants = options.getProperty("simple_plants", "false").equals("true");
        fastFlowerAnim = options.getProperty("fastFlowerAnim", "false").equals("true");
        sshot_compress = options.getProperty("sshot_compress", "false").equals("true");
        sshot_noui = options.getProperty("sshot_noui", "false").equals("true");
        sshot_nonames = options.getProperty("sshot_nonames", "false").equals("true");
        newclaim = options.getProperty("newclaim", "true").equals("true");
        sysCursor = options.getProperty("sysCursor", "false").equals("true");
        showq = options.getProperty("showq", "true").equals("true");
        showpath = options.getProperty("showpath", "false").equals("true");
        sfxVol = Integer.parseInt(options.getProperty("sfx_vol", "100"));
        musicVol = Integer.parseInt(options.getProperty("music_vol", "100"));
	global_ui_lock = options.getProperty("global_ui_lock","false").equals("true");
        hideObjectList.clear();
        if (!hideObjects.isEmpty()) {
            for (String objectName : hideObjects.split(",")) {
                if (!objectName.isEmpty()) {
                    hideObjectList.add(objectName);
                }
            }
        }
        
        foragObjectList.clear();
        if (!foragObjects.isEmpty()) {
            for (String objectName : foragObjects.split(",")) {
                if (!objectName.isEmpty()) {
                    foragObjectList.add(objectName);
                }
            }
        }
        
        Resource.checkhide();
        timestamp = options.getProperty("timestamp","false").equals("true");
		tiles_per_click = Integer.parseInt(options.getProperty("tiles_per_click","5"));
    }

    public static synchronized void setWindowOpt(String key, String value) {
	synchronized (window_props) {
	    String prev_val =window_props.getProperty(key); 
	    if((prev_val != null)&&prev_val.equals(value))
		return;
	    window_props.setProperty(key, value);
	}
	saveWindowOpt();
    }
    
    public static synchronized void setWindowOpt(String key, Boolean value) {
	setWindowOpt(key, value?"true":"false");
    }
    
    public static void saveWindowOpt() {
	synchronized (window_props) {
	    try {
		window_props.store(new FileOutputStream("windows.conf"), "Window config options");
	    } catch (IOException e) {
		System.out.println(e);
	    }
	}
    }
    
    public static void addforag(String str){
	foragObjectList.add(str);
	Resource.checkhide();
    }
    
    public static void remforag(String str){
	foragObjectList.remove(str);
	Resource.checkhide();
    }
    
    public static void addhide(String str){
	hideObjectList.add(str);
	Resource.checkhide();
    }
    
    public static void remhide(String str){
	hideObjectList.remove(str);
	Resource.checkhide();
    }
    
    public static void saveOptions() {
        String hideObjects = "";
        for (String objectName : hideObjectList) {
            hideObjects += objectName+",";
        }
        String foragObjects = "";
        for (String objectName : foragObjectList) {
            foragObjects += objectName+",";
        }
        options.setProperty("hideObjects", hideObjects);
        options.setProperty("foragObjects", foragObjects);
        options.setProperty("GoogleAPIKey", GoogleTranslator.apikey);
        options.setProperty("timestamp", (timestamp)?"true":"false");
        options.setProperty("zoom", zoom?"true":"false");
        options.setProperty("noborders", noborders?"true":"false");
        options.setProperty("new_minimap", new_minimap?"true":"false");
        options.setProperty("new_chat", new_chat?"true":"false");
        options.setProperty("use_smileys", use_smileys?"true":"false");
        options.setProperty("sfx_vol", String.valueOf(sfxVol));
        options.setProperty("music_vol", String.valueOf(musicVol));
        options.setProperty("music_on", isMusicOn?"true":"false");
        options.setProperty("sound_on", isSoundOn?"true":"false");
        options.setProperty("show_direction", showDirection?"true":"false");
        options.setProperty("showNames", showNames?"true":"false");
        options.setProperty("showOtherNames", showOtherNames?"true":"false");
        options.setProperty("showBeast", showBeast?"true":"false");
        options.setProperty("showHP", showHP ? "true" : "false");
        options.setProperty("showForag", showForag ? "true" : "false");
        options.setProperty("showRadius", showRadius?"true":"false");
        options.setProperty("showHidden", showHidden?"true":"false");
        options.setProperty("simple_plants", simple_plants?"true":"false");
        options.setProperty("fastFlowerAnim", fastFlowerAnim?"true":"false");
        options.setProperty("sshot_compress", sshot_compress?"true":"false");
        options.setProperty("sshot_noui", sshot_noui?"true":"false");
        options.setProperty("sshot_nonames", sshot_nonames?"true":"false");
        options.setProperty("newclaim", newclaim?"true":"false");
        options.setProperty("sysCursor", sysCursor?"true":"false");
        options.setProperty("showq", showq?"true":"false");
        options.setProperty("showpath", showpath?"true":"false");
	options.setProperty("tiles_per_click",Integer.toString(tiles_per_click));
	options.setProperty("global_ui_lock",global_ui_lock?"true":"false");
        
        try {
            options.store(new FileOutputStream("haven.conf"), "Custom config options");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
	
	//boshaw below
	public static void setkopt_int(String key,int val){
		keys.setProperty(key,Integer.toString(val));
		savekeys();
	}
	public static int getkopt_int(String key,int def){
		return Integer.parseInt(keys.getProperty(key,Integer.toString(def)));
	}
	
	public static void savekeys(){
		try{
			keys.store(new FileOutputStream("keys.conf"), "Custom Key Bindings");
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public static void loadkopts(){
        File inputFile = new File("keys.conf");
        if (!inputFile.exists()) {
            return;
        }
        try {
            keys.load(new FileInputStream("keys.conf"));
        }
        catch (IOException e) {
            System.out.println(e);
        }
	}
	
	public static int default_key_int(String key){
		if(key.equals(KeyFunction.MOVE_NORTH_SKEY+"_INT"))
			return KeyEvent.VK_UP;
		else if(key.equals(KeyFunction.MOVE_SOUTH_SKEY+"_INT"))
			return KeyEvent.VK_DOWN;
		else if(key.equals(KeyFunction.MOVE_EAST_SKEY+"_INT"))
			return KeyEvent.VK_RIGHT;
		else if(key.equals(KeyFunction.MOVE_WEST_SKEY+"_INT"))
			return KeyEvent.VK_LEFT;
		else if(key.equals(KeyFunction.PROFILE_MV_SKEY+"_INT"))
			return (int)'`';
		else if(key.equals(KeyFunction.PROFILE_GLOB_SKEY+"_INT"))
			return (int)'~';
		else if(key.equals(KeyFunction.PROFILE_ILM_SKEY+"_INT"))
			return (int)'!';
		else if(key.equals(KeyFunction.ENTERCMD_SKEY+"_INT"))
			return (int)':';
		else if(key.equals(KeyFunction.OPEN_KBW_SKEY+"_INT"))
			return (int)'k';
		else if(key.equals(KeyFunction.OPEN_INV_SKEY+"_INT"))
			return 9;
		else if(key.equals(KeyFunction.OPEN_EQUI_SKEY+"_INT"))
			return KeyEvent.VK_E;
		else if(key.equals(KeyFunction.OPEN_CHRW_SKEY+"_INT"))
			return KeyEvent.VK_T;
		else if(key.equals(KeyFunction.OPEN_OPT_SKEY+"_INT"))
			return KeyEvent.VK_O;
		else if(key.equals(KeyFunction.OPEN_BUDDY_SKEY+"_INT"))
			return KeyEvent.VK_B;
		else if(key.equals(KeyFunction.OPEN_LNDW_SKEY+"_INT"))
			return KeyEvent.VK_L;
		else if(key.equals(KeyFunction.OPEN_GCHAT_SKEY+"_INT"))
			return KeyEvent.VK_C;
		else if(key.equals(KeyFunction.TOGGLE_HUD_SKEY+"_INT"))
			return 32;
		else if(key.equals(KeyFunction.SCREEN_SHOT_SKEY+"_INT"))
			return KeyEvent.VK_END;
		else if(key.equals(KeyFunction.RESET_CAM_SKEY+"_INT"))
			return KeyEvent.VK_HOME;
		else if(key.equals(KeyFunction.GRID_TOG_SKEY+"_INT"))
			return KeyEvent.VK_G;
		else if(key.equals(KeyFunction.SPEED_3_SKEY+"_INT"))
			return 114;
		else if(key.equals(KeyFunction.SPEED_2_SKEY+"_INT"))
			return 101;
		else if(key.equals(KeyFunction.SPEED_1_SKEY+"_INT"))
			return 119;
		else if(key.equals(KeyFunction.SPEED_0_SKEY+"_INT"))
			return 113;
		else if(key.equals(KeyFunction.HIDE_TOG_SKEY+"_INT"))
			return KeyEvent.VK_H;
		else if(key.equals(KeyFunction.XRAY_TOG_SKEY+"_INT"))
			return KeyEvent.VK_X;
		else if(key.equals(KeyFunction.NV_TOG_SKEY+"_INT"))
			return KeyEvent.VK_N;
		else {
			return -1; //should never reach here...
		}
	}
	
	public static int default_key_bool(String key){
		int def = KeyFunction.NORMAL;
		if(key.equals(KeyFunction.MOVE_NORTH_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.MOVE_SOUTH_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.MOVE_EAST_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.MOVE_WEST_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.OPEN_EQUI_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.OPEN_CHRW_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.OPEN_OPT_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.OPEN_BUDDY_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.OPEN_LNDW_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.OPEN_GCHAT_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.SCREEN_SHOT_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.RESET_CAM_SKEY+"_BOOL"))
			return KeyFunction.SPECIAL;
		else if(key.equals(KeyFunction.GRID_TOG_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.SPEED_3_SKEY+"_BOOL"))
			return KeyFunction.ALT;
		else if(key.equals(KeyFunction.SPEED_2_SKEY+"_BOOL"))
			return KeyFunction.ALT;
		else if(key.equals(KeyFunction.SPEED_1_SKEY+"_BOOL"))
			return KeyFunction.ALT;
		else if(key.equals(KeyFunction.SPEED_0_SKEY+"_BOOL"))
			return KeyFunction.ALT;
		else if(key.equals(KeyFunction.HIDE_TOG_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.XRAY_TOG_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		else if(key.equals(KeyFunction.NV_TOG_SKEY+"_BOOL"))
			return KeyFunction.CTRL;
		return def;
	}
}
