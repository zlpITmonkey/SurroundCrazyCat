package com.example.surroundcrazycat;


public class Dot {
	
	int x,y;     //记录当前点的x，y坐标
	int status;   //记录每个点的状态
	
	public static final int STATUS_ON = 1;    // 不可点击状态(通道)
	public static final int STATUS_OFF = 0;   // 可点击状态（障碍物）
	public static final int STATUS_IN = 9;    // 不可点击状态（猫）
	
	public Dot(int x, int y) 
	{
		super();
		this.x = x;
		this.y = y;
		//设置为不可点击状态
		status = STATUS_OFF;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setXY(int x,int y) {
		this.y = y;
		this.x = x;
	}
	
	
}
