package com.example.surroundcrazycat;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class PlayGround extends SurfaceView implements OnTouchListener {

	// 行数
	private static final int ROW = 9;
	// 列数
	private static final int COL = 9;
	// 障碍的数量
	private static final int BOCKS = COL * ROW / 5;
	// 屏幕宽度
	private int SCREEN_WIDTH;
	// 每个通道的宽度
	private int WIDTH;
	// 奇数行和偶数行通道间的位置偏差量
	private int DISTANCE;
	// 屏幕顶端和通道最顶端间的距离
	private int OFFSET;
	// 整个通道与屏幕两端间的距离
	private int length;
	// 做成神经猫动态图效果的单张图片
	private Drawable cat_drawable;
	// 背景图
	private Drawable background;
	// 神经猫动态图的索引
	private int index = 0;

	//记录二维数组的坐标集合
	private Dot[][] matrix;

	//设置圆点类的对象猫点
	private Dot cat;

	private Timer timer = null;

	private TimerTask timerttask = null;

	private Context context;

	private int steps;

	private int[] images = { R.drawable.cat1, R.drawable.cat2, R.drawable.cat3,
			R.drawable.cat4, R.drawable.cat5, R.drawable.cat6, R.drawable.cat7,
			R.drawable.cat8, R.drawable.cat9, R.drawable.cat10,
			R.drawable.cat11, R.drawable.cat12, R.drawable.cat13,
			R.drawable.cat14, R.drawable.cat15, R.drawable.cat16 };

	@SuppressLint("ClickableViewAccessibility")
//-----------------------------------第0步：声明各种数据---------------------------------------------------
	//在构造函数中初始化各种数据
	public PlayGround(Context context) 
	{
		super(context);
		matrix = new Dot[ROW][COL];
		//初始化动态图和背景图
		cat_drawable = getResources().getDrawable(images[index]);
		background = getResources().getDrawable(R.drawable.catbg);
		this.context = context;
		initGame();
		//添加callback回调接口
		getHolder().addCallback(callback);
		//设置触屏监听
		setOnTouchListener(this);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
	}
	
	//如果点击了手机的返回键，则停止线程
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopTimer();
		}
		return super.onKeyDown(keyCode, event);
	}
//-----------------------------------------------------------------------------------------------------------
	
	
	
//-------------------------------第一步：设置界面数据----------------------------------------------------------------

	//1.0步： 初始化游戏
	private void initGame() {
		steps=0;
	    //先获取圆点的坐标赋给二维数组坐标集合
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j] = new Dot(j, i);
			}
		}
		//再设置所以圆的状态为不可点击的（方便操作，后面指定相关的通道和猫的圆点颜色就行）
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j].setStatus(Dot.STATUS_OFF);
			}
		}
		//接着初始化猫点的二维数组坐标
		cat = new Dot(COL / 2 - 1, ROW / 2 - 1);
		//并获取猫点的二位数组坐标，设置为猫点所在的状态
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_IN);
		
		//最后设置随机的障碍，个数小于我们定义的障碍数
		for (int i = 0; i < BOCKS;) {
			int x = (int) ((Math.random() * 1000) % COL);     //随机生成一个x轴坐标
			int y = (int) ((Math.random() * 1000) % ROW);     //随机生成一个y轴坐标
			//如果随机出现的这个点的状态等于通道状态
			if (getDot(x, y).getStatus() == Dot.STATUS_OFF) 
			{
				//即可设置障碍
				getDot(x, y).setStatus(Dot.STATUS_ON);
				i++;
			}
		}
	}

	// 1.1步：绘图
	private void redraw() 
	{
		//获取画布对象
		Canvas canvas = getHolder().lockCanvas();
		//设置画布背景为灰色
		canvas.drawColor(Color.GRAY);
		Paint paint = new Paint();
		//设置抗锯齿
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		//描绘一个9*9的矩阵
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				DISTANCE = 0;       //设置偏移量
	//如果是偶数行（数组是从0 开始的，偶数行为13579，奇数位02468）
				if (i % 2 != 0)    
				{
				   //设置偏移量为通道宽的一半
					DISTANCE = (int) WIDTH / 2;
				}
				//获取所有的二维数组坐标
				Dot dot = getDot(j, i);
				//检查当前点的坐标的状态
				switch (dot.getStatus()) 
				{
				//猫点所在的位置为红色
				case Dot.STATUS_IN:
					paint.setColor(0xFFFF0000);
					break;
			    //路障为黄色
				case Dot.STATUS_ON:
					paint.setColor(0XFFFFAA00);
					break;
				 //通道为灰色
				case Dot.STATUS_OFF:
					paint.setColor(0XFFA9A9A9);
					break;
				default:
					break;
				}
				//画圆
				canvas.drawOval(new RectF(dot.getX() * WIDTH + DISTANCE
						+ length, dot.getY() * WIDTH + OFFSET, (dot.getX() + 1)
						* WIDTH + DISTANCE + length, (dot.getY() + 1) * WIDTH
						+ OFFSET), paint);
			}
		}
		int left = 0;
		int top = 0;
		//偶数行cat的动态图片距离左边和上面的距离
		if (cat.getY() % 2 == 0) 
		{
			left = cat.getX() * WIDTH;
			top = cat.getY() * WIDTH;
		} 
		//奇数行cat的动态图片距离左边和上面的距离
		else 
		{
			left = (int) (WIDTH / 2) + cat.getX() * WIDTH;
			top = cat.getY() * WIDTH;
		}
		// 此处神经猫图片的位置是根据效果图来调整的
		cat_drawable.setBounds(left - WIDTH / 6 + length, top - WIDTH / 2
				+ OFFSET, left + WIDTH + length, top + WIDTH + OFFSET);
		cat_drawable.draw(canvas);
		//设置背景图片的位置
		background.setBounds(0, 0, SCREEN_WIDTH, OFFSET);
		//显示背景图
		background.draw(canvas);
		/*getHolder()获取Surfaceview对象，再通过unlockCanvasAndPost方法，
		通知系统Surface已经绘制完成，这样系统会把绘制完的内容显示出来*/
		getHolder().unlockCanvasAndPost(canvas);
	}
	
