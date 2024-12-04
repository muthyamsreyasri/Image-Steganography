package com.voidmain.servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.voidmain.pojo.AppForm;
import com.voidmain.service.AES;
import com.voidmain.service.Decryption;
import com.voidmain.service.Encryption;

@WebServlet("/VoidmainServlet")
public class VoidmainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	HttpServletRequest request;
	HttpServletResponse response;

	Object obj=null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.request=request;
		this.response=response;

		try {
			
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			
			if(isMultipart) {
				
				Object obj=new AppForm();
				Map<Object,List<String>> map=HttpRequestParser.parseMultiPartRequest(request,obj);
				List<String> list=map.get(obj);
				
				AppForm appForm=(AppForm)obj;
				appForm.setImage(list.get(0));
				
				String type=appForm.getType();
				
				String filepath = request.getServletContext().getRealPath("")+"/documents/";
				
				if(type.equals("encode"))
				{
					try
					{
						String actualMessage=appForm.getMessage();
						String encmessage=AES.encrypt(appForm.getKey(), appForm.getMessage());
						
						appForm.setMessage(AES.encrypt(appForm.getKey(), appForm.getMessage()));
						
						System.out.println(actualMessage+"\t"+encmessage);
						
						BufferedImage sourceImage = ImageIO.read(new File(filepath+appForm.getImage())); 
						
						int imageWidth = sourceImage.getWidth(), imageHeight = sourceImage.getHeight(),
								imageSize = imageWidth * imageHeight;
						
						System.out.println(appForm.getMessage()+"\t"+imageSize);
						
						if(appForm.getMessage().length() * 8 + 32 > imageSize) {
							
							response.sendRedirect("encode.jsp?status=Message is too long for the chosen image");
						}
						
						BufferedImage enBufferedImage = new Encryption().embedMessage(appForm,filepath);
						
					    ImageIO.write(enBufferedImage, "png",new File("C:\\Users\\sreya\\Desktop\\project\\encoded.png"));
					    
					    PrintWriter out = response.getWriter();
					    
						try {

							response.setContentType("text/html");  

							response.setContentType("APPLICATION/OCTET-STREAM");   

							response.setHeader("Content-Disposition","attachment; filename=\"" + appForm.getImage() + "\"");   

							FileInputStream fileInputStream = new FileInputStream("C:\\Users\\sreya\\Desktop\\project\\encoded.png");  

							int i;   

							while ((i=fileInputStream.read()) != -1) {  
								out.write(i);   
							}   

							fileInputStream.close();   

							out.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					catch(Exception e)
					{
						response.sendRedirect("encode.jsp?status=Operation Failed");
					}
				}
				else if(type.equals("decode"))
				{
					try {
						
						BufferedImage encodedImage = ImageIO.read(new File(filepath+appForm.getImage()));
						
						String encmessage= new Decryption().decodeMessage(encodedImage);
						String actual=AES.decrypt(appForm.getKey(),encmessage);
						
						if(actual!=null && !actual.equals(""))
						{
							
							response.sendRedirect("decode.jsp?status="+actual);
						}
						else
						{
							response.sendRedirect("decode.jsp?status=Unable to Extract Message");
						}
						
					} catch (Exception e) {
						
						response.sendRedirect("decode.jsp?status=Unable to Extract Message");
					}
					
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
