# Java-Tetris
第一个自己动手独立完成的Java项目--俄罗斯方块

    看完马士兵老师的J2SE教程 跟着做了三个小项目但都是看着视频写的 这是自己独立完成的
    这个项目写了两遍 第一遍遇到瓶颈写不下去了 其实主要是自己思路没有想好 硬编码现象严重还不是很熟悉面向对象编程 
    然后在网上搜了搜 找了找思路 重新开始写的
    
> 
我觉得比较重要的几个思想：
> 
1 地图map使用二维数组标记  0表示该单元格不填充颜色  1表示单元格填充颜色  即如果某个方块处于不活动状态后不再是它自己画自己  而是在利用map将该方块画出来 
    
> 
2 方块使用不同的矩阵表示出来  我第一遍的是给每个形状建立一个类  这样不仅麻烦而且没有必要 利用5*5矩阵很方便的表示出来 如L可以表示为 如下:

```
    {0, 0, 1, 0, 0},
    {0, 0, 1, 0, 0},
    {0, 0, 1, 1, 0},
    {0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0}
```
   
> 
3 旋转算法 利用数学思想可以很方便的将图形旋转 即在5*5矩阵中围绕中心旋转 代码如下:
> 
代码参考: http://www.cppblog.com/biao/archive/2010/10/31/131881.html
```Java
      public int[][] rotation(){
	  	  int[][] temp = new int[5][5];
	    	int len = temp.length;
		    int last = len - 1;
	    	for(int i = 0; i < len; i++)
    			for(int j = 0; j < len; j++)
		    			temp[j][last - i] = box[i][j];
	  		}
	    	return temp;
    	}
```
    	
> 
4 消除某几行的算法 从需要消除的行开始往上遍历 把上一行赋值给当前行即可
    
> 
5 双缓冲 消除闪烁现象 

> 
代码来源：http://developer.51cto.com/art/201105/259587.htm

```Java
// 重写update方法,先将窗体上的图形画在图片对象上，再一次性显示     
public void update(Graphics g) {     
    if (offScreenImage == null) {     
        // 截取窗体所在位置的图片     
        offScreenImage = this.createImage(WIDTH, HEIGHT);     
    }     
    // 获得截取图片的画布     
    Graphics gImage = offScreenImage.getGraphics();     
    // 获取画布的底色并且使用这种颜色填充画布（默认的颜色为黑色）     
    Color c = Color.BLACK;     
    gImage.setColor(c);     
    gImage.fillRect(0, 0, WIDTH, HEIGHT); // 有清除上一步图像的功能，相当于gImage.clearRect(0, 0,    WIDTH, HEIGHT)     
    // 将截下的图片上的画布传给重绘函数，重绘函数只需要在截图的画布上绘制即可，不必在从底层绘制     
    paint(gImage);     
    //将接下来的图片加载到窗体画布上去，才能考到每次画的效果     
    g.drawImage(offScreenImage, 0, 0, null);     
}    
```

    再啰嗦几句 这还是我第一次认认真真的写总结 写完发现收货还是蛮多的~~ 希望我能坚持下去吧
    