//1.2步：实现SurfaceView的Callback接口，对Surfaceview进行创建，销毁，改变时的情况进行监视
	Callback callback = new Callback()
	{
		//当Surface第一次创建后会立即调用该函数
		public void surfaceCreated(SurfaceHolder holder) 
		{
			redraw();
			//当创建时开启线程
			startTimer();
		}
		//当Surface被改变时会调用
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			WIDTH = width / (COL + 1);     //通道的宽度等于屏幕宽度除以列数加1（偶数行会比奇数行偏差一些位置）
			OFFSET = height - WIDTH * ROW - 2 * WIDTH;
			length = WIDTH / 3;
			SCREEN_WIDTH = width;
		}
		//当Surface被摧毁前会调用该函数
		public void surfaceDestroyed(SurfaceHolder holder) 
		{
			//停止线程
			stopTimer();
		}
	};

	// 开启定时任务
	private void startTimer() {
		timer = new Timer();
		timerttask = new TimerTask() {
			public void run() {
				gifImage();
			}
		};

		timer.schedule(timerttask, 50, 65);
	}

	// 停止定时任务
	public void stopTimer() {
		timer.cancel();
		timer.purge();
	}

	// 动态图
	private void gifImage() {
		index++;
		if (index > images.length - 1) {
			index = 0;
		}
		cat_drawable = getResources().getDrawable(images[index]);
		redraw();
	}

// 获取正确点击坐标的方法（圆点的坐标在界面上x代表行，y代表列，因为安卓的坐标原点在左上角，而二维数组的坐标点在右下角）
	private Dot getDot(int x, int y) 
	{
		//所以在二位数组中，x轴代表列，y轴代表行，这里返回一个正确的点击坐标，而不是圆点坐标
		//第一个[]表示列，第二个[]表示行，所以第一个对应圆点界面坐标的y,第二个对应x
		return matrix[y][x];     
	}
