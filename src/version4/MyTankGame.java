package version4;

/**
 * @author chrisq E-mail:chrisq@bu.edu
 * @version Created time：2015年1月7日 下午1:48:39
 * Description:
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MyTankGame extends JFrame implements ActionListener {
	myStartPanel msp = null;
	MyPan mp = null;
	JMenuBar jmb = null;
	JMenu jm1 = null;
	JMenuItem jmi1 = null, jmi2=null, jmi3=null,jmi4=null;

	public static void main(String[] args) {
		MyTankGame myt1 = new MyTankGame();
	}

	public MyTankGame() {
		MyPan mp = new MyPan("newgame");
		jmb = new JMenuBar();
		jm1 = new JMenu("Game(G)");
		jm1.setMnemonic('G');
		
		
		jmi1 = new JMenuItem("Game Start(S)");
		jmi1.setMnemonic('N');
		jmi1.addActionListener(this);
		jmi1.setActionCommand("newgame");
		jmi2 = new JMenuItem("Exit(E)");
		jmi2.setMnemonic('E');
		jmi2.addActionListener(this);
		jmi2.setActionCommand("Exit");
		jmi3 = new JMenuItem("Save&Exit(S)");
		jmi3.setMnemonic('S');
		jmi3.addActionListener(this);
		jmi3.setActionCommand("save");
		jmi4 = new JMenuItem("Load(L)");
		jmi4.setMnemonic('L');
		jmi4.addActionListener(this);
		jmi4.setActionCommand("load");
		
		
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jm1.add(jmi4);
		jmb.add(jm1);

		msp = new myStartPanel();
		this.add(msp);
		Thread tmsp = new Thread(msp);
		tmsp.start();
		this.setJMenuBar(jmb);
		this.setBounds(200, 200, 600, 500);
		this.setVisible(true);
		this.setTitle("Tank war");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("newgame")) {
			mp = new MyPan("newgame");
			this.remove(msp);
			Thread pt = new Thread(mp);
			this.addKeyListener(mp);
			pt.start();
			this.add(mp);
			this.setVisible(true);
		}else if(e.getActionCommand().equals("Exit")){
			new Recorder().keepScore();
			System.exit(0);
		}else if(e.getActionCommand().equals("save")){
			//record score and number
			Recorder rd = new Recorder();
			rd.setEts(mp.ets);
			rd.keepScroeAndEnemy();
			System.exit(0);
		}else if(e.getActionCommand().equals("load")){
			mp = new MyPan("continue");
			
			this.remove(msp);
			Thread pt = new Thread(mp);
			this.addKeyListener(mp);
			pt.start();
			this.add(mp);
			this.setVisible(true);
		}

	}

}

class myStartPanel extends Panel implements Runnable {
	int times = 0;

	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		if (times % 2 == 0) {
			Font myfont = new Font("TimesRoman", Font.BOLD, 30);
			g.setFont(myfont);
			g.setColor(Color.yellow);
			g.drawString("stage:1", 150, 150);
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(400);
			} catch (Exception e) {
				// TODO: handle exception
			}
			times++;
			this.repaint();
		}

	}

}

class MyPan extends Panel implements KeyListener, Runnable {
	Hero hero = null;
	
	Vector<Enemy> ets = new Vector<Enemy>();

	Vector<Bomb> bombs = new Vector<Bomb>();
	
	Vector<Node> nodes = new Vector<Node>();

	int enemySize = 3;

	Image img1 = null;
	Image img2 = null;
	Image img3 = null;

	public MyPan(String flag) {
		//restore the record
		Recorder.getScore();
		hero = new Hero(110, 250);
		hero.setType(1);
		if(flag.equals("newgame")){
			for (int i = 0; i < enemySize; i++) {
				Enemy et = new Enemy((i + 1) * 40, 15);
				et.setDirect(1);
				et.setType(2);
				et.setEts(ets);

				Thread t = new Thread(et);
				t.start();
				Bullet eb = new Bullet(et.x, et.y, et.direct);
				et.ebs.add(eb);
				Thread t2 = new Thread(eb);
				t2.start();
				ets.add(et);

			}
		}else{
			nodes= new Recorder().getNodesAndEnemy();
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				
				Enemy et = new Enemy(node.x, node.y);
				et.setDirect(node.direct);
				et.setType(2);
				et.setEts(ets);

				Thread t = new Thread(et);
				t.start();
				Bullet eb = new Bullet(et.x, et.y, et.direct);
				et.ebs.add(eb);
				Thread t2 = new Thread(eb);
				t2.start();
				ets.add(et);

			}
		}
		
		

		// try {
		// img1 = ImageIO.read(new File("bomb_1.gif"));
		// img2 = ImageIO.read(new File("bomb_2.gif"));
		// img3 = ImageIO.read(new File("bomb_3.gif"));
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

		img1 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("./bomb_1.gif"));
		img2 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("./bomb_2.gif"));
		img3 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("./bomb_3.gif"));
		
		AePlayWave apw = new AePlayWave("./111.wav");
		apw.start();

	}

	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 400, 300);

		this.showInfo(g);

		// draw the hero tank
		if (hero.isalive == true) {
			this.drawTank(hero.getX(), hero.getY(), g, hero.getDirect(),
					hero.getType());
		}

		for (int i = 0; i < bombs.size(); i++) {
			Bomb b = bombs.get(i);
			if (b.life > 6) {
				g.drawImage(img1, b.x, b.y, 30, 30, this);
			} else if (b.life > 3) {
				g.drawImage(img2, b.x, b.y, 30, 30, this);
			} else if (b.life > 0) {
				g.drawImage(img3, b.x, b.y, 30, 30, this);
			}
			b.lifedown();

			if (b.life == 0) {
				b.isalive = false;
				bombs.remove(b);
			}
		}

		for (int i = 0; i < ets.size(); i++) {
			Enemy etank = ets.get(i);
			if (etank.isalive == true) {
				this.drawTank(etank.getX(), etank.getY(), g, etank.getDirect(),
						etank.getType());
				for (int j = 0; j < etank.ebs.size(); j++) {
					Bullet b = etank.ebs.get(j);
					if (b.isalive == true) {
						g.fill3DRect(b.x, b.y, 2, 2, false);
					} else {
						etank.ebs.remove(b);
					}
				}
			}

		}

		for (int i = 0; i < hero.bs.size(); i++) {
			Bullet mybullet = hero.bs.get(i);
			if (mybullet != null && mybullet.isalive == true) {
				g.fill3DRect(mybullet.x, mybullet.y, 2, 2, false);
			}
			if (mybullet.isalive == false) {
				hero.bs.remove(mybullet);
			}
		}
	}

	
	public void showInfo(Graphics g){
		//	draw info tank
				this.drawTank(80, 330, g, 0, 2);
				g.setColor(Color.black);
				g.drawString(Recorder.getEnNum()+"", 93, 335);
				
				this.drawTank(170, 330, g, 0, 1);
				g.setColor(Color.black);
				g.drawString(Recorder.getMyLife()+"", 183, 335);
				
				//play info
				g.setColor(Color.black);
				Font f = new Font("TimesRoman",Font.BOLD,20);
				g.setFont(f);
				g.drawString("your score", 440, 30);
				this.drawTank(470, 60, g, 0, 2);
				g.setColor(Color.black);
				g.drawString(Recorder.getAllEnNum()+"", 500, 65);
				
	}
	
	public void hitHeroTank() {
		for (int i = 0; i < ets.size(); i++) {
			Enemy et = ets.get(i);
			for (int j = 0; j < et.ebs.size(); j++) {
				Bullet b = et.ebs.get(j);
				if (hero.isalive == true) {
					if(this.hittank(b, hero)){
						
					}
				}
			}
		}
	}

	public void hitEnemyTank() {
		for (int i = 0; i < hero.bs.size(); i++) {
			Bullet myb = hero.bs.get(i);
			if (myb.isalive == true) {
				for (int j = 0; j < ets.size(); j++) {
					Enemy myet = ets.get(j);
					if (myet.isalive == true) {
						if(hittank(myb, myet)){
							Recorder.reduceEnemy();
							Recorder.addScore();
						}
					}

				}
			}
		}
	}

	public boolean hittank(Bullet b, Tank et) {
		boolean b3 =false;
		switch (et.direct) {
		case 0:
		case 2:
			if (b.x > et.x - 10 && b.x < et.x + 10 && b.y > et.y - 15
					&& b.y < et.y + 15) {
				b.isalive = false;
				et.isalive = false;
				b3=true;
				Bomb b1 = new Bomb(et.x - 10, et.y - 15);
				bombs.add(b1);
			}
			break;
		case 1:
		case 3:
			if (b.x > et.x - 15 && b.x < et.x + 15 && b.y > et.y - 10
					&& b.y < et.y + 10) {
				b.isalive = false;
				et.isalive = false;
				b3=true;
				Bomb b2 = new Bomb(et.x - 15, et.y - 10);
				bombs.add(b2);

			}
		}
		return b3;
	}

	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		switch (type) {
		case 1:
			g.setColor(Color.CYAN);
			break;
		case 2:
			g.setColor(Color.RED);
			break;
		case 3:
			g.setColor(Color.YELLOW);
			break;
		case 4:
			g.setColor(Color.BLUE);
			break;

		}

		switch (direct) {
		case 0:
			g.fill3DRect(x - 10, y - 15, 5, 30, false);
			g.fill3DRect(x + 5, y - 15, 5, 30, false);
			g.fill3DRect(x - 5, y - 10, 10, 20, false);
			g.fillOval(x - 5, y - 5, 10, 10);
			g.drawLine(x - 1, y, x - 1, y - 18);
			break;
		case 1:
			g.fill3DRect(x - 10, y - 15, 5, 30, false);
			g.fill3DRect(x + 5, y - 15, 5, 30, false);
			g.fill3DRect(x - 5, y - 10, 10, 20, false);
			g.fillOval(x - 5, y - 5, 10, 10);
			g.drawLine(x - 1, y, x - 1, y + 18);
			break;
		case 2:
			g.fill3DRect(x - 15, y - 10, 30, 5, false);
			g.fill3DRect(x - 15, y + 5, 30, 5, false);
			g.fill3DRect(x - 10, y - 5, 20, 10, false);
			g.fillOval(x - 5, y - 5, 10, 10);
			g.drawLine(x, y, x - 17, y);
			break;
		case 3:
			g.fill3DRect(x - 15, y - 10, 30, 5, false);
			g.fill3DRect(x - 15, y + 5, 30, 5, false);
			g.fill3DRect(x - 10, y - 5, 20, 10, false);
			g.fillOval(x - 5, y - 5, 10, 10);
			g.drawLine(x, y, x + 17, y);
			break;

		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == KeyEvent.VK_UP) {
			this.hero.setDirect(0);
			this.hero.moveup();
		} else if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			this.hero.setDirect(1);
			this.hero.movedown();
		} else if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			this.hero.setDirect(2);
			this.hero.moveleft();
		} else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.hero.setDirect(3);
			this.hero.moveright();
		}

		if (arg0.getKeyCode() == KeyEvent.VK_Z) {
			this.hero.shot();
		}

		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
			}

			this.hitEnemyTank();
			this.hitHeroTank();
			for (int i = 0; i < ets.size(); i++) {
				Enemy et = ets.get(i);
				if (et.isalive == true) {
					if (et.ebs.size() < 1) {
						Bullet b = null;
						switch (et.direct) {

						case 0:
							b = new Bullet(et.x - 1, et.y - 19, 0);
							et.ebs.add(b);
							break;
						case 1:
							b = new Bullet(et.x - 1, et.y + 19, 1);
							et.ebs.add(b);
							break;
						case 2:
							b = new Bullet(et.x - 19, et.y, 2);
							et.ebs.add(b);
							break;
						case 3:
							b = new Bullet(et.x + 19, et.y, 3);
							et.ebs.add(b);
							break;

						}
						Thread t = new Thread(b);
						t.start();
					}
				}
			}
			this.repaint();
		}
	}
}
