import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

public class MainFrame extends Frame{
	private static int WIDTH = 150, HEIGHT = 300;	//�� ��
	static int BLOCK_SIZE = 10; //ÿ����Ԫ��Ĵ�С
	static int ROW = WIDTH / BLOCK_SIZE, COL = HEIGHT / BLOCK_SIZE; //�� ��
	int[][] map = new int[COL][ROW]; //��ͼ  �����¼�����λ��
	private Image offScreen = null;
	private FramePaint framePaint = new FramePaint(); //�߳� ����repaint
	boolean have = true; //��ǰ�Ƿ��л�ķ���
	private static Random r = new Random(); //����� 
	int score = 0; //����
	private boolean gameOver = false; //��ʶ��Ϸ�Ƿ����
	private ArrayList<Shape> shapes = new ArrayList<Shape>(); //������ų��ֵķ���
	
	/**
	 * �߳� 
	 * @author Administrator
	 * ����repaint
	 */
	private class FramePaint implements Runnable{
		private boolean running = true;
		
		@Override
		public void run() {
			//֮ǰһֱ��while(running) ���Ƿ������¿�ʼ��ʱ���޷��ػ� �����ĳ�������
			while(true){
				if(running) 
					repaint();
				try {
					Thread.sleep(150);
				}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/*
		 * ��Ϸ���� ��ǰ�߳���ͣ
		 */
		public void pause(){
			running = false;
		}
		
		/*
		 * ��Ϸ���¿�ʼ
		 */
		public void restart(){			
			running = true;
		}
	}

	/**
	 * ���̼�������
	 * @author Administrator
	 *
	 */
	private class KeyListener extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			/*
			 * F2���¿�ʼ��Ϸ
			 */
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_F2)
				restart();
			
			for(Shape shape: shapes)
				if(shape.isAlive())
					shape.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			for(Shape shape: shapes)
				if(shape.isAlive())
					shape.keyReleased(e);
		}
		
	}
	
	public static void main(String[] args) {
		new MainFrame("Tetromino");
	}
	
	/**
	 * ���캯�� ������ʼ��һЩ����ı�����
	 * @param str ����
	 */
	public MainFrame(String str){
		super(str);
		initalMap();
		this.setLocation(550, 200);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setVisible(true);
		new Thread(this.framePaint).start();
		this.addKeyListener(new KeyListener());
		this.createShape();
		this.addWindowListener(
			new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				
			}
		);
	}
	
	/**
	 * ����ͼ�����е�Ԫ��ʼ��Ϊ0  ���Ϊ1�򻭳�����
	 */
	public void initalMap(){
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				map[i][j] = 0;
	}
	
	/**
	 * ����һ������  λ�ã��й̶� �����
	 */
	public void createShape(){
		int posRow = this.getPosX();
		Shape s = new Shape(1, posRow, this);		
		shapes.add(s);
		this.have = true;
	}
	
	public void paint(Graphics g){		
		if(gameOver){
			/*
			 * gameOver=true ������Ϸ���� �Ȼ���GAME OVER������ʾ���� Ȼ�󽫵�ǰ�߳���ͣ
			 */
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("GAME", 14, 100);
			g.drawString("OVER", 15, 200);
			
			framePaint.pause();
			return ;
		}	
		
		/*
		 * ����ǰ������ɫ��Ϊ��ɫ ��������
		 */
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);	
		
		/*
		 * ��ʾ�÷� 
		 */
		g.setColor(Color.black);
		g.drawString("SCORE: "+score, 10, 50);
		
		/*
		 * ����������ɫΪ��ɫ ������ ��ʾ���Ϊ����
		 */
		g.setColor(Color.pink);		
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				g.drawRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);	
		
		/*
		 * ����shapes�е�Ԫ�� ���alive�򻭳�
		 */
		for(Shape shape: shapes)
			if(shape.isAlive())
				shape.draw(g);
		
		/*
		 * ����map ���Ϊ1 �������Ԫ�������ɫ
		 */
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				if(map[i][j] == 1){
					Color c = new Color(44, 128, 197);
					g.setColor(c);
					g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				}
		
		/*
		 * �����ǰ������û�з���� �����²���һ��
		 */
		if(false == this.have)
			createShape();
		
		/*
		 * Ȼ�� ����������������
		 */
		cutLine();
	}
		
	/**
	 * ��дupdate���� ˫���� ������˸
	 */
	public void update(Graphics g){
		if(this.offScreen == null)
			this.offScreen = this.createImage(WIDTH, HEIGHT);
		Graphics gOffScreen = this.offScreen.getGraphics();
		gOffScreen.drawRect(0, 0, WIDTH, HEIGHT);
		this.paint(gOffScreen);
		g.drawImage(this.offScreen, 0, 0, null);
	}
	
	/**
	 * �����ȡ��λ��
	 * @return
	 */
	public int getPosX(){
		return r.nextInt(ROW - 4);
	}
	
	/**
	 * ������
	 * 1 ����map��ÿһ�� �������ÿһ����Ԫ��ֵ�ĺ�ǡ�õ������� ���ʾ������������
	 * 2  �����������е��б���뵽num�б���
	 * 3 ����num�е�ÿһ��Ԫ�� �Ӹ������ϱ���map ��ǰ�е�ֵ��Ϊ��һ�е�ֵ �Ӷ��ﵽ�������е�Ч��
	 * 4 û����һ�м�100��
	 */
	public void cutLine(){
		ArrayList<Integer> num = new ArrayList<Integer>();
		int sum = 0;
		for(int i = 0; i < COL; i++){
			sum = 0;
			for(int j = 0; j < ROW; j++){
				sum += map[i][j];
				if(sum == ROW)
					num.add(i);
			}
		}
		
		for(Integer line : num)
			for(int i = line; i > 0; i--)
				for(int j = 0; j < ROW; j++)
					map[i][j] = map[i - 1][j];		
		
		score += num.size() * 100;
	}
	
	/**
	 * ������Ϸ
	 */
	public void stop(){
		gameOver = true;		
	}
	
	/**
	 * ���¿�ʼ��Ϸ
	 */
	public void restart(){
		gameOver = false;
		this.score = 0;
		initalMap();
		shapes.clear();
		createShape();
		framePaint.restart();
	}
}