//------------------------------------------------------------------------------------------------------------

	
//-------------------------------------第二步：响应用户的交互（触屏）-----------------------------------------------
	
	
	// 触屏事件
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouch(View v, MotionEvent event) 
		{
			int x, y;
			if (event.getAction() == MotionEvent.ACTION_UP) {

		     //获取用户的点击坐标（设置超出游戏边界为不可点击状态）
				if (event.getY() <= OFFSET)  //如果没有超过上下边界范围的坐标，可以点击
				{
					return true;
				}
			    //设置y点坐标
				y = (int) ((event.getY() - OFFSET) / WIDTH);
			    //判断偶数行的坐标范围
				if (y % 2 == 0) 
				{
					if (event.getX() <= length
							|| event.getX() >= length + WIDTH * COL) 
					{
						return true;
					}
					x = (int) ((event.getX() - length) / WIDTH);
				} 
				//判断奇数行的坐标范围
				else {
					if (event.getX() <= (length + WIDTH / 2)
							|| event.getX() > (length + WIDTH / 2 + WIDTH * COL)) {
						return true;
					}
					x = (int) ((event.getX() - WIDTH / 2 - length) / WIDTH);
				}

				//如果获取的坐标不在游戏界面内（即界面下面这一块区域），则刷新游戏
				if (x + 1 > COL || y + 1 > ROW) 
				{
					AlertDialog.Builder dialog = new Builder(context);
					dialog.setTitle("更新障碍");
					dialog.setMessage("(#`O′)是否要更新障碍的位置");
					dialog.setCancelable(false);
					dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							initGame();
						}
					});
					dialog.setPositiveButton("取消", null);
					dialog.show();
				} 
			   //到达边界，设置不可点击
				else if (inEdge(cat)) 
				{
					return false;
				} 
			    //如果点击的坐标的状态为可点击的
				else if (getDot(x, y).getStatus() == Dot.STATUS_OFF) 
				{
					//即设置为不可点击状态
					getDot(x, y).setStatus(Dot.STATUS_ON);
					//然后猫点移动
					move();
				  //记录移动点数
					steps++;
				}
				// redraw();
			}
			return true;
		}

//--------------------------------------------------------------------------------------------------------------------------

	
//--------------------------------第三步：游戏逻辑的实现-----------------------------------------------------------------------

		
	//3.1步： 判断神经猫是否处于边界（四种情况：到达上边界，下边界，左边界，右边界）
 //到达右边界条件：x轴坐标等于列数；到达下边界条件：y轴坐标等于行数  
//到达左上边界的条件：x和y轴乘积为0（因为左边界的x轴为这列的第一个，所以坐标值为0；上边界的y轴为第一行，所以坐标值为0）
	private boolean inEdge(Dot dot) {
		if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL
				|| dot.getY() + 1 == ROW) 
		{
			return true;
		}
		return false;
	}
	

	 /*--------------------------------------------------------------------------------*/
	
