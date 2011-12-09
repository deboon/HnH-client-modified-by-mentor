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

import static haven.MCache.cmaps;
import static haven.MCache.tilesz;
import haven.MCache.Grid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

public class MiniMap extends Widget {
    static Map<String, Tex> grids = new WeakHashMap<String, Tex>();
    static Set<String> loading = new HashSet<String>();
    static Loader loader = new Loader();
    static Coord mappingStartPoint = null;
    static long mappingSession = 0;
    static Map<String, Coord> gridsHashes = new TreeMap<String, Coord>();
    static Map<Coord, String> coordHashes = new TreeMap<Coord, String>();
    static Map<Coord, Tex> caveTex = new TreeMap<Coord, Tex>();
    public static final Tex bg = Resource.loadtex("gfx/hud/mmap/ptex");
    public static final Tex nomap = Resource.loadtex("gfx/hud/mmap/nomap");
    public static final Resource plx = Resource.load("gfx/hud/mmap/x");
    public Coord off, doff;
    boolean hidden = false, grid=false;;
    MapView mv;
    boolean dm = false;
    public int scale = 4;
    double scales[] = {0.5, 0.66, 0.8, 0.9, 1, 1.1, 1.25, 1.5, 1.75, 2};
	Coord pcoord;
    
    public double getScale() {
        return scales[scale];
    }

    public void setScale(int scale) {
	this.scale = Math.max(0,Math.min(scale,scales.length-1));
    }

    static class Loader implements Runnable {
	Thread me = null;
	
	private InputStream getreal(String nm) throws IOException {
	    URL url = new URL(Config.mapurl, nm + ".png");
	    URLConnection c = url.openConnection();
	    c.addRequestProperty("User-Agent", "Haven/1.0");
	    InputStream s = c.getInputStream();
	    /*
	     * I've commented this out, since it seems that the JNLP
	     * PersistenceService (or at least Sun's implementation of
	     * it) is SLOWER THAN SNAILS, so this caused more problems
	     * than it solved.
	     *
	    if(ResCache.global != null) {
		StreamTee tee = new StreamTee(s);
		tee.setncwe();
		tee.attach(ResCache.global.store("mm/" + nm));
		s = tee;
	    }
	    */
	    return(s);
	}
	
	private InputStream getcached(String nm) throws IOException {
	    /*if(ResCache.global == null)
		throw(new FileNotFoundException("No resource cache installed"));
	    return(ResCache.global.fetch("mm/" + nm));*/
	    if (mappingSession > 0) {
		String fileName;
		if (gridsHashes.containsKey(nm)) {
		    Coord coordinates = gridsHashes.get(nm);
		    fileName = "tile_" + coordinates.x + "_"
			    + coordinates.y;
		} else {
		    fileName = nm;
		}
		
		File inputfile = new File("map/"
			+ Utils.sessdate(mappingSession) + "/" + fileName
			+ ".png");
		if(!inputfile.exists())
		    throw(new FileNotFoundException("Minimap cache not found"));
		return new FileInputStream(inputfile);
	    }
	    throw(new FileNotFoundException("No resource cache installed"));
	}

	public void run() {
	    try {
		while(true) {
		    String grid;
		    synchronized(grids) {
			grid = null;
			for(String cg : loading) {
			    grid = cg;
			    break;
			}
		    }
		    if(grid == null)
			break;
		    try {
			InputStream in;
			boolean cached;
			try {
			    in = getcached(grid);
			    cached = true;
			} catch(FileNotFoundException e) {
			    in = getreal(grid);
			    cached = false;
			}
			BufferedImage img;
			try {
			    img = ImageIO.read(in);
			    if ((!cached)&(mappingSession > 0)) {
				String fileName;
				if (gridsHashes.containsKey(grid)) {
				    Coord coordinates = gridsHashes.get(grid);
				    fileName = "tile_" + coordinates.x + "_"
					    + coordinates.y;
				} else {
				    fileName = grid;
				}
				
				File outputfile = new File("map/"
					+ Utils.sessdate(mappingSession) + "/" + fileName
					+ ".png");
				ImageIO.write(img, "png", outputfile);
			    }
			} finally {
			    Utils.readtileof(in);
			    in.close();
			}
			Tex tex = new TexI(img);
			synchronized (grids) {
			    grids.put(grid, tex);
			    loading.remove(grid);
			}
		    } catch(IOException e) {
			synchronized(grids) {
			    grids.put(grid, null);
			    loading.remove(grid);
			}
		    }
		}
	    } finally {
		synchronized(this) {
		    me = null;
		}
	    }
	}
	
	void start() {
	    synchronized(this) {
		if(me == null) {
		    me = new HackThread(this, "Minimap loader");
		    me.setDaemon(true);
		    me.start();
		}
	    }
	}
	
	void req(String nm) {
	    synchronized(grids) {
		if(loading.contains(nm))
		    return;
		loading.add(nm);
		start();
	    }
	}
    }
    
