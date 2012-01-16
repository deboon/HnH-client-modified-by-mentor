package haven;

import java.io.*;
import java.net.Socket;

public class Ment implements Runnable {

    public UI ui;
    public Glob glob;
    public MenuGrid menugrid;
    public static Ment aw;

    public int HP;
    public int Stamina;
    public int Hunger;
    public int PlayerID;
    public Item DragItem = null;
    public boolean HourGlass = false;
    public boolean FlowerMenuReady = false;
    public boolean DragItemResChanged = false;
    Object ResMonitor = new Object();
    private Thread botThread = null;

    private boolean connected = false;
    private InputStream sin;
    private OutputStream sout;
    private DataInputStream in;
    private DataOutputStream out;
    BufferedReader buffin;
    BufferedWriter buffout;


    private class Starve implements Runnable {

        @Override
        public void run() {
            System.out.println("Bot Started");
            while (Hunger > 57) {
                int well_obj = FindMapObj("well", 4, 0, 0);
                int barrel_obj = FindMapObj("barrel", 4, 0, 0);
                if ((well_obj == 0) && (barrel_obj == 0)) {
                    SlenPrint("Cant find well or barrel");
                    System.out.println("Cant find well or barrel");
                    return;
                }
                if(!HaveInventory("Inventory")) {
                    if (!OpenInventory()) {
                        System.out.println("Can't open Inventory... Lets die now.");
                        return;
                    }
                }
                while (!HaveInventory("Inventory")) {
                    Sleep(1000);
                }
                Coord myc = MyCoord();

                Inventory("Inventory", 0, 0, "take", 0);
                while (DragItem == null) {
                    Sleep(300);
                }
                if(well_obj != 0) {
                    if(!MapInteractClick(well_obj, 0)) {
                        return;
                    };
                } else {
                    if(barrel_obj != 0) {
                        if(!MapInteractClick(barrel_obj, 0)) {
                            return;
                        };
                    }
                }
                WaitTillDragResChanged();
                DropToInventory("Inventory", new Coord(0, 0));
                while (DragItem != null) {
                    Sleep(300);
                }
                Inventory("Inventory", 0, 0, "iact", 0);
                while (!FlowerMenuReady) {
                    Sleep(300);
                }
                SelectFlowerMenuOpt("Drink");
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
            }
            System.out.println("End of Bot");
        }

        public void cancel() {
            botThread.interrupt();
            botThread = null;
        }
    }

    private class Bstarve implements Runnable {

        @Override
        public void run() {
            System.out.println("[Bstarve]: Bot Started");
            while (Hunger > 60) {
                if(!HaveInventory("Inventory")) {
                    if (!OpenInventory()) {
                        System.out.println("[Bstarve]: Can't open Inventory... Lets die now.");
                        return;
                    }
                }
                while (!HaveInventory("Inventory")) {
                    Sleep(1000);
                }
                Coord myc = MyCoord();

                Inventory("Inventory", 0, 0, "take", 0);
                while (DragItem == null) {
                    Sleep(300);
                }
                DoInteractClick(myc,0);
                //DoClick(myc, 3, 0);
                WaitTillDragResChanged();
                DropToInventory("Inventory", new Coord(0, 0));
                while (DragItem != null) {
                    Sleep(300);
                }
                Inventory("Inventory", 0, 0, "iact", 0);
                while (!FlowerMenuReady) {
                    Sleep(300);
                }
                SelectFlowerMenuOpt("Drink");
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
                SendAction("plow");
                DoClick(myc, 1, 0);
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
            }
            System.out.println("[Bstarve]: End of Bot");
        }

        public void cancel() {
            botThread.interrupt();
            botThread = null;
        }
    }

    private class Mussel implements Runnable {

        @Override
        public void run() {
            System.out.println("[Mussel]: Bot Started");
            while (FindMapObj("mussel", 5, 0, 0) != 0) {
                int mussel = FindMapObj("mussel", 5, 0, 0);
                DoObjClick(mussel, 3, 0);
                while (!FlowerMenuReady) {
                    Sleep(300);
                }
                SelectFlowerMenuOpt("Pick");
                while (!HourGlass) {
                    Sleep(30);
                }
                while (HourGlass) {
                    Sleep(30);
                }
            }
            System.out.println("[Mussel]: End of Bot");
        }

        public void cancel() {
            botThread.interrupt();
            botThread = null;
        }
    }

