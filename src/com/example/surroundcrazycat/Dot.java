package com.example.surroundcrazycat;


public class Dot {
	
	int x,y;     //��¼��ǰ���x��y����
	int status;   //��¼ÿ�����״̬
	
	public static final int STATUS_ON = 1;    // ���ɵ��״̬(ͨ��)
	public static final int STATUS_OFF = 0;   // �ɵ��״̬���ϰ��
	public static final int STATUS_IN = 9;    // ���ɵ��״̬��è��
	
	public Dot(int x, int y) 
	{
		super();
		this.x = x;
		this.y = y;
		//����Ϊ���ɵ��״̬
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
