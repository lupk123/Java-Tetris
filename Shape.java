import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Shape{
	private int row, col; //起始的行 列
	private Dir dir = Dir.Normal; //块的移动方向
	private int block = MainFrame.BLOCK_SIZE; //每一单元格的大小
	private static Random r = new Random(); //随机函数
	private int speed = 0; //加速下落的速度
	private int type; //一共有七种形状的方块 type代表哪一种
	private boolean alive = true; //是否活动状态
	public boolean isAlive() {
		return alive;
	}

	private int[][] box = null; //每个形状的矩阵表示
	private MainFrame main; //管家
	private int left = 5, right = 0, bottom = 0; //当前方块在矩阵表示中最左边的列 最右边的列 最下面的行
	
	/**
	 * 构造函数
	 * @param row 行
	 * @param col 列
	 * @param main 管家
	 * 初始化一些变量  确定方块的形状 
	 */
	public Shape(int row, int col, MainFrame main){
		this.row = row;
		this.col = col;
		this.main = main;
		int type = r.nextInt(7);
		initBoxShape(type);
//		box = rotation();	
	}
	
	/**
	 * 画出当前方块
	 * @param g 画布
	 */
	public void draw(Graphics g){
		Color c = new Color(44, 128, 197);
		g.setColor(c);
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++){
				//如果矩阵中单元格为1 代表该单元格有填充 根据所在行 列计算出每个填充单元格的位置并画出
				if(box[i][j] == 1)
					g.fillRect((this.col + j) * this.block, (this.row + i) * this.block, block, block);
			}
		//每画一次 移动一次
		move();
	}
	
	/**
	 * 移动函数 
	 */
	public void move(){			
		if(false == alive) 
			return ;
		//获取当前方块的边界值
		getBorder();

		switch(dir){
		case L: //向左移动 如果没有超出边界
			if(this.col > 0 - left + 1)	
				this.col--;
			break;
		case R: //向右移动 如果没有超出边界
			if(this.col < main.ROW - right)
				this.col++;
			break;
		case D: //加速状态
			//加速
			accelerate();
			//判断合适的速度(<=this.speed)
			int temp = this.speed;
			while(temp-- != 0){
				//没有超出边界 并且 要移动的位置没有堆砌完的方块
				if(row + 1 > main.COL - bottom || checkDown())
				{
					break;
				}
				this.row++;
			}
			break;
		case U:	//旋转
				box = rotation();	
			break;
		case Normal: //普通状态 正常速度下降 并且没有超出边界
			if(this.row < main.COL - bottom)
			{
				this.row++;
				main.score += 1;
			}
			break;
		}
		
		/*
		 * 如果下面的位置有已经堆砌的方块 或到达底部  
		 * 1 设置自身不活动状态 并且管家中方块设为无 
		 * 2 将管家的map数组更新
		 */
		if(checkDown() || this.row == main.COL - bottom){
			if(this.row <= 2)
				main.stop();
			this.alive = false;
			main.have = false;
			
			setMainBlock();						
		}		
	}
	
	/**
	 * 检查是否可以下落
	 * @return false 可以下落 true 不可以下落
	 */
	public boolean checkDown(){
		for(int i = 0; i < bottom; i++)
			for(int j = left - 1; j < right; j++)
				if(col + j >= 0 && col + j < main.ROW && main.map[row + i][col + j] == 1 && box[i][j] == 1)	
				{
					this.row--;
					return true;
				}
		return false;
	}
	
	/**
	 * 更新管家中的map函数 将当前已经处于不活的状态方块的位置在map中置1
	 */
	public void setMainBlock(){
		for(int i = 0; i < bottom; i++)
			for(int j = left - 1; j < right; j++)
				if(box[i][j] == 1)
					main.map[row + i][col + j] = 1;				
	}
	
	/**
	 * 旋转 如果旋转会出界则不旋转
	 * @return 旋转后的矩阵
	 */
	
	public int[][] rotation(){
		int[][] temp = new int[5][5];
		int len = temp.length;
		int last = len - 1;
		for(int i = 0; i < len; i++)
			for(int j = 0; j < len; j++){
				if(col + last - i >= 0)
					temp[j][last - i] = box[i][j];
				else{
					temp = box;
					break;
				}
			}
		return temp;
	}
	
	/**
	 * 初始化每个形状的矩阵表示
	 * @param e
	 */
	public void keyPressed(KeyEvent e){
		if(!alive)
			return ;
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_LEFT:
			this.dir = Dir.L;
			break;
		case KeyEvent.VK_RIGHT:
			this.dir = Dir.R;
			break;
		case KeyEvent.VK_UP:
			this.dir = Dir.U;
			break;
		case KeyEvent.VK_DOWN:
			this.dir = Dir.D;
			break;
		}
	}
	public void keyReleased(KeyEvent e){
		if(!alive)
			return ;
		this.dir = Dir.Normal;
	}
	public void accelerate(){
		this.speed = 3;
	}
	
	public void getBorder(){
		left = 5;
		right = 0;
		bottom = 0;

		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++){
				if(box[i][j] == 1){
					if(i + 1 > bottom)
						bottom = i + 1;
					if(j + 1 < left)
						left = j + 1;
					if(j + 1 > right)
						right = j + 1;
				}
			}
	}
	
	public void initBoxShape(int type){
		switch(type){
		case 0: //I
			box = new int[][]
					{{0, 0, 1, 0, 0},
					 {0, 0, 1, 0, 0},
					 {0, 0, 1, 0, 0},
					 {0, 0, 1, 0, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 1: //L
			box = new int[][]
					{{0, 0, 1, 0, 0},
					 {0, 0, 1, 0, 0},
					 {0, 0, 1, 1, 0},
					 {0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 2: //J
			box = new int[][]
					{{0, 0, 1, 0, 0},
					 {0, 0, 1, 0, 0},
					 {0, 1, 1, 0, 0},
					 {0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 3: //O
			box = new int[][]
					{{0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},
					 {0, 0, 1, 1, 0},
					 {0, 0, 1, 1, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 4: //S
			box = new int[][]
					{{0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},
					 {0, 0, 1, 1, 0},
					 {0, 1, 1, 0, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 5: //Z
			box = new int[][]
					{{0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},
					 {0, 1, 1, 0, 0},
					 {0, 0, 1, 1, 0},
					 {0, 0, 0, 0, 0},};
			break;
		case 6: //T
			box = new int[][]
					{{0, 0, 0, 0, 0},
					 {0, 0, 0, 0, 0},
					 {0, 1, 1, 1, 0},
					 {0, 0, 1, 0, 0},
					 {0, 0, 0, 0, 0},};
			break;
		}
	}
}