    public void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    public boolean WaitTillDragResChanged() {
        synchronized (Ment.aw.ResMonitor) {
            DragItemResChanged = false;
            do {
                try {
                    Ment.aw.ResMonitor.wait();
                } catch (InterruptedException e) { return false;}
            } while (!DragItemResChanged);
            DragItemResChanged = false;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            this.reconnect();
            while (true) {
                try {
                    String cmd = buffin.readLine().replaceAll("\n", "");
                    System.out.println("Received cmd: " + cmd);
                    if (cmd.startsWith("MyCoord")) {
                        Coord c = MyCoord();
                        boolean sw = ((c.x == 0) && (c.y == 0));
                        send("MyCoord|" + sw + "|" + c.x + "|" + c.y);
                    } else if (cmd.startsWith("HaveDragItem")) {
                        send("HaveDragItem|" + HaveDragItem());
                    } else if (cmd.startsWith("OpenInventory")) {
                        send("OpenInventory|" + OpenInventory());
                    } else if (cmd.startsWith("Inventory")) {
                        //boolean Inventory(String name, int x, int y, String action, int mod)
                        String[] args = cmd.split("\\|");
                        if (args.length == 6) {
                            boolean sw = Inventory(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], Integer.parseInt(args[5]));
                            send("Inventory|" + sw);
                        } else {
                            send("!Inventory|wrong_params");
                        }
                    } else if (cmd.startsWith("DropToInventory")) {
                        //DropToInventory|str_invname|int_x|int_y
                        String[] args = cmd.split("\\|");
                        if (args.length == 4) {
                            Coord c = new Coord(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            boolean sw = DropToInventory(args[1], c);
                            send("DropToInventory|" + sw);
                        } else {
                            send("!DropToInventory|wrong_params");
                        }
                    } else if (cmd.startsWith("DropItem")) {
                        String[] args = cmd.split("\\|");
                        if (args.length == 4) {
                            boolean sw = DropItem(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            send("DropItem|" + sw);
                        } else {
                            send("!DropItem|wrong_params");
                        }
                    } else if (cmd.startsWith("MapInteractClick")) {
                        //boolean MapInteractClick(int id, int mod)
                        String[] args = cmd.split("\\|");
                        if (args.length == 3) {
                            boolean sw = MapInteractClick(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                            send("MapInteractClick|" + sw);
                        } else {
                            send("!MapInteractClick|wrong_params");
                        }
                    } else if (cmd.startsWith("DoInteractClick")) {
                        String[] args = cmd.split("\\|");
                        if (args.length == 4) {
                            Coord mc = new Coord(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                            boolean sw = DoInteractClick(mc, Integer.parseInt(args[3]));
                            send("DoInteractClick|" + sw);
                        } else {
                            send("!DoInteractClick|wrong_params");
                        }
                    } else if (cmd.startsWith("DragItemResName")) {
                        //String DragItemResName()
                        String res = DragItemResName();
                        if (res.equals("")) {
                            send("DragItemResName|false");
                        } else {
                            send("DragItemResName|true|" + res);
                        }
                    } else if (cmd.startsWith("HaveInventory")) {
                        String[] args = cmd.split("\\|");
                        if (args.length == 2) {
                            send("HaveInventory|" + HaveInventory(args[1]) + "|" + args[1]);
                        } else {
                            send("!HaveInventory|wrong_params");
                        }
                    } else if (cmd.startsWith("FindMapObj")) {
                        //int FindMapObj(String name, int radius, int x, int y)
                        String[] args = cmd.split("\\|");
                        if (args.length == 5) {
                            int id = FindMapObj(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                            if (id != 0) {
                                send("FindMapObj|true|" + id);
                            } else {
                                send("FindMapObj|false");
                            }
                        } else {
                            send("!FindMapObj|wrong_params");
                        }
                    } else if (cmd.startsWith("SendAction")) {
                        String[] args = cmd.split("\\|");
                        if (args.length == 2) {
                            send("SendAction|" + SendAction(args[1]) + "|" + args[1]);
                        } else {
                            send("!SendAction|wrong_params");
                        }
                    } else if (cmd.startsWith("SelectFlowerMenuOpt")) {
                        //boolean SelectFlowerMenuOpt(String OptName)
                        String[] args = cmd.split("\\|");
                        if (args.length == 2) {
                            send("SelectFlowerMenuOpt|" + SelectFlowerMenuOpt(args[1]));
                        } else {
                            send("!SelectFlowerMenuOpt|wrong_params");
                        }
                    } else if (cmd.startsWith("DoClick")) {
                        //boolean DoClick(Coord mc, int btn, int modflags)
                        String[] args = cmd.split("\\|");
                        if (args.length == 5) {
                            Coord c = new Coord(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                            send("DoClick|" + DoClick(c, Integer.parseInt(args[3]), Integer.parseInt(args[4])));
                        } else {
                            send("!DoClick|wrong_params");
                        }
                    } else if (cmd.startsWith("DoObjClick")) {
                        //DoObjClick|obj_id|btn|modflags)
                        String[] args = cmd.split("\\|");
                        if (args.length == 4) {
                            send("DoObjClick|" + DoObjClick(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
                        } else {
                            send("!DoObjClick|wrong_params");
                        }
                    } else {
                        System.out.println("Received unknown comand");
                        send("!" + cmd + "|Unknown_command");
                    }
                } catch (Exception e) {
                    System.out.println("Connection to Master LOST: " + e);
                    e.printStackTrace();
                    this.connected = false;
                    this.reconnect();
                }
            }
        } catch (Exception e) {
            System.out.println("Puppet thread died: " + e);
            return;
        }
    }

    private void reconnect() {
        while (true) {
            try {
                Socket bot_sock = new Socket("localhost", 40000);
                System.out.println("Connected to Master");
                sin = bot_sock.getInputStream();
                sout = bot_sock.getOutputStream();
                in = new DataInputStream(sin);
                out = new DataOutputStream(sout);
                buffin = new BufferedReader(new InputStreamReader(sin));
                buffout = new BufferedWriter(new OutputStreamWriter(sout));
                connected = true;
                break;
            } catch (Exception e) {
                //System.out.println("Cant connect to Master " + e + "\nRetry in 5 sec");
                connected = false;
                try {
                    Thread.sleep(5000);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public Ment() {
        aw = this;
        Thread bot_thread = new Thread(this);
        bot_thread.start();
    }

    public boolean send(String msg) {
        if (connected) {
            try {
                msg = msg.replaceAll("\n", "");
                msg = msg + "\n";
                sout.write(msg.getBytes(), 0, msg.length());
            } catch (Exception e) {
                return false;
            }
            return (true);
        }
        return false;
    }

    public void setPlayerID(int id) {
        PlayerID = id;
    }

    {
        Console.setscmd("starve", new Console.Command() {
            public void run(Console cons, String[] args) {
                try {
                    botThread = new Thread(new Starve());
                    botThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Console.setscmd("bstarve", new Console.Command() {
            public void run(Console cons, String[] args) {
                try {
                    botThread = new Thread(new Bstarve());
                    botThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Console.setscmd("m", new Console.Command() {
            public void run(Console cons, String[] args) {
                try {
                    botThread = new Thread(new Mussel());
                    botThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Console.setscmd("stop", new Console.Command() {
            public void run(Console cons, String[] args) {
                if(botThread != null) {
                    System.out.println("Bot Stopped");
                    botThread.stop();
                    botThread = null;
                }
            }
        });
    }

    public boolean MapInteractClick(int id, int mod) {
        Gob pgob, gob, playergob;
        synchronized (glob.oc) {
            pgob = glob.oc.getgob(PlayerID);
            gob = glob.oc.getgob(id);
        }
        if (pgob == null || gob == null) return false;
        Coord mc = gob.getc();
        ui.mainview.wdgmsg("itemact", GetCenterScreenCoord(), mc, mod, id, mc);
        return true;
    }

    public boolean DoInteractClick(Coord mc, int modflags) {
        if (ui.mainview != null) {
            ui.mainview.wdgmsg("itemact", GetCenterScreenCoord(), mc, modflags);
            return true;
        }
        return false;
    }

    public int FindMapObj(String name, int radius, int x, int y) {
        Coord my = MyCoord();
        my = MapView.tilify(my);
        int tilesz = 11;
        Coord offset = new Coord(x, y).mul(tilesz);
        my = my.add(offset);
        double min = radius * 11;
        Gob min_gob = null;

        synchronized (glob.oc) {
            for (Gob gob : glob.oc) {
                double len = gob.getc().dist(my);
                boolean m = ((name.length() > 0) && (gob.GetResName().contains(name))) || (name.length() < 1);
                if ((m) && (len < min)) {
                    min = len;
                    min_gob = gob;
                }
            }
        }
        if (min_gob != null)
            return min_gob.id;
        else
            return 0;
    }

    public boolean DoClick(Coord mc, int btn, int modflags) {
        if (ui.mainview != null) {
            ui.mainview.wdgmsg("click", GetCenterScreenCoord(), mc, btn, modflags);
            return true;
        }
        return false;
    }

    public boolean DoObjClick(int obj_id, int btn, int modflags) {
        Coord sc, oc;
        Gob o = glob.oc.getgob(obj_id);
        if (o == null) return false;
        if (ui.mainview != null) {
            sc = GetCenterScreenCoord();
            oc = o.getc();
            System.out.println("send object click: "+oc.toString()+" obj_id="+obj_id+" btn="+btn+" modflags="+modflags);
            ui.mainview.wdgmsg("click", sc, oc, btn, modflags, obj_id, oc);
            return true;
        }
        return false;
    }

    public boolean SendAction(String act_name) {
        if (menugrid != null) {
            if (act_name.equals("laystone")) {
                menugrid.wdgmsg("act", "stoneroad", "stone");
            } else {
                menugrid.wdgmsg("act", act_name);
            }
        } else {
            return false;
        }
        return true;
    }

    public Coord MyCoord() {
        Gob pl;
        if (glob != null) {
            if (((pl = glob.oc.getgob(PlayerID)) != null))
                return pl.getc();
        }
        return new Coord(0, 0);
    }

    public boolean HaveDragItem() {
        for (Widget wdg = ui.root.child; wdg != null; wdg = wdg.next) {
            if ((wdg instanceof Item) && (((Item) wdg).dm)) return true;
        }
        return false;
    }

    public String DragItemResName() {
        if (DragItem != null) {
            return DragItem.GetResName();
        }
        return "";
    }

    public boolean HaveInventory(String name) {
        Widget root = ui.root;
        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof Window) {
                if (((Window) wdg).cap != null) {
                    if (((Window) wdg).cap.text.equals(name)) {
                        for (Widget inv = wdg.child; inv != null; inv = inv.next) {
                            if (inv instanceof Inventory) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean OpenInventory() {
        if (ui != null) {
            ui.root.wdgmsg("gk", 9);
            return true;
        }
        return false;
    }

    public boolean Inventory(String name, int x, int y, String action, int mod) {
        if (
                (!action.equals("take")) &&
                        (!action.equals("transfer")) &&
                        (!action.equals("drop")) &&
                        (!action.equals("iact")) &&
                        (!action.equals("itemact"))
                ) return false;
        Widget root = ui.root;
        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof Window) {
                if (((Window) wdg).cap != null) {
                    if (((Window) wdg).cap.text.equals(name)) {
                        for (Widget inv = wdg.child; inv != null; inv = inv.next) {
                            if (inv instanceof Inventory) {
                                Inventory invn = (Inventory) inv;
                                // ищем вещь в указанных координатах
                                for (Widget i = invn.child; i != null; i = i.next) {
                                    if (i instanceof Item) {
                                        Item it = (Item) i;
                                        if ((it.coord_x() == x) && (it.coord_y() == y)) {
                                            Coord c = GetCenterScreenCoord();
                                            if (action.equals("itemact")) {
                                                it.wdgmsg("itemact", mod);
                                            } else {
                                                it.wdgmsg(action, c);
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean DropToInventory(String invname, Coord c) {
        Widget root = ui.root;
        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof Window) {
                if (((Window) wdg).cap != null) {
                    if (((Window) wdg).cap.text.equals(invname)) {
                        for (Widget inv = wdg.child; inv != null; inv = inv.next) {
                            if (inv instanceof Inventory) {
                                Inventory invn = (Inventory) inv;
                                invn.wdgmsg("drop", c);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Coord GetCenterScreenCoord() {
        Coord sc, sz;
        if (ui.mainview != null) {
            sz = ui.mainview.sz;
            sc = new Coord(
                    (int) Math.round(Math.random() * 200 + sz.x / 2 - 100),
                    (int) Math.round(Math.random() * 200 + sz.y / 2 - 100));
            return sc;
        } else {
            return new Coord(400, 400);
        }
    }

    public boolean DropItem(String name, int cx, int cy) {
        Coord c = new Coord(cx, cy);
        Widget root = ui.root;
        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof Window) {
                if (((Window) wdg).cap != null) {
                    if (((Window) wdg).cap.text.equals(name)) {
                        for (Widget inv = wdg.child; inv != null; inv = inv.next) {
                            if (inv instanceof Inventory) {
                                Inventory invn = (Inventory) inv;
                                invn.wdgmsg("drop", c);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean SelectFlowerMenuOpt(String OptName) {
        if (FlowerMenuReady) {
            ui.flower_menu.SelectOpt(OptName);
            return true;
        }
        return false;
    }

    public static void SlenPrint(String msg) {
        Widget root = Ment.aw.ui.root;
        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof SlenHud)
                ((SlenHud)wdg).error(msg);
        }
    }
}