// 3.2步：获取cat的相邻点方向的所有的坐标(以猫为中心，它可接触的为6个点，除了同一列的点，其余的点需要判断奇偶行)，dir为6个点方向，返回这个方向的坐标
	private Dot getNeighbour(Dot dot, int dir) 
	{
	//dir的值为1-6的数字
		switch (dir) 
		{
		//为猫点的正左列方向（左为小，右为大，上为小，下为大）
		case 1:
			return getDot(dot.getX() - 1, dot.getY());
		
//为猫点的右上方，奇数行列的坐标数是一样的，偶数行列的坐标数是小一个的，y坐标一致
//奇数行（例如猫点坐标为（3.3）那么右上方的坐标则为（3,2））	，偶数行（假如猫点坐标为（2,3）那么右上方的坐标为（1,2））
//规律：偶数行的列和行坐标小一个，奇数行的行坐标小一个
		case 2:
			if (dot.getY() % 2 == 0) 
			{   //奇数行求余，结果等于1，偶数行为0
				
				return getDot(dot.getX() - 1, dot.getY() - 1);
			} else {
				return getDot(dot.getX(), dot.getY() - 1);
			}
			
      //为猫点的左上方		
		case 3:
			if (dot.getY() % 2 == 0) 
			{
				//左上偶数行规律：行坐标小一个
				return getDot(dot.getX(), dot.getY() - 1);
			} 
			else 
			{
				//左上奇数行规律：列坐标大一个，行坐标依然小一个
				return getDot(dot.getX() + 1, dot.getY() - 1);
			}
			
			//为猫点的正右方	
		case 4:
			return getDot(dot.getX() + 1, dot.getY());
			
			//为猫点的右下方	
		case 5:
			if (dot.getY() % 2 == 0) 
			{
				//右下偶数行规律：行坐标大一个
				return getDot(dot.getX(), dot.getY() + 1);
			} 
			else 
			{
				//右下奇数行规律：列坐标大一个，行坐标大一个
				return getDot(dot.getX() + 1, dot.getY() + 1);
			}
			
			//为猫点的左下方
		case 6:
			if (dot.getY() % 2 == 0) 
			{
				//左下偶数行规律：列坐标小一个，行坐标大一个
				return getDot(dot.getX() - 1, dot.getY() + 1);
			} 
			else 
			{
				//左下奇数行规律：行坐标大一个
				return getDot(dot.getX(), dot.getY() + 1);
			}
		}
		return null;
	}
	
	/*-------------------------------------------------------------*/

	//3.3步： 获取cat在一个方向dir上的所有可移动距离点的总数（即圆点个数）
	private int getDistance(Dot one, int dir) 
	{
		//遇到路障距离为负数，没有则为正数
		int distance = 0;    //定义一个距离，即记录一个方向上可移动的距离点
		//如果到了边界，直接返回1，表示结束
		if (inEdge(one)) 
		{
			return 1;
		}
		//定义一个当前点对象
		Dot ori = one;
		Dot next;    //定义下一个点对象
		//循环一个方向上的所有点坐标，遇到障碍物时结束或者到达边缘
		while (true) 
		{
	//由当前点坐标查出相邻点的方向，赋给下一个点（即把1-6的方向给next）
    //有两种情况：第一：下一点的这个方向上有路障，第二种：下一个点这个方向上没有路障，最后可以抵达边缘		
			next = getNeighbour(ori, dir);
			
		//这为第一种情况：下一个点的方向上有路障
			if (next.getStatus() == Dot.STATUS_ON) 
			{
            // 即可返回可移动距离点数（值为0或者负数）
				return distance * -1;
			}
			//判断下一个点是否到达边缘
			if (inEdge(next)) 
			{
				//循环增加没有障碍的这个方向上的可移动距离点数
				distance++;
				//抵达到了边缘，则返回这个方向上的距离点数（也就是灰色圆点的个数）
				return distance;
			}
	 //每次循环距离加一个，表示可移动的距离点（这里为了计算在这个方向上遇到障碍之前的灰色圆点个数）
			distance++;
			
			//让当前点移动到下一个点，然后在这个点上继续查找方向，循环反复
			ori = next;
		}
	}

	// 移动cat至指定点方法
	private void moveTo(Dot dot) 
	{
		//移动到这个点，设置状态为猫点
		dot.setStatus(Dot.STATUS_IN);
		//获取移动前猫点的坐标，设置为可点击状态
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
		//把新的坐标赋给猫点
		cat.setXY(dot.getX(), dot.getY());
	}

	// cat的移动算法
