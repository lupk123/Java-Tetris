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
	private static int WIDTH = 150, HEIGHT = 300;	//宽 高
	static int BLOCK_SIZE = 10; //每个单元格的大小
	static int ROW = WIDTH / BLOCK_SIZE, COL = HEIGHT / BLOCK_SIZE; //列 行
	int[][] map = new int[COL][ROW]; //地图  方便记录方块的位置
	private Image offScreen = null;
	private FramePaint framePaint = new FramePaint(); //线程 用来repaint
	boolean have = true; //当前是否还有活动的方块
	private static Random r = new Random(); //随机数 
	int score = 0; //分数
	private boolean gameOver = false; //标识游戏是否结束
	private ArrayList<Shape> shapes = new ArrayList<Shape>(); //用来存放出现的方块
	
	/**
	 * 线程 
	 * @author Administrator
	 * 用来repaint
	 */
	private class FramePaint implements Runnable{
		private boolean running = true;
		
		@Override
		public void run() {
			//之前一直是while(running) 可是发现重新开始的时候无法重画 后来改成这样的
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
		 * 游戏结束 当前线程暂停
		 */
		public void pause(){
			running = false;
		}
		
		/*
		 * 游戏重新开始
		 */
		public void restart(){			
			running = true;
		}
	}

	/**
	 * 键盘监听函数
	 * @author Administrator
	 *
	 */
	private class KeyListener extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			/*
			 * F2重新开始游戏
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
	 * 构造函数 用来初始化一些必须的变量等
	 * @param str 名称
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
	 * 将地图的所有单元初始化为0  如果为1则画出方格
	 */
	public void initalMap(){
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				map[i][j] = 0;
	}
	
	/**
	 * 产生一个方块  位置：行固定 列随机
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
			 * gameOver=true 代表游戏结束 先画出GAME OVER字样提示结束 然后将当前线程暂停
			 */
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("GAME", 14, 100);
			g.drawString("OVER", 15, 200);
			
			framePaint.pause();
			return ;
		}	
		
		/*
		 * 将当前画布颜色设为白色 并画出来
		 */
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);	
		
		/*
		 * 显示得分 
		 */
		g.setColor(Color.black);
		g.drawString("SCORE: "+score, 10, 50);
		
		/*
		 * 设置线条颜色为粉色 并画出 显示结果为网格
		 */
		g.setColor(Color.pink);		
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				g.drawRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);	
		
		/*
		 * 遍历shapes中的元素 如何alive则画出
		 */
		for(Shape shape: shapes)
			if(shape.isAlive())
				shape.draw(g);
		
		/*
		 * 遍历map 如果为1 则将这个单元格填充颜色
		 */
		for(int i = 0; i < COL; i++)
			for(int j = 0; j < ROW; j++)
				if(map[i][j] == 1){
					Color c = new Color(44, 128, 197);
					g.setColor(c);
					g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				}
		
		/*
		 * 如果当前画布中没有方块的 则重新产生一个
		 */
		if(false == this.have)
			createShape();
		
		/*
		 * 然后 消除可以消除的行
		 */
		cutLine();
	}
		
	/**
	 * 重写update函数 双缓冲 消除闪烁
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
	 * 随机获取列位置
	 * @return
	 */
	public int getPosX(){
		return r.nextInt(ROW - 4);
	}
	
	/**
	 * 消除行
	 * 1 遍历map中每一行 如果该行每一个单元的值的和恰好等于列数 则表示可以消除该行
	 * 2  将能消除的行的行标加入到num列表中
	 * 3 遍历num中的每一个元素 从改行往上遍历map 当前行的值设为上一行的值 从而达到消除改行的效果
	 * 4 没消除一行加100分
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
	 * 结束游戏
	 */
	public void stop(){
		gameOver = true;		
	}
	
	/**
	 * 重新开始游戏
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