    public static void newMappingSession() {
	long newSession = System.currentTimeMillis();
	String date = Utils.sessdate(newSession);
	try {
	    (new File("map/" + date)).mkdirs();
	    Writer currentSessionFile = new FileWriter("map/currentsession.js");
	    currentSessionFile.write("var currentSession = '" + date + "';\n");
	    currentSessionFile.close();
	    mappingSession = newSession;
	    gridsHashes.clear();
	    coordHashes.clear();
	} catch (IOException ex) {
	}
    }

    public MiniMap(Coord c, Coord sz, Widget parent, MapView mv) {
	super(c, sz, parent);
	this.mv = mv;
	off = new Coord();
	newMappingSession();
    }
    
    public static Tex getgrid(final String nm) {
	return(AccessController.doPrivileged(new PrivilegedAction<Tex>() {
		public Tex run() {
		    synchronized(grids) {
			if(grids.containsKey(nm)) {
			    return(grids.get(nm));
			} else {
			    loader.req(nm);
			    return(null);
			}
		    }
		}
	    }));
    }

    public Coord xlate(Coord c, boolean in) {
	if(in) {
	    return c.div(getScale());
	} else {
	    return c.mul(getScale());
	}
    }
    
    public void draw(GOut og) {
	double scale = getScale();
	Coord hsz = sz.div(scale);
	
	Coord tc = mv.mc.div(tilesz).add(off.div(scale));
	Coord ulg = tc.div(cmaps);
	while((ulg.x * cmaps.x) - tc.x + (hsz.x / 2) > 0)
	    ulg.x--;
	while((ulg.y * cmaps.y) - tc.y + (hsz.y / 2) > 0)
	    ulg.y--;
	
	if(!hidden) {
	    Coord s = bg.sz();
	    for(int y = 0; (y * s.y) < sz.y; y++) {
		    for(int x = 0; (x * s.x) < sz.x; x++) {
			og.image(bg, new Coord(x*s.x, y*s.y));
		    }
	    }
	}
	
	GOut g = og.reclip(og.ul.mul((1-scale)/scale), hsz);
	g.gl.glPushMatrix();
	g.scale(scale);
	
	synchronized(caveTex){

	    for(int y = ulg.y; (y * cmaps.y) - tc.y + (hsz.y / 2) < hsz.y; y++) {
		for(int x = ulg.x; (x * cmaps.x) - tc.x + (hsz.x / 2) < hsz.x; x++) {
		    Coord cg = new Coord(x, y);
		    if (mappingStartPoint == null) {
			mappingStartPoint = new Coord(cg);
		    }
		    Grid grid;
		    synchronized(ui.sess.glob.map.req) {
			synchronized(ui.sess.glob.map.grids) {
			    grid = ui.sess.glob.map.grids.get(cg);
			    if(grid == null)
				ui.sess.glob.map.request(cg);
			}
		    }
		    Coord relativeCoordinates = cg.sub(mappingStartPoint);
		    String mnm = null;

		    if(grid == null) {
			mnm = coordHashes.get(relativeCoordinates);
		    } else {
			mnm = grid.mnm;
		    }

		    Tex tex = null;

		    if (mnm != null) {
			caveTex.clear();
			if (!gridsHashes.containsKey(mnm)) {
			    if ((Math.abs(relativeCoordinates.x) > 450)
				    || (Math.abs(relativeCoordinates.y) > 450)) {
				newMappingSession();
				mappingStartPoint = cg;
				relativeCoordinates = new Coord(0, 0);
			    }
			    gridsHashes.put(mnm, relativeCoordinates);
			    coordHashes.put(relativeCoordinates, mnm);
			}
			else {
			    Coord coordinates = gridsHashes.get(mnm);
			    if (!coordinates.equals(relativeCoordinates)) {
				mappingStartPoint = mappingStartPoint.add(relativeCoordinates.sub(coordinates));
			    }
			}

			tex = getgrid(mnm);
			if((tex == null)&&(grid!=null)){
			    tex = grid.getTex();
			}
		    } else {
			if(grid != null) {
			    tex = grid.getTex();
			    if(tex != null) {
				caveTex.put(cg, tex);
			    }
			}
			tex = caveTex.get(cg);
		    }

		    //caveTex.isEmpty();

		    if (tex == null)
			continue;

		    if(!hidden) g.image(tex, cg.mul(cmaps).add(tc.inv()).add(hsz.div(2)));
		}
	    }
	}
	//grid
	if(grid&&!hidden) {
	    g.chcolor(200,32,64,255);
	    Coord c1, c2;
	    c1 = new Coord();
	    c2 = new Coord(hsz.x,0);
	    for(int y = ulg.y+1; (y * cmaps.y) - tc.y + (hsz.y / 2) < hsz.y; y++) {
		c1.y = (y * cmaps.y) - tc.y + (hsz.y / 2);
		c2.y = c1.y;
		g.line(c1, c2, 1);
	    }
	    c1 = new Coord();
	    c2 = new Coord(0,hsz.y);
	    for(int x = ulg.x+1; (x * cmaps.x) - tc.x + (hsz.x / 2) < hsz.x; x++) {
		c1.x = (x * cmaps.x) - tc.x + (hsz.x / 2);
		c2.x = c1.x;
		g.line(c1, c2, 1);
	    }
	    g.chcolor();
	}
	//end of grid
	
	if((!plx.loading)&&(!hidden)) {
	    synchronized(ui.sess.glob.party.memb) {
		for(Party.Member m : ui.sess.glob.party.memb.values()) {
		    Coord ptc = m.getc();
		    if(ptc == null)
			continue;
		    ptc = ptc.div(tilesz).add(tc.inv()).add(hsz.div(2));
			if(m.gobid == ui.mainview.playergob){
				pcoord = ptc.add(plx.layer(Resource.negc).cc.inv());
			}
		    g.chcolor(m.col.getRed(), m.col.getGreen(), m.col.getBlue(), 128);
		    g.image(plx.layer(Resource.imgc).tex(), ptc.add(plx.layer(Resource.negc).cc.inv()));
		    g.chcolor();
		}
	    }
	}
	
	if((Config.showForag) && (!hidden)) {
	    synchronized(ui.sess.glob.oc) {
		g.chcolor(255, 255, 255, 255);
		for (Gob tg : ui.sess.glob.oc) {
		    String name = tg.resname();
		    if ((tg.sc!=null)&&(Config.foragObjectList.contains(name))) {
		    //if ((tg.sc!=null) && (name.indexOf("/cdv") < 0) && (name.indexOf("terobjs/herbs")>=0)) {
			Coord c = tg.rc.div(tilesz).add(tc.inv()).add(hsz.div(2)).sub(3,3);
			Coord size = new Coord(6,6);
			g.rect(c, size);
		    }
		}
		g.chcolor();
	    }
	}
	
	if((Config.showBeast) && (!hidden)) {
	    synchronized(ui.sess.glob.oc) {
		g.chcolor(255, 128, 128, 255);
		for (Gob tg : ui.sess.glob.oc) {
		    String name = tg.resname();
		    if ((tg.sc!=null) && (name.indexOf("/cdv") < 0) && ((name.indexOf("kritter/aurochs")>=0) || (name.indexOf("kritter/bear")>=0) || (name.indexOf("kritter/boar")>=0) || (name.indexOf("kritter/deer")>=0) || (name.indexOf("kritter/fox")>=0) || (name.indexOf("kritter/mufflon")>=0) || (name.indexOf("kritter/troll")>=0))) {
			Coord c = tg.rc.div(tilesz).add(tc.inv()).add(hsz.div(2)).sub(3,3);
			Coord size = new Coord(6,6);
			g.fellipse(c, size);
		    }
		}
		g.chcolor();
	    }
	}
	g.gl.glPopMatrix();
	super.draw(og);
    }
    