//（只有两种情况：第一：这个方向可以到达边缘即认为可移动的路径 ；第二：这个方向有障碍物（障碍物分为身边障碍物和这个方向上的障碍物））
	private void move() 
	{
		//当前点到达边界
		if (inEdge(cat)) 
		{
			failure();
			return;
		}
 // 当前点周围6个点中的可用点的集合（可用点有两种情况,第一:可以通过这个点达到边界的可用点，第二：这个点的方向上有障碍物的可用点）
		Vector<Dot> available = new Vector<Dot>();
		
		// 记录上面第一种情况可用点的坐标
		Vector<Dot> direct = new Vector<Dot>();
		
		//记录可用点和可用方向的集合（后面要根据这个点坐标找到相应的方向）
		HashMap<Dot, Integer> hash = new HashMap<Dot, Integer>();
		
		//循环判断6个方向上是否有可用点
		for (int i = 1; i < 7; i++) 
		{
			//先判断第一个点，循环到第6个点
			Dot n = getNeighbour(cat, i);
			//如果当前方向为通道，即为可用点
			if (n.getStatus() == Dot.STATUS_OFF) 
			{
				//把当前点加入到可用点集合
				available.add(n);
				
				//把点和方向加入
				hash.put(n, i);
		 //如果这个可用点获取返回的距离点为正数，说明这个方向可以到达边界
				if (getDistance(n, i) > 0) 
				{
				//则把可用点的坐标加入集合
					direct.add(n);
				}
			}
		}
		
    //-----------开始对上面可用点进行处理---------------------
		//第一种：没有可用点
		if (available.size() == 0) 
		{
			//成功围住
			win();
		}
		//第二种：只有一个可用点
		else if (available.size() == 1) 
		{
			//直接移动过去
			moveTo(available.get(0));
		} 
	//第三种：direct集合中有多个可用点坐标（这里的可用点都为可以到达边界的可用点）
		else 
		{
			//定义一个最优路径的起始坐标点
			Dot best = null;
			//有可以到达边界路径
			if (direct.size() != 0) {
				int min = 20;   //用于储存可移动距离点数值
				for (int i = 0; i < direct.size(); i++) 
				{
					//如果这个可用点为到达边界的点坐标
					if (inEdge(direct.get(i))) 
					{
					 //这个点赋给best，不用再进行循环比较
						best = direct.get(i);
						break;
					} 
					else {
				 //获取这个可用点的方向上可移动距离点总数（即这个方向灰色圆的总数）
						int t = getDistance(direct.get(i),hash.get(direct.get(i)));
						if (t < min) 
						{
			//把当前这个方向上个数赋给 min，再与下一个方向的距离点数值比较，把小的赋给min， 直到选出总数最小的一条路径
							min = t;
							//把这个可用点赋给best
							best = direct.get(i);    
						}
					}
				}
			} 
			//第四种：所有方向上的路径都有障碍
			else {
				int max = 1;      //有路障的路径的距离点值不是0就是负数
				for (int i = 0; i < available.size(); i++) 
				{
			  //获取这个可用点的方向上遇到路障之前的可移动距离点总数（即遇到障碍之前的灰色圆总数）
					int k = getDistance(available.get(i),hash.get(available.get(i)));
					
			 //负数越小，即表示遇到路障之前的可移动距离点数越多，也就是灰色圆点个数越多,便是这种情况下的最佳路径
					if (k < max) 
					{
				 //把当前这个方向上个数赋给 max，再与下一个方向的距离点数值比较，把小的赋给max， 直到选出总数最小的一条路径	
						max = k;
						
						best = available.get(i);
					}
				}
			}
			//移动到这个最优路径的其实坐标点（因为每次移动都要判断当前方向的最佳路径，选出之后再进行移动1）
			moveTo(best);
		}

		//到达边缘，结束
		if (inEdge(cat)) 
		{
			failure();
		}
	}
//-----------------------------------------------------------------------------------------------------
	
	
//--------------------------------------第四步：游戏结束后的逻辑----------------------------------------------------------------
	// 通关失败
	private void failure() {
		AlertDialog.Builder dialog = new Builder(context);
		dialog.setTitle("通关失败");
		if (steps>20) 
		{
			dialog.setMessage("你让痴汉脸逃出二次元了，但是相信你尽力了٩(๑❛ᴗ❛๑)۶");
		}
		else if (steps<=10) 
		{
			dialog.setMessage("你让痴汉脸逃出二次元啦!  (｡ŏ_ŏ)亲，咱走点脑子可好？");
		} 
		else if (steps>10&&steps<=20) 
		{
			dialog.setMessage("你让痴汉脸逃出二次元啦!  (╥╯^╰╥)咱别光走脑子，不走心呐！");
		}
		dialog.setCancelable(false);
		dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				initGame();
			}
		});
		dialog.setPositiveButton("取消", null);
		dialog.show();
	}

	// 通关成功
	private void win() {
		AlertDialog.Builder dialog = new Builder(context);
		dialog.setTitle("通关成功");
		if (steps<=12) 
		{
			dialog.setMessage("你用" + (steps + 1) + "步捕捉到了痴汉脸耶,真是天才ヾ(◍°∇°◍)ﾉﾞ");
		}
		else if (steps>12&&steps<=20) 
		{
			dialog.setMessage("你用" + (steps + 1) + "步捕捉到了痴汉脸，好吧");
		} 
		else if(steps>20&&steps<30) 
		{
			dialog.setMessage("你用" + (steps + 1) + "步捕捉到了痴汉脸，(⊙o⊙)…你是不是被他色诱了？");
		}
		dialog.setCancelable(false);
		dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				initGame();
			}
		});
		dialog.setPositiveButton("取消", null);
		dialog.show();
	}
}

//------------------------------------------------------------------------------------------
	