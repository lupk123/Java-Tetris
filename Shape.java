import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Shape{
	private int row, col; //��ʼ���� ��
	private Dir dir = Dir.Normal; //����ƶ�����
	private int block = MainFrame.BLOCK_SIZE; //ÿһ��Ԫ��Ĵ�С
	private static Random r = new Random(); //�������
	private int speed = 0; //����������ٶ�
	private int type; //һ����������״�ķ��� type������һ��
	private boolean alive = true; //�Ƿ�״̬
	public boolean isAlive() {
		return alive;
	}

	private int[][] box = null; //ÿ����״�ľ����ʾ
	private MainFrame main; //�ܼ�
	private int left = 5, right = 0, bottom = 0; //��ǰ�����ھ����ʾ������ߵ��� ���ұߵ��� ���������
	
	/**
	 * ���캯��
	 * @param row ��
	 * @param col ��
	 * @param main �ܼ�
	 * ��ʼ��һЩ����  ȷ���������״ 
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
	 * ������ǰ����
	 * @param g ����
	 */
	public void draw(Graphics g){
		Color c = new Color(44, 128, 197);
		g.setColor(c);
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++){
				//��������е�Ԫ��Ϊ1 ����õ�Ԫ������� ���������� �м����ÿ����䵥Ԫ���λ�ò�����
				if(box[i][j] == 1)
					g.fillRect((this.col + j) * this.block, (this.row + i) * this.block, block, block);
			}
		//ÿ��һ�� �ƶ�һ��
		move();
	}
	
	/**
	 * �ƶ����� 
	 */
	public void move(){			
		if(false == alive) 
			return ;
		//��ȡ��ǰ����ı߽�ֵ
		getBorder();

		switch(dir){
		case L: //�����ƶ� ���û�г����߽�
			if(this.col > 0 - left + 1)	
				this.col--;
			break;
		case R: //�����ƶ� ���û�г����߽�
			if(this.col < main.ROW - right)
				this.col++;
			break;
		case D: //����״̬
			//����
			accelerate();
			//�жϺ��ʵ��ٶ�(<=this.speed)
			int temp = this.speed;
			while(temp-- != 0){
				//û�г����߽� ���� Ҫ�ƶ���λ��û�ж�����ķ���
				if(row + 1 > main.COL - bottom || checkDown())
				{
					break;
				}
				this.row++;
			}
			break;
		case U:	//��ת
				box = rotation();	
			break;
		case Normal: //��ͨ״̬ �����ٶ��½� ����û�г����߽�
			if(this.row < main.COL - bottom)
			{
				this.row++;
				main.score += 1;
			}
			break;
		}
		
		/*
		 * ��������λ�����Ѿ������ķ��� �򵽴�ײ�  
		 * 1 ���������״̬ ���ҹܼ��з�����Ϊ�� 
		 * 2 ���ܼҵ�map�������
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
	 * ����Ƿ��������
	 * @return false �������� true ����������
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
	 * ���¹ܼ��е�map���� ����ǰ�Ѿ����ڲ����״̬�����λ����map����1
	 */
	public void setMainBlock(){
		for(int i = 0; i < bottom; i++)
			for(int j = left - 1; j < right; j++)
				if(box[i][j] == 1)
					main.map[row + i][col + j] = 1;				
	}
	
	/**
	 * ��ת �����ת���������ת
	 * @return ��ת��ľ���
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
	 * ��ʼ��ÿ����״�ľ����ʾ
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