    public boolean isCave() {
	synchronized (caveTex) {
	   return !caveTex.isEmpty(); 
	}
    }
    
    public void saveCaveMaps() {
	synchronized (caveTex) {
	    Coord rc = null;
	    String sess = Utils.sessdate(System.currentTimeMillis());
	    File outputfile = new File("cave/" + sess);
	    try {
		Writer currentSessionFile = new FileWriter("cave/currentsession.js");
		currentSessionFile.write("var currentSession = '" + sess + "';\n");
		currentSessionFile.close();
	    } catch (IOException e1) { }
	    outputfile.mkdirs();
	    for(Coord c:caveTex.keySet()) {
		if(rc == null){
		    rc = c;
		}
		TexI tex = (TexI) caveTex.get(c);
		c = c.sub(rc);
		String fileName = "tile_" + c.x + "_" + c.y;
		outputfile = new File("cave/"	+ sess + "/" + fileName + ".png");
		try {
		    ImageIO.write(tex.back, "png", outputfile);
		} catch (IOException e) { }
	    }
	}
    }
    
    public boolean mousedown(Coord c, int button) {
	switch(button){
	case 1: ui.grabmouse(this);
			dm = true;
			doff = c; break;
	case 3: if(pcoord == null)
				break;
			Coord of = c.sub(pcoord);
			Coord pc = ui.sess.glob.oc.getgob(ui.mainview.playergob).getc();
			ui.mainview.wdgmsg("click",Coord.fake,pc.add(of.x*ui.mainview.east*MapView.ONE_TILE,of.y*MapView.ONE_TILE),1,ui.modflags());
	}
	return(true);
    }
    
    public boolean mouseup(Coord c, int button) {
	if(dm) {
	    ui.grabmouse(null);
	    dm = false;
	    return true;
	} else {
	    return super.mouseup(c, button);
	}
    }
    
    public void mousemove(Coord c) {
	if(dm) {
	    off = off.add(doff.sub(c));
	    doff = c;
	} else {
	    super.mousemove(c);
	}
    }
    
    public void hide() {
	hidden = true;
    }
    
    public void show() {
	hidden = false;
    }
}
