package version4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
/**
 * @author chrisq E-mail:chrisq@bu.edu
 */
class AePlayWave extends Thread {

	private String filename;
	public AePlayWave(String wavfile) {
		filename = wavfile;

	}

	public void run() {

		File soundFile = new File(filename);

		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		auline.start();
		int nBytesRead = 0;

		byte[] abData = new byte[512];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}

	}

	
}
class Node{
	int x;
	int y;
	int direct;
	public Node(int x, int y, int direct){
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
}

class Recorder{
	private static int enNum=20;
	private static int myLife=3;
	private static int allEnNum=0;
	
	static Vector<Node> nodes= new Vector<Node>();
	
	private	static FileWriter fw = null;
	private static BufferedWriter bfw = null;
	private static FileReader fr = null;
	private static BufferedReader bfr = null;
	private static Vector<Enemy> ets = new Vector<Enemy>();
	
	public Vector<Node> getNodesAndEnemy(){
		try {
			fr = new FileReader("./myRecording.txt");
			bfr = new BufferedReader(fr);
			String n = "";
			n=bfr.readLine();
			allEnNum = Integer.parseInt(n);
			while((n=bfr.readLine())!=null){
				String []xyz=n.split(" ");
				Node node = new Node (Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
				nodes.add(node);
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bfr.close();
				fr.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return nodes;
	}
	
	public Vector<Enemy> getEts() {
		return ets;
	}

	public void setEts(Vector<Enemy> ets) {
		this.ets = ets;
	}

	public static void getScore(){
		try {
			fr = new FileReader("./myRecording.txt");
			bfr = new BufferedReader(fr);
			String n = bfr.readLine();
			allEnNum = Integer.parseInt(n);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bfr.close();
				fr.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	public void  keepScroeAndEnemy(){
		try {
			fw = new FileWriter("./myRecording.txt");
			bfw = new BufferedWriter(fw);
			bfw.write(allEnNum+"\r\n");
			for(int i=0;i<ets.size();i++){
				Enemy et = ets.get(i);
				if(et.isalive){
					String record = et.x+" "+et.y+" "+et.direct;
					bfw.write(record+"\r\n");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bfw.close();
				fw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	public void keepScore(){
		try {
			fw = new FileWriter("./myRecording.txt");
			bfw = new BufferedWriter(fw);
			bfw.write(allEnNum+"\r\n");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bfw.close();
				fw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
	}
	
	public static int getAllEnNum() {
		return allEnNum;
	}
	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) { 
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}
	
	public static void reduceEnemy(){
		enNum--;
	}
	public static void addScore(){
		allEnNum++;
	}
}

class Tank {
	boolean isalive = true;
	int x = 0;
	int y = 0;
	int direct = 0; // 0 up, 1 down, 2 left, 3 right
	int type = 1;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	int speed = 5;

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}

class Enemy extends Tank implements Runnable {

	int times = 0;
	Vector<Bullet> ebs = new Vector<Bullet>();
	Vector<Enemy> ets = new Vector<Enemy>();

	public Enemy(int x, int y) {
		super(x, y);
	}

	public void setEts(Vector<Enemy> vv) {
		this.ets = vv;
	}

	public boolean isTouchOtherEnemy() {
		boolean b = false;
		switch (direct) {
		case 0:
			for (int i = 0; i < ets.size(); i++) {
				Enemy et = ets.get(i);
				if (et != this) {
					if (et.direct == 0 || et.direct == 2) {
						if (this.x - 10 >= et.x - 10
								&& this.x - 10 <= et.x + 10
								&& this.y - 15 >= et.y - 15
								&& this.y - 15 <= et.y + 15) {
							return true;
						}
						if (this.x + 10 >= et.x - 10
								&& this.x + 10 <= et.x + 10
								&& this.y - 15 >= et.y - 15
								&& this.y - 15 <= et.y + 15) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x - 10 >= et.x - 15
								&& this.x - 10 <= et.x + 15
								&& this.y - 15 >= et.y - 10
								&& this.y - 15 <= et.y + 10) {
							return true;
						}
						if (this.x + 10 >= et.x - 15
								&& this.x + 10 <= et.x + 15
								&& this.y - 15 >= et.y - 10
								&& this.y - 15 <= et.y + 10) {
							return true;
						}
					}
				}
			}
			break;
		case 1:
			for (int i = 0; i < ets.size(); i++) {
				Enemy et = ets.get(i);
				if (et != this) {
					if (et.direct == 0 || et.direct == 2) {
						if (this.x - 10 >= et.x - 10
								&& this.x - 10 <= et.x + 10
								&& this.y + 15 >= et.y - 15
								&& this.y + 15 <= et.y + 15) {
							return true;
						}
						if (this.x + 10 >= et.x - 10
								&& this.x + 10 <= et.x + 10
								&& this.y + 15 >= et.y - 15
								&& this.y + 15 <= et.y + 15) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x - 10 >= et.x - 15
								&& this.x - 10 <= et.x + 15
								&& this.y + 15 >= et.y - 10
								&& this.y + 15 <= et.y + 10) {
							return true;
						}
						if (this.x + 10 >= et.x - 15
								&& this.x + 10 <= et.x + 15
								&& this.y + 15 >= et.y - 10
								&& this.y + 15 <= et.y + 10) {
							return true;
						}
					}
				}
			}
			break;
		case 2:
			for (int i = 0; i < ets.size(); i++) {
				Enemy et = ets.get(i);
				if (et != this) {
					if (et.direct == 0 || et.direct == 2) {
						if (this.x - 15 >= et.x - 10
								&& this.x - 15 <= et.x + 10
								&& this.y - 10 >= et.y - 15
								&& this.y - 10 <= et.y + 15) {
							return true;
						}
						if (this.x - 15 >= et.x - 10
								&& this.x - 15 <= et.x + 10
								&& this.y + 10 >= et.y - 15
								&& this.y + 10 <= et.y + 15) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x - 15 >= et.x - 15
								&& this.x - 15 <= et.x + 15
								&& this.y - 10 >= et.y - 10
								&& this.y - 10 <= et.y + 10) {
							return true;
						}
						if (this.x - 15 >= et.x - 15
								&& this.x - 15 <= et.x + 15
								&& this.y + 10 >= et.y - 10
								&& this.y + 10 <= et.y + 10) {
							return true;
						}
					}
				}
			}
			break;
		case 3:
			for (int i = 0; i < ets.size(); i++) {
				Enemy et = ets.get(i);
				if (et != this) {
					if (et.direct == 0 || et.direct == 2) {
						if (this.x + 15 >= et.x - 10
								&& this.x + 15 <= et.x + 10
								&& this.y - 10 >= et.y - 15
								&& this.y - 10 <= et.y + 15) {
							return true;
						}
						if (this.x + 15 >= et.x - 10
								&& this.x + 15 <= et.x + 10
								&& this.y + 10 >= et.y - 15
								&& this.y + 10 <= et.y + 15) {
							return true;
						}
					}
					if (et.direct == 1 || et.direct == 3) {
						if (this.x + 15 >= et.x - 15
								&& this.x + 15 <= et.x + 15
								&& this.y - 10 >= et.y - 10
								&& this.y - 10 <= et.y + 10) {
							return true;
						}
						if (this.x + 15 >= et.x - 15
								&& this.x + 15 <= et.x + 15
								&& this.y + 10 >= et.y - 10
								&& this.y + 10 <= et.y + 10) {
							return true;
						}
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {

			switch (direct) {
			case 0:
				for (int i = 0; i < 30; i++) {
					if (y > 15 && !this.isTouchOtherEnemy()) {
						y -= 1;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				break;
			case 1:
				for (int i = 0; i < 30; i++) {
					if (y < 285 && !this.isTouchOtherEnemy()) {
						y += 1;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				break;
			case 2:
				for (int i = 0; i < 30; i++) {
					if (x > 15 && !this.isTouchOtherEnemy()) {
						x -= 1;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				break;
			case 3:
				for (int i = 0; i < 30; i++) {
					if (x < 385 && !this.isTouchOtherEnemy()) {
						x += 1;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				break;
			}
			if (times % 2 == 0) {
				if (isalive) {
					if (ebs.size() < 5) {
						Bullet b = null;
						switch (direct) {
						case 0:
							b = new Bullet(x, y, 0);
							ebs.add(b);
							break;
						case 1:
							b = new Bullet(x, y, 1);
							ebs.add(b);
							break;
						case 2:
							b = new Bullet(x, y, 2);
							ebs.add(b);
							break;
						case 3:
							b = new Bullet(x, y, 3);
							ebs.add(b);
							break;

						}
						Thread t = new Thread(b);
						t.start();
					}
				}
			}
			// if(ebs.size()<1){
			// Bullet b = new Bullet(x, type, direct);
			// }
			this.direct = (int) (Math.random() * 4);
			if (this.isalive == false) {
				break;
			}
			this.times++;
		}
	}
}

class Hero extends Tank {
	// boolean isalive = true;
	Vector<Bullet> bs = new Vector<Bullet>();
	Bullet bh = null;

	public Hero(int x, int y) {
		super(x, y);
	}

	public void shot() {
		Thread t;

		switch (this.direct) {
		case 0:
			bh = new Bullet(x - 1, y - 19, 0);
			bs.add(bh);
			break;
		case 1:
			bh = new Bullet(x - 1, y + 19, 1);
			bs.add(bh);
			break;
		case 2:
			bh = new Bullet(x - 19, y, 2);
			bs.add(bh);
			break;
		case 3:
			bh = new Bullet(x + 19, y, 3);
			bs.add(bh);
			break;
		}
		t = new Thread(bh);
		t.start();

	}

	public void moveup() {
		y -= speed;
	}

	public void movedown() {
		y += speed;
	}

	public void moveright() {
		x += speed;
	}

	public void moveleft() {
		x -= speed;
	}
}

class Bullet implements Runnable {

	int x;
	int y;
	int direct;
	int speed = 5;
	boolean isalive = true;

	public Bullet(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}

	public Bullet() {

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {

			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception

			}
			switch (direct) {
			case 0:
				y -= speed;
				break;
			case 1:
				y += speed;
				break;
			case 2:
				x -= speed;
				break;
			case 3:
				x += speed;
				break;

			}
			if (x < 0 || x > 400 || y < 0 || y > 300) {
				this.isalive = false;
				break;
			}

		}

	}

}

class Bomb {
	int x;
	int y;
	int life = 9;
	boolean isalive = true;

	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;

	}

	public void lifedown() {
		if (life > 0) {
			life--;
		} else {
			this.isalive = false;
		}

	}
}
