package com.voidmain.service;

import java.awt.image.*;

public class Decryption
{
	public String decodeMessage(BufferedImage image) {
		
		String message="";
		
		int len = extractInteger(image, 0, 0);
		
		try 
		{
			
			if(len<500)
			{
			
				byte b[] = new byte[len];
				
				for(int i=0; i<len; i++)
				{
					
					System.out.println("in for");
				
					try {
						
						b[i] = extractByte(image, i*8+32, 0);
						
					} catch (Exception e) {
						
						System.out.println("in catch1");
						
						break;
						
					}
				}
				
				System.out.println("after for");
				
				message=new String(b);
			}
			else
			{
				return "";
			}
		}
		catch (Exception e) {
			
			System.out.println("in catch2");
			
			return "";
		}
		
		System.out.println("before return");
		
		return message; 
	}

	private int extractInteger(BufferedImage img, int start, int storageBit) {
		int maxX = img.getWidth(), maxY = img.getHeight(), 
				startX = start/maxY, startY = start - startX*maxY, count=0;
		int length = 0;
		for(int i=startX; i<maxX && count<32; i++) {
			for(int j=startY; j<maxY && count<32; j++) {
				int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
				length = setBitValue(length, count, bit);
				count++;
			}
		}
		return length;
	}

	private byte extractByte(BufferedImage img, int start, int storageBit) {
		int maxX = img.getWidth(), maxY = img.getHeight(), 
				startX = start/maxY, startY = start - startX*maxY, count=0;
		byte b = 0;
		for(int i=startX; i<maxX && count<8; i++) {
			for(int j=startY; j<maxY && count<8; j++) {
				int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
				b = (byte)setBitValue(b, count, bit);
				count++;
			}
		}
		return b;
	}
	
	private int getBitValue(int n, int location) {
		int v = n & (int) Math.round(Math.pow(2, location));
		return v==0?0:1;
	}

	private int setBitValue(int n, int location, int bit) {
		int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
		if(bv == bit)
			return n;
		if(bv == 0 && bit == 1)
			n |= toggle;
		else if(bv == 1 && bit == 0)
			n ^= toggle;
		return n;
	}
}